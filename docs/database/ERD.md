# Modelo entidad-relación conceptual

```mermaid
erDiagram
 USERS ||--o{ MEMBERSHIPS : belongs
 ORGANIZATIONS ||--o{ MEMBERSHIPS : includes
 ORGANIZATIONS ||--o{ INVITATIONS : issues
 ORGANIZATIONS ||--o{ BRANCHES : owns
 ORGANIZATIONS ||--o{ RESOURCE_CATEGORIES : defines
 BRANCHES ||--o{ RESOURCES : contains
 RESOURCE_CATEGORIES ||--o{ RESOURCES : classifies
 RESOURCES ||--o{ RESOURCE_IMAGES : has
 RESOURCES ||--o{ AVAILABILITY_RULES : defines
 RESOURCES ||--o{ AVAILABILITY_EXCEPTIONS : overrides
 RESOURCES ||--o{ RESOURCE_OCCUPANCIES : occupies
 ORGANIZATIONS ||--o{ CUSTOMERS : owns
 CUSTOMERS ||--o{ BOOKINGS : requests
 RESOURCES ||--o{ BOOKINGS : receives
 BOOKINGS o|--o{ RESOURCE_OCCUPANCIES : allocates
 BOOKINGS ||--o{ BOOKING_STATUS_HISTORY : records
 ORGANIZATIONS ||--o{ BOOKING_POLICIES : configures
 ORGANIZATIONS ||--o{ NOTIFICATIONS : sends
 ORGANIZATIONS ||--o{ AUDIT_LOGS : records
 ORGANIZATIONS ||--o{ IDEMPOTENCY_KEYS : scopes
```

Modelo preliminar. Toda entidad de negocio requiere organización coherente; usar claves foráneas compuestas cuando corresponda. Una ocupación puede representar mantenimiento sin reserva. El historial de reprogramación debe conservar intervalos anteriores. El diagrama no constituye DDL ejecutable.
