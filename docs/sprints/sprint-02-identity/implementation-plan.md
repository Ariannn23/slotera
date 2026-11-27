# Plan de implementación — Sprint 02

Estado: APPROVED | Versión: 1 | Base examinada: `3c3267e` | Aprobación: usuario, 2026-11-27

## Diseño y decisiones pendientes

Las decisiones numeradas del [README](README.md) se presentan como propuesta. La excepción de `/me` requiere resolución explícita: `API_CONVENTIONS.md` generaliza el encabezado de organización a los endpoints internos y `FLOWS.md` sitúa la autenticación antes del alta de empresa. No implementar ese tramo sin aceptar la excepción. ADR-004 permanece propuesto hasta aprobación humana; aprobar el plan debe confirmar también su adopción para este sprint.

### Proveedor y entorno

- Añadir Keycloak con realm de desarrollo reproducible, cliente SPA público con Standard Flow y PKCE S256, cliente o audiencia `slotera-api` y mapper que incluya esa audiencia solo en access tokens. Configurar los scopes o mappers necesarios para que el access token incluya los claims de correo requeridos, y verificar su presencia en tokens reales. Deshabilitar implicit flow y direct access grants en el cliente SPA. Configurar registro, verificación de email y recuperación.
- Usar PostgreSQL dedicado para datos de Keycloak y volumen propio, sin alterar el volumen existente de Slotera. Añadir Mailpit local para SMTP y comprobación de correos. Puertos de desarrollo configurables y ligados a loopback cuando corresponda. Importar realm en arranque limpio y documentar que una importación no actualiza un realm existente.
- Proponer Keycloak 26.8.2 con la imagen oficial actualizada `26.8.2-3` y `keycloak-js` 26.2.5, publicado por separado. Al consultar el 27-11-2026, Keycloak indicaba que sus imágenes 26.8.2 actualizadas incluyen un runtime corregido para CVE-2026-114938; verificar digest y runtime real en implementación y CI. No suponer que una etiqueta o una caché local prueban qué JVM se ejecuta. Usar `start-dev` y Mailpit únicamente en local/CI, y credenciales sintéticas fuera del repositorio. No desplegar ni modificar infraestructura productiva.
- Configurar un issuer canónico que coincida exactamente entre lo emitido y lo validado por API, tanto en navegador/host como en CI. La configuración real de dominio HTTPS y proxy de producción se resolverá antes del despliegue.

### API y datos

- Añadir el starter de Spring Boot 4 para OAuth2 resource server y las dependencias de test de seguridad gestionadas por el BOM; fijar versiones adicionales únicamente tras verificar compatibilidad. Crear `SecurityFilterChain` sin sesiones de servidor para la API Bearer. Mantener el perfil de producción sin documentación expuesta y rechazar configuraciones de issuer o audiencia ausentes.
- Validar firma mediante JWKS, algoritmo acordado, `iss`, `aud`, `exp`, `nbf` cuando exista y `sub`. Definir mapper de audiencia para el access token y verificar con Keycloak real que el ID token no pueda usarse contra la API. La audiencia debe estar configurada en API y proveedor de manera coherente. Ante indisponibilidad de descubrimiento o JWKS no aceptar credenciales no verificadas; registrar comportamiento y revalidar sin revelar tokens.
- Definir reglas por ruta: health público; `/v3/api-docs` y Swagger solo donde estén habilitados en `dev`/`test`; `/api/v1/me` y resto de rutas privadas autenticadas. Mantener las rutas de negocio futuras y la autorización por organización para sus sprints. Configurar CORS con orígenes explícitos de SPA; no confiar en roles ni en encabezados de organización proporcionados por cliente.
- Integrar 401/403 con `ProblemDetail` y encabezado `WWW-Authenticate` cuando corresponda. Comprobar la interacción con `ApiExceptionHandler`: su captura genérica de excepciones puede interferir con errores de seguridad lanzados dentro de MVC; adaptar y probar el camino real del filtro y de autorización. No devolver trazas, JWT, contraseñas ni referencias técnicas al usuario.
- Crear migración Flyway V2 de `users` con `UUID`, `issuer`, `subject`, email verificado, estado y timestamps, y restricción única `(issuer, subject)`. Mantener V1 intacta. Crear o recuperar la fila en la primera consulta autenticada de `/me` de forma atómica, incluso con solicitudes simultáneas. Nunca unir identidades por coincidencia de correo. Propuesta: exigir claim `email_verified=true` y email presente para crear o actualizar email; rechazo seguro ante claim ausente, falso o inconsistente, sin sobrescribir un email verificado con uno no verificado. El estado local y desactivación requieren comprobación; no inventar roles de empresa.
- Contrato propuesto `GET /api/v1/me` con Bearer válido y sin encabezado de organización. Respuesta 200 `{"id":"uuid","email":"persona@example.test","emailVerified":true}`. Identidad faltante/inválida produce 401; identidad válida que incumple la política de correo o estado produce 403. No mostrar issuer, subject, roles de empresa, contraseña ni tokens. Documentar y probar respuestas de error. La obtención del perfil desde un GET puede crear la fila local; documentar esa escritura idempotente y su transacción.
- Migraciones: probar V1→V2 y BD vacía en PostgreSQL real. Preservar usuarios existentes al reintentar o ante errores; no eliminar datos ni usar migraciones productivas. Preparar recuperación mediante respaldo y reversión con migración posterior revisada; no editar migraciones ya aplicadas.

### React y sesión

- Sustituir la pantalla de Vite por una interfaz mínima para entrar, registrarse, recuperar contraseña, ver perfil y salir. Iniciar la autenticación y la recuperación en Keycloak; no pedir ni reenviar contraseñas a la API. Mantener tokens de acceso y renovación en memoria mediante el adaptador, sin persistirlos en `localStorage`, `sessionStorage` ni logs. La gestión de parámetros temporales propios del protocolo queda a cargo de la biblioteca.
- Renovar cuando proceda; si la sesión expira o falla la renovación, mostrar estado claro y pedir iniciar sesión nuevamente. Al recargar, verificar el comportamiento real de sesión de Keycloak y restricciones de cookies; no prometer persistencia universal. Al salir, limpiar estado del cliente y terminar sesión OIDC. Un access token ya emitido puede seguir siendo válido hasta expirar; documentar y probar este límite sin afirmar revocación inmediata.
- La SPA usa orígenes y redirect URIs precisos configurables. No introducir secretos de cliente en navegador. Ejercicio de aprendizaje reservado al usuario: identificar `iss`, `sub`, `aud` y `exp` en un JWT sintético y explicar qué comprueba la API; no enviar tokens reales ni credenciales. Revisar su respuesta y dificultades.

## Archivos previstos

| Ruta | Cambio | Motivo |
|---|---|---|
| `compose.yaml`, `.env.example`, `infrastructure/keycloak/` | Modificar o crear | Proveedor, BD de identidad, Mailpit y realm local sin secretos reales. |
| `.github/workflows/ci.yml` | Modificar | Servicios y pruebas reales de seguridad, migración y navegador según viabilidad. |
| `apps/api/pom.xml`, `apps/api/src/main/resources/` | Modificar | Seguridad, issuer/audiencia por entorno y V2 de Flyway. |
| `apps/api/src/main/java/com/slotera/identity/` y `shared/` | Crear o modificar | Perfil, persistencia, filtros, errores y README de módulo. |
| `apps/api/src/test/` | Crear o modificar | Seguridad, concurrencia, migraciones e integración OIDC. |
| `apps/web/package.json`, lockfile, `apps/web/src/` | Modificar | Adaptador OIDC, interfaz mínima, pruebas y README de módulo de identidad. |
| `README.md`, `apps/api/README.md`, `apps/web/README.md`, `docs/api/`, `docs/security/`, `docs/database/`, `docs/adr/ADR-004-authentication.md` | Actualizar al implementar | Contratos y comportamiento observado. |
| `docs/sprints/SPRINT-02.md`, `docs/sprints/sprint-02-identity/` | Completar | Plan, evidencia y acta de cierre. |

## Secuencia, comandos y recuperación

1. Después de aprobación expresa de plan y decisiones, verificar `git status --short --branch`, `git fetch origin` y que `develop` siga en el SHA esperado. Crear una copia aislada sin sobrescribir el checkout principal y generar `sprint/02-identity` desde `origin/develop`; copiar los borradores aprobados y confirmar su integridad. Si la base cambió o los borradores locales entran en conflicto, detener y revisar.
2. Verificar versiones oficiales y el runtime de la imagen; configurar servicios y realm reproducibles. `podman compose config --quiet` valida YAML y `podman compose up -d` inicia los servicios locales, sin borrar volúmenes. `podman compose ps` comprueba su estado.
3. Implementar migración, identidad local, JWT y reglas de seguridad, con tests en incrementos pequeños. Añadir cliente React y completar el flujo real con Keycloak y Mailpit. Registrar las decisiones aprobadas y actualizar README por módulo.
4. Ejecutar `./mvnw.cmd clean verify` en `apps/api`; revisar reportes y asegurar que las pruebas que requieren Keycloak/PostgreSQL real ejecuten realmente en CI. En `apps/web`, ejecutar `pnpm run typecheck`, `lint`, `test` y `build`; ejecutar pruebas de navegador del flujo OIDC y registrar entorno, SHA, resultado y datos sintéticos. Verificar migración limpia y V1→V2, reinicio y rollback ante fallo.
5. Verificar permisos, seguridad, flujos, cambios y ausencia de secretos. Crear commits atómicos en español, publicar rama y abrir PR hacia `develop`; comprobar CI del candidato. Solicitar aprobación humana del resultado y autorización de merge por separado.

Ante fallo, conservar el volumen de Keycloak y los datos de usuarios. No borrar volúmenes ni regenerar realms automáticamente. El retorno de código/configuración se prepara con commit inverso compatible con el esquema. Antes de revertir datos, producción o migraciones se requiere una estrategia de recuperación aprobada y respaldo comprobado.

## Estimación y fuentes

Roadmap: 10 h. Estimación inicial propuesta 18–30 h para incluir configuración, flujo de registro y recuperación, UI mínima, pruebas de tokens reales, aprendizaje y evidencia. Reestimar tras configurar los servicios; no reducir pruebas para cumplir el calendario.

- [Spring Security: validación JWT y audiencia](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Keycloak: adaptador JavaScript, PKCE y memoria](https://www.keycloak.org/securing-apps/javascript-adapter)
- [Keycloak: importación de realms](https://www.keycloak.org/server/importExport)
- [Keycloak: configuración de correo](https://www.keycloak.org/docs/latest/server_admin/)
- [Keycloak: CVE-2026-114938 y runtime corregido](https://www.keycloak.org/2026/11/java4shell-vulnerability-cve-2026-114938)
- [Keycloak: descargas y versiones independientes](https://www.keycloak.org/downloads)

## Aprobación previa

Estado: APPROVED | Aprobador: usuario | Fecha: 2026-11-27 | Referencia: «Apruebo el plan y las decisiones tomadas, puedes comenzar la implementacion» | Versión aprobada: 1. Incluye ADR-004, la excepción de `/me` y las políticas descritas. La aprobación del plan no equivale a autorización de merge.

## Decisiones y evidencia de implementación

- Imagen local `26.8.2-3` fijada por digest `sha256:75b5c435a83b530caba5923922f93d47b2e3f8b4ad9e79200581122996653e55`; JVM observada Red Hat OpenJDK 21.0.13.1. La aplicación usa `keycloak-js` 26.2.5.
- El realm añade los scopes `basic`, `profile` y `email`. La primera prueba real mostró que sin `basic` el token no incluía `sub`; se corrigió el archivo y se confirmó en una importación separada que `basic` está asignado. En el realm de desarrollo existente se aplicó el cambio por API administrativa sin borrar usuarios.
- La SPA usa un control periódico de renovación y una interfaz mínima; Keycloak 26.8.2 muestra sus pantallas de registro y recuperación en inglés. La plantilla predeterminada intentaba asociar un evento a un campo de contraseña ausente cuando esta se configura después de verificar el correo. Se añadió un tema local que hereda de `keycloak.v2` y limita el cambio a comprobar la existencia del campo. La prueba de navegador se repitió sin errores de JavaScript. El override proviene de Keycloak 26.8.2 y debe revisarse al actualizar el proveedor.
- El desarrollador explicó correctamente `iss`, `sub`, `aud` y `exp` en el ejercicio. Se revisó que `exp` representa segundos desde la época Unix y que la firma debe verificarse antes de confiar en los claims.
