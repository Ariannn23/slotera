# Diccionario de datos preliminar

UUID como identificadores, TIMESTAMPTZ para instantes, NUMERIC(12,2) y BigDecimal para dinero. Índices por organización, recurso, estado y fecha según consultas; claves compuestas para integridad multitenant. Flyway versiona cambios.

| Tabla | Campos/propósito principales |
|---|---|
| users | id, issuer, subject, email, email_verified, status, created_at, updated_at; `(issuer, subject)` único |
| organizations | id, slug único, nombre, razón social, correo, teléfono, logo, zona, moneda, estado |
| memberships | organization_id, user_id, role, status; par único |
| invitations | organization_id, email, role, token_hash, expires_at, status |
| branches | organization_id, nombre, dirección, zona, estado, horario general |
| resource_categories | organization_id, nombre |
| resources | organization_id, branch_id, category_id, nombre, descripción, capacidad, tarifa, duraciones, granularidad, buffer, confirmación, estado |
| resource_images | organization_id, resource_id, object_key, orden |
| availability_rules | organization_id, resource_id, día de semana, inicio/fin local |
| availability_exceptions | organization_id, resource_id, fecha local, tipo, intervalos especiales |
| resource_occupancies | organization_id, resource_id, booking_id opcional, tipo, starts_at, ends_at, active |
| customers | organization_id, nombre, correo, teléfono, notas, timestamps |
| bookings | ver tabla detallada |
| booking_status_history | organization_id, booking_id, estado previo/nuevo, actor, motivo, fecha, cambios de intervalo |
| booking_policies | organization_id, alcance, anticipación, expiración, cancelación, aprobación |
| notifications | organization_id, destinatario, tipo, status, attempts, next_attempt_at |
| audit_logs | organization_id, actor, acción, entidad, cambios filtrados, correlation_id, fecha |
| idempotency_keys | organization_id, actor/ámbito, key, request_hash, resultado, expiración |

## bookings
| Campo | Tipo |
|---|---|
| id | UUID |
| organization_id | UUID |
| resource_id | UUID |
| customer_id | UUID |
| booking_code | VARCHAR |
| starts_at | TIMESTAMPTZ |
| ends_at | TIMESTAMPTZ |
| status | VARCHAR |
| source | VARCHAR |
| hourly_rate | NUMERIC(12,2) |
| total_amount | NUMERIC(12,2) |
| currency | CHAR(3) |
| expires_at | TIMESTAMPTZ nullable |
| cancelled_at | TIMESTAMPTZ nullable |
| cancellation_reason | TEXT nullable |
| created_by | UUID nullable para portal |
| created_at | TIMESTAMPTZ |
| updated_at | TIMESTAMPTZ |
| version | BIGINT |

## Restricción conceptual de ocupaciones
```sql
CREATE EXTENSION IF NOT EXISTS btree_gist;
ALTER TABLE resource_occupancies
ADD CONSTRAINT valid_interval CHECK (ends_at > starts_at);
ALTER TABLE resource_occupancies
ADD CONSTRAINT no_overlapping_occupancy
EXCLUDE USING gist (
  organization_id WITH =,
  resource_id WITH =,
  tstzrange(starts_at, ends_at, '[)') WITH &&
) WHERE (active = true);
```

Campos de la restricción NOT NULL. Validar permisos de extensión en despliegue. La expiración debe desactivar ocupaciones transaccionalmente; no usar now() en predicado de índice. Confirmación y expiración deben competir bajo el mismo protocolo de bloqueo. El esquema final requiere migraciones y pruebas reales.
