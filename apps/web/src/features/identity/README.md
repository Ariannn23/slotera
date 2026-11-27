# Identidad en la web

Este módulo inicia sesión mediante Keycloak OIDC Authorization Code + PKCE S256, registra usuarios a través del proveedor y consulta `/api/v1/me`. Keycloak gestiona la verificación de correo, las contraseñas y su recuperación. Las credenciales no pasan por la API de Slotera.

El adaptador mantiene access token y refresh token en memoria. El módulo no los guarda en almacenamiento persistente, ni los escribe en logs. La renovación se comprueba al consultar perfil y mientras la pantalla está activa. La salida limpia el estado del cliente y redirige al cierre de sesión OIDC. Un access token ya emitido puede seguir siendo válido hasta que expire.

Configurar `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, `VITE_KEYCLOAK_CLIENT_ID` y `VITE_API_URL`; los valores públicos nunca deben incluir secretos. El realm local acepta únicamente el origen y redirección documentados. En caso de acceso sin correo verificado, el servidor rechaza el perfil. La autorización por organización corresponde al Sprint 03.

Pruebas desde `apps/web`: `pnpm run typecheck`, `pnpm run lint`, `pnpm run test` y `pnpm run build`. Los flujos OIDC requieren Keycloak y navegador real.
