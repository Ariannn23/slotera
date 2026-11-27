# Contrato REST preliminar

Base `/api/v1`. JSON, OpenAPI, validación Jakarta, DTOs separados de entidades. Paginación, filtros y orden estable. Endpoints internos requieren `Authorization: Bearer ...`. Los endpoints asociados a una organización requerirán `X-Organization-Id` validado contra membresía en Sprint 03. `/me` requiere identidad y funciona sin organización seleccionada. Público resuelve slug y comprueba pertenencia del recurso.

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

## Fundamentos implementados en Sprint 01

El manejador HTTP responde con `application/problem+json`, `type`, `title`, `status`, `detail`, `instance` y `code`. Para validación se añade `fields` con nombres de campo y mensajes genéricos, sin valores enviados. JSON inválido y parámetros de paginación inválidos producen 400; ruta inexistente 404; método no permitido 405; fallo inesperado 500 con detalle genérico. Las rutas de negocio de la tabla siguen siendo contratos preliminares.

La utilidad de paginación recibe `page` desde 0 y `size` entre 1 y 100, con valores iniciales 0 y 20. `sort` usa `campo,asc` o `campo,desc`; el controlador define explícitamente campos permitidos y debe incluir `id` como desempate. Un parámetro repetido, dirección o campo no admitido, valor no numérico o fuera de rango produce 400. Cada módulo definirá el JSON de sus listados y cuándo aplica esta utilidad.

Springdoc 3.0.3 habilita `/v3/api-docs` y Swagger UI solo con los perfiles `dev` y `test`. El perfil `prod` los desactiva y el arranque rechaza combinar `prod` con `dev` o `test`, independientemente del orden. El estado de salud de Actuator se mantiene fuera de `/api/v1`.

## Perfil autenticado

`GET /api/v1/me` recibe un access token Bearer válido para la audiencia de Slotera, con correo verificado. Responde 200 con `id`, `email` y `emailVerified`. La primera llamada crea la identidad local transaccionalmente por `(issuer, subject)`. Una credencial ausente o inválida produce 401 con `WWW-Authenticate: Bearer`; correo no verificado o usuario local inactivo produce 403. Estos errores usan `application/problem+json`. No se requiere `X-Organization-Id` para este endpoint.
