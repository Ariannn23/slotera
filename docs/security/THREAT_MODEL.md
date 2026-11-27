# Seguridad y amenazas

Keycloak gestiona credenciales, verificación y recuperación; Spring Security valida issuer, audiencia, firma y expiración de JWT. OIDC Authorization Code + PKCE para SPA. Identidad interna ligada a issuer/subject. Nunca confiar en roles o organización enviados por cliente sin verificación.

Sprint 02 implementa Authorization Code con PKCE S256 y mantiene los tokens de acceso y renovación en memoria del adaptador. El resource server valida firma RS256, emisor, audiencia, expiración, `nbf`, sujeto y tipo `Bearer`; requiere correo verificado para `/me`. La revocación de sesión no invalida inmediatamente un access token ya emitido que el resource server valide localmente. El token permanece aceptable hasta su expiración. La autorización por membresía se implementará en Sprint 03.

| Amenaza | Control |
|---|---|
| Acceso no autorizado | Autenticación y autorización por caso de uso |
| BOLA/IDOR y fuga entre empresas | Consultas acotadas, membresía, FK coherentes, pruebas cruzadas |
| Inyección SQL | Parámetros y validación |
| Fuerza bruta | Rate limiting y políticas Keycloak |
| XSS | Escape, CSP y validación de contenido |
| CSRF | Protección si se usan cookies; CORS restringido |
| Robo de credenciales | HTTPS, sesiones seguras, rotación de secretos |
| Duplicados y abuso | Idempotencia, límites y exclusión SQL |
| Enlaces públicos | Tokens aleatorios, hash en BD, expiración, alcance limitado, revocación |
| Archivos maliciosos | Tipo real, tamaño, nombres seguros, almacenamiento privado cuando corresponda |
| Exposición de secretos | Variables, CI secrets, logs redaccionados |
| Exposición de métricas | Actuator y Grafana restringidos |

Auditar acciones críticas sin tokens, contraseñas ni PII innecesaria. SUPER_ADMIN no tiene acceso ordinario a datos privados; definir soporte excepcional explícito si se requiere. Probar manipulación de IDs y cambio de organización. Política de retención y privacidad pendiente antes de uso comercial.
