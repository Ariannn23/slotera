# ADR-004 — Identidad externa

Estado: aprobado por el usuario el 27-11-2026 como parte del plan del Sprint 02.

## Decisión
Keycloak OIDC + Spring Security resource server.

## Alternativas
Autenticación propia: más riesgo y mantenimiento.

## Consecuencias
Mayor infraestructura; sesión SPA, claims, alta y revocación pendientes.
