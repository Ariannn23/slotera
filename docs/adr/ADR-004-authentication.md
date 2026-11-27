# ADR-004 — Identidad externa

Estado: aprobado por el usuario el 27-11-2026 como parte del plan del Sprint 02.

## Decisión
Keycloak OIDC + Spring Security resource server.

## Alternativas
Autenticación propia: más riesgo y mantenimiento.

## Consecuencias
Mayor infraestructura. La SPA usa Authorization Code con PKCE S256 y mantiene tokens en memoria. La API valida firma, emisor, audiencia, vigencia, sujeto y tipo de token. La identidad local se vincula por `(issuer, subject)` en la primera llamada autenticada a `/me`, que no requiere organización. El correo debe estar verificado. El logout no garantiza invalidación inmediata de access tokens ya emitidos; su tiempo de vida local se configura en cinco minutos. La autorización por membresía corresponde al Sprint 03. Registro y recuperación se delegan a Keycloak.
