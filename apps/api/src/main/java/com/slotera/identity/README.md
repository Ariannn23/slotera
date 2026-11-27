# Identidad

El módulo valida access tokens JWT emitidos por Keycloak con firma RS256, emisor, audiencia, expiración, inicio de validez, sujeto y tipo `Bearer`. La clave pública se obtiene del endpoint JWKS del emisor configurado. La API no almacena contraseñas ni tokens.

`GET /api/v1/me` requiere un token válido con email presente y verificado. No requiere organización seleccionada. Devuelve `id`, `email` y `emailVerified`. La primera consulta crea un usuario local; `(issuer, subject)` es único. Un usuario inactivo o sin correo verificado recibe 403. Credenciales ausentes o inválidas reciben 401 y `WWW-Authenticate: Bearer`. No se conceden permisos de organización por claims de cliente.

Los errores de seguridad no incluyen credenciales. `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`, `OIDC_ISSUER`, `OIDC_AUDIENCE` y `WEB_ORIGIN` se configuran por entorno. Producción requiere valores explícitos y HTTPS para emisor y origen. `application.properties` contiene valores de desarrollo locales.

La migración Flyway V2 crea `users`. `UserProfileService` ejecuta un upsert transaccional con unicidad en PostgreSQL; las cuentas no se vinculan por correo. Las pruebas `JwtSecurityIntegrationTest` usan una clave RSA y JWKS sintéticos, PostgreSQL real y transacciones independientes. La prueba de navegador de `apps/web/e2e` verifica tokens de Keycloak real. Ejecutar `./mvnw.cmd clean verify` desde `apps/api` con PostgreSQL disponible.

Logout termina la sesión y limpia el cliente, pero un access token firmado previamente puede permanecer válido hasta expirar. El control por membresía y permisos pertenece a Sprint 03. Ver ADR-004 y la matriz del Sprint 02.
