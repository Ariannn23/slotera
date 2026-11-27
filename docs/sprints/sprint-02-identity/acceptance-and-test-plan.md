# Aceptación y plan de pruebas — Sprint 02

Entorno, commit candidato y evidencia: pendientes. Las siguientes políticas y respuestas son propuestas para aprobación.

| ID | HU | Dado | Cuando | Entonces | Estado |
|---|---|---|---|---|---|
| CA-01 | HU-01 | Keycloak y Mailpit locales configurados | Una persona se registra y verifica correo | El enlace de verificación funciona y permite continuar; se prueba la política de email no verificado. | PENDING |
| CA-02 | HU-01 | Cuenta verificada | Entra con Authorization Code + PKCE y cierra sesión | La SPA obtiene sesión, puede consultar `/me` y limpia su estado al salir. | PENDING |
| CA-03 | HU-02 | Cuenta de prueba y SMTP local | Se solicita recuperación y se completa el enlace | El correo llega a Mailpit y la nueva contraseña permite entrar. Se prueba enlace inválido o vencido. | PENDING |
| CA-04 | HU-03 | Primera petición autenticada con email verificado | Se llama `/me` sin organización | Devuelve el perfil propuesto y crea una única identidad local por issuer/subject, aun con solicitudes concurrentes. | PENDING |
| CA-05 | HU-03, HU-04 | API protegida | Se envían tokens válido, ausente, malformado, vencido, con firma/emisor/audiencia incorrectos o ID token | Solo el access token válido y adecuado permite acceso; 401 y encabezado Bearer ante credencial inválida. | PENDING |
| CA-06 | HU-01 | Sesión iniciada o finalizada | Se recarga, renueva o sale | Comportamiento observado y documentado; no persistir access/refresh tokens. Verificar que un access token emitido pueda conservar validez hasta su expiración. | PENDING |
| CA-07 | HU-04 | Credenciales y autorizaciones sintéticas | Faltan claims obligatorios, email no está verificado, usuario está inactivo o se deniega permiso de fixture | Rechazo seguro y distinción 401/403; respuesta y logs sin secretos. | PENDING |
| CA-08 | HU-05 | PostgreSQL V1 y BD vacía | Se aplica V2 y se repite arranque | Migración e integridad pasan; dos solicitudes simultáneas no duplican identidad; fallo no deja una fila parcial. | PENDING |
| CA-09 | HU-05 | Copia limpia y CI | Se levantan servicios y se ejecutan suites reales | Flujo OIDC, correo, migración, API y web reproducibles; reportes y CI del commit candidato registrados. | PENDING |

## Pruebas automatizadas

- JWT: firma, issuer, audience, `exp`, `nbf` futuro, claims faltantes, token de otro cliente, ID token y manipulación. Probar validadores con claves y expiraciones sintéticas y también tokens firmados por un Keycloak real; `jwt()` de MockMvc por sí solo no demuestra firma ni validación.
- Permisos y errores: 401/403, Bearer challenge, health y documentación de desarrollo, ausencia de bypass mediante `X-Organization-Id`, excepción de seguridad dentro de MVC y no exposición de tokens, contraseñas o PII sintética en respuesta y logs.
- Identidad: primera creación, lecturas sucesivas, actualización de correo verificado, `email_verified` falso o ausente, cuenta inactiva, igual correo con dos subjects sin asociación automática, concurrencia con transacciones independientes y restricciones en PostgreSQL real.
- Migración: V1→V2 y base vacía, reinicio, constraint único y fallo que no deje registro parcial. Mantener pruebas y comportamiento del Sprint 01.
- Frontend: estados sin sesión, ingreso, perfil, renovación fallida y salida; sin persistencia de tokens en almacenamiento del navegador. Navegador real con Keycloak, PKCE, usuario/correo sintéticos y Mailpit; verificar redirect, verificación, recuperación y manejo de cancelación. El acceso al contenido de correos de prueba debe evitar registrar enlaces o tokens en evidencia pública.
- Resiliencia: JWKS o emisor no disponible y claves rotadas, validación falla de manera segura cuando no hay clave válida. Distinguir funcionamiento con claves cacheadas de una validación nueva. No afirmar revocación inmediata de JWT por logout.

## Verificación manual y aprendizaje

En local, levantar servicios y realizar registro, verificación y recuperación con datos sintéticos. Comprobar `/me` sin `X-Organization-Id`, health y acceso a documentación solo en perfiles acordados. Revisar interfaz a 375, 768 y 1280 px, teclado, errores y consola sin warnings o errores de React. No registrar capturas de datos sensibles. El desarrollador examina un JWT ficticio y explica `iss`, `sub`, `aud` y `exp` con acompañamiento.

## Ejecución y riesgos

| Fecha | Comando y directorio | SHA / entorno | Resultado | Estado | Evidencia |
|---|---|---|---|---|---|
| — | Pendiente | — | — | PENDING | — |

## Evidencia local parcial — 27-11-2026

- PostgreSQL 17, Keycloak 26.8.2-3 y Mailpit iniciados con Podman Compose y volúmenes separados. El contenedor de Keycloak informó OpenJDK Red Hat 21.0.13.1. El realm limpio incluyó los scopes `basic` y `email`.
- Se aplicó V2 sobre la base de desarrollo con V1 y se verificaron V1 y V2 exitosas en una base vacía separada. La prueba transaccional y concurrente de perfiles está implementada; su ejecución final queda por registrar con el SHA candidato.
- La prueba Playwright con Keycloak real completó registro, correo de verificación, contraseña, acceso y `/me`, renovación al avanzar reloj, logout, recuperación, nuevo acceso, rechazo de ID token y enlace inválido. Comprobó que un access token emitido sigue siendo válido tras logout hasta expirar, y revisó almacenamiento y 375/768/1280 px. Un aviso de la plantilla de Keycloak queda documentado en el plan. No se guardan tokens ni enlaces como evidencia versionada.
- La suite local antes de las últimas pruebas añadidas pasó con 16 pruebas API, ninguna omitida; web pasó typecheck, lint, una prueba Vitest y build. Se repetirá `clean verify` para el commit candidato.
- El ejercicio del desarrollador identificó correctamente emisor, sujeto, audiencia y vencimiento. Se explicó la representación de `exp` y la validación de firma.
- Pendientes: CI del PR, verificación final de regresiones, evaluación explícita de indisponibilidad/rotación JWKS y registro de resultados en esta matriz. Las pruebas locales se realizaron en la copia aislada, con la API en 18089 y PostgreSQL de prueba en 55435.

Revisar reportes efectivos, cantidades y pruebas omitidas. Si no hay navegador, contenedor, correo, CI o acceso al proveedor, marcar BLOCKED y conservar las demás evidencias. No se declaran probadas membresías ni aislamiento multiempresa hasta Sprint 03. Estados: PENDING, PASS, FAIL, BLOCKED y NOT_APPLICABLE con motivo.
