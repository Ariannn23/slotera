# Contrato REST preliminar

Base `/api/v1`. JSON, OpenAPI, validación Jakarta, DTOs separados de entidades. Paginación, filtros y orden estable. Endpoints internos usan `Authorization: Bearer ...` y `X-Organization-Id` validado contra membresía. Público resuelve slug y comprueba pertenencia del recurso.

| Método | Ruta | Acción |
|---|---|---|
| GET | `/me` | Perfil |
| POST | `/organizations` | Crear empresa |
| GET | `/organizations` | Mis empresas |
| GET | `/organizations/{id}` | Detalle |
| POST | `/organizations/{id}/invitations` | Invitar |
| GET | `/memberships` | Miembros |
| POST | `/branches` | Crear sede |
| GET | `/branches` | Listar sedes |
| POST | `/resources` | Crear recurso |
| GET | `/resources` | Listar recursos |
| PATCH | `/resources/{id}` | Actualizar |
| PUT | `/resources/{id}/availability` | Configurar horarios |
| GET | `/resources/{id}/slots` | Disponibilidad |
| POST | `/resources/{id}/blocks` | Bloquear |
| POST | `/bookings` | Crear |
| GET | `/bookings` | Listar |
| GET | `/bookings/{id}` | Detalle |
| POST | `/bookings/{id}/confirm` | confirm |
| POST | `/bookings/{id}/reject` | reject |
| POST | `/bookings/{id}/cancel` | cancel |
| POST | `/bookings/{id}/reschedule` | reschedule |
| POST | `/bookings/{id}/check-in` | check-in |
| POST | `/bookings/{id}/complete` | complete |
| POST | `/bookings/{id}/no-show` | no-show |
| GET | `/customers` | Clientes |
| GET | `/calendar` | Calendario |
| GET | `/dashboard/metrics` | Indicadores |
| GET | `/audit-logs` | Auditoría |
| GET | `/public/{slug}/resources` | Catálogo |
| GET | `/public/{slug}/resources/{id}/slots` | Slots públicos |
| POST | `/public/{slug}/bookings` | Reserva pública |

## Crear reserva
```http
POST /api/v1/bookings
Authorization: Bearer <access-token>
X-Organization-Id: <organization-id>
Idempotency-Key: 487d8fca-33ef-4bdd
Content-Type: application/json
```
```json
{"resourceId":"resource-uuid","customerId":"customer-uuid","startsAt":"2027-01-15T15:00:00Z","endsAt":"2027-01-15T16:00:00Z","notes":"Reserva deportiva"}
```
Respuesta 201:
```json
{"id":"booking-uuid","code":"SLT-000124","status":"CONFIRMED","startsAt":"2027-01-15T15:00:00Z","endsAt":"2027-01-15T16:00:00Z","totalAmount":80.00,"currency":"PEN"}
```
Respuesta 409, `application/problem+json` (RFC 9457):
```json
{"type":"/problems/booking-conflict","title":"Booking conflict","status":409,"detail":"El horario seleccionado ya no está disponible.","code":"BOOKING_SLOT_UNAVAILABLE"}
```
400 entradas inválidas, 401 sin identidad, 403 permisos insuficientes, 404 entidad inexistente o inaccesible cuando convenga evitar enumeración, 409 conflicto, 429 exceso de solicitudes, 500 error interno sin stack trace.

Idempotencia: clave acotada por organización/actor/operación, hash del cuerpo, persistencia atómica del resultado; misma clave con cuerpo diferente se rechaza. Definir TTL y semántica de reintentos. Las rutas de recuperación, invitaciones, cambios de rol, clientes y enlaces seguros se completarán en su sprint; este inventario no es OpenAPI final.
