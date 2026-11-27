# Sprint 02 — Identidad

Estado: IN_PROGRESS | Semana orientativa: 3 | Presupuesto del roadmap: 10 h
Base examinada: `develop` en `3c3267e` | Rama prevista: `sprint/02-identity`
[Ficha del roadmap](../SPRINT-02.md)

## Objetivo y aprendizaje

Permitir que una persona se registre, verifique su correo, inicie y cierre sesión, recupere su contraseña y consulte su perfil local. La API debe aceptar exclusivamente tokens válidos para Slotera en sus rutas privadas. Aprender OIDC, Authorization Code + PKCE, JWT, firma, emisor, audiencia, Spring Security y migraciones.

## Alcance propuesto

Keycloak y correo de prueba en entorno local y CI; configuración reproducible del realm y clientes; Spring Security como resource server; validación de JWT; persistencia de identidad local; `GET /api/v1/me`; interfaz mínima React de autenticación y perfil; recuperación, registro y verificación gestionados por Keycloak; pruebas automatizadas y manuales; documentación, migración y README de identidad. Los detalles y dependencias son propuestas sujetas a aprobación.

La gestión de organizaciones, membresías, roles por empresa e invitaciones corresponde a Sprint 03. Se verificará que este sprint no conceda acceso a datos multiempresa: esos endpoints todavía no existen. El diseño visual definitivo y el despliegue productivo quedan fuera del sprint.

## Historias

| ID | Como | Quiero | Para | Trazabilidad | Criterios |
|---|---|---|---|---|---|
| HU-01 | Persona usuaria | Registrarme, verificar correo, iniciar y cerrar sesión | Acceder a Slotera | RF-01, FLOWS | CA-01, CA-02, CA-06 |
| HU-02 | Persona usuaria | Recuperar mi contraseña por correo | Recuperar acceso | RF-01 | CA-03 |
| HU-03 | Persona autenticada | Consultar mi perfil local | Identificar mi cuenta antes de crear una empresa | RF-01, API `/me` | CA-04, CA-05 |
| HU-04 | Responsable técnico | Validar tokens y rechazar credenciales inválidas | Proteger la API | RNF-03, ADR-004 propuesto | CA-05, CA-07 |
| HU-05 | Desarrollador | Reproducir identidad y pruebas en local y CI | Detectar regresiones | RNF-08, RNF-11 | CA-08, CA-09 |

## Decisiones propuestas para aprobación

1. Aprobar ADR-004 para Keycloak OIDC y Spring Security resource server, manteniendo el monolito modular. Actualizar el ADR en implementación con fecha, versión y consecuencias.
2. Usar Authorization Code + PKCE S256 para React, con tokens en memoria mediante el adaptador OIDC. Configurar verificación de correo para el registro y recuperación mediante Keycloak y correo de prueba. La sesión y el comportamiento al recargar se verifican en navegador.
3. Crear un cliente público para la SPA y una audiencia separada de API. El resource server valida firma, emisor, audiencia, expiración y `nbf` si aparece; exige `iss`, `sub`, `aud` y `exp`, y rechaza un ID token como credencial de API. El tiempo de vida del access token se propone en 5 minutos, sujeto a revisión al verificar Keycloak.
4. Resolver la discrepancia del contexto multiempresa con una excepción explícita: `/api/v1/me` requiere identidad válida, no requiere `X-Organization-Id` ni membresía, porque el alta de empresa ocurre después de autenticarse. Sprint 03 definirá las excepciones para creación/listado de organizaciones y la matriz de permisos. Ningún rol enviado por cliente concede privilegios de empresa.
5. Vincular usuario local por `(issuer, subject)`, único en base de datos. Propuesta de alta local en el primer `/me` autenticado, con inserción idempotente ante concurrencia y sin vinculación automática por email. El perfil devuelve `id`, `email` y `emailVerified`, sin contraseña ni tokens. Un email no verificado no se considera prueba de propiedad; el comportamiento cuando falte el claim se define en el plan.
6. Proponer `health` público y documentación interactiva accesible solo en `dev`/`test`, como Sprint 01. Las demás rutas de API requieren autenticación; permisos finos por organización se posponen a Sprint 03. La API responderá 401 con encabezado Bearer ante identidad ausente o inválida y 403 ante autorización denegada, sin detallar tokens.

## Dependencias y riesgos

Sprint 01 está cerrado. Hoy no existe integración de seguridad, perfil de usuario ni proveedor de identidad. Spring Boot 4.0.10, PostgreSQL 17 y Flyway están implementados. La versión estable consultada el 27-11-2026 es Keycloak 26.8.2 y el adaptador JavaScript tiene ciclo de publicación independiente; se propone verificar y fijar versiones e imagen con runtime corregido antes de implementarlas.

Keycloak advierte que el cierre de sesión no invalida inmediatamente los access tokens ya emitidos; la validación JWT local los aceptará hasta su vencimiento si siguen siendo válidos. La revocación inmediata y el acceso excepcional de soporte requieren decisión posterior. La estimación del roadmap de 10 h es ajustada para un flujo completo con correo, navegador, persistencia y pruebas; previsión preliminar 18–30 h, sujeta al tiempo real y al aprendizaje.

El checkout principal está en la rama anterior y tiene cambios locales previos en fichas y archivos no versionados. Los borradores de este sprint se preparan allí sin tocar esos cambios. Tras aprobación, se propone implementar desde una copia aislada de `develop` y trasladar estos documentos aprobados, preservando el checkout principal.

## Seguimiento

- [x] Plan y decisiones aprobados
- [x] Rama e implementación
- [ ] Verificación y evidencia
- [ ] Resultado aprobado, merge y cierre verificados

[Plan de implementación](implementation-plan.md) · [Aceptación y pruebas](acceptance-and-test-plan.md) · [Acta](approval.md)
