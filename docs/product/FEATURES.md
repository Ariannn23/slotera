# Catálogo funcional

## MOD-01 — Autenticación y autorización

Prioridad: Alta.

Registro, login/logout, verificación, recuperación/cambio de contraseña, sesiones, invitaciones, protección de endpoints y límite de intentos. Keycloak gestiona credenciales; Spring valida tokens y permisos.

## MOD-02 — Organizaciones

Prioridad: Alta.

Crear espacio de trabajo, nombre, slug, razón social, correo, teléfono, zona horaria, moneda, logo, estado; selección de organización.

## MOD-03 — Usuarios y permisos

Prioridad: Alta.

Membresías, invitaciones, roles por organización, suspensión y actividad; usuario puede pertenecer a varias empresas.

## MOD-04 — Sedes

Prioridad: Alta.

Nombre, dirección, horario general, zona horaria, estado y recursos asociados.

## MOD-05 — Recursos y categorías

Prioridad: Alta.

Catálogo, fotografías, descripción, capacidad informativa, tarifa, duración mínima/máxima, granularidad, buffer, confirmación y estados ACTIVE/INACTIVE/MAINTENANCE.

## MOD-06 — Horarios y disponibilidad

Prioridad: Alta.

Reglas semanales, excepciones, festivos, mantenimiento, bloqueos, anticipación, buffer y cálculo dinámico de intervalos.

## MOD-07 — Reservas

Prioridad: Alta.

Crear, aprobar, rechazar, expirar, cancelar, reprogramar, check-in, completar, inasistencia, historial, precio y observaciones.

## MOD-08 — Clientes

Prioridad: Alta.

Directorio por empresa, nombres, correo, teléfono, fecha de registro, historial, cancelaciones, inasistencias y notas.

## MOD-09 — Portal público

Prioridad: Alta.

Catálogo por slug, selección de fecha y horario, datos del cliente, aceptación de condiciones, verificación de correo y enlace seguro de gestión.

## MOD-10 — Calendario

Prioridad: Alta.

Vista diaria, semanal y mensual; filtros por sede, recurso y estado.

## MOD-11 — Notificaciones

Prioridad: Media.

Correo e internas: solicitud, confirmación, rechazo, cancelación, reprogramación, recordatorio e invitación; reintentos y registro.

## MOD-12 — Dashboard

Prioridad: Media.

Reservas hoy, pendientes, ocupación, importe de reservas completadas (no pagos), cancelaciones, demanda, clientes frecuentes, inasistencias y sede; CSV básico.

## MOD-13 — Auditoría

Prioridad: Media.

Actor, organización, acción, entidad, cambios permitidos, fecha y correlación. Excluir secretos y datos innecesarios.

## MOD-14 — Configuración

Prioridad: Media.

Políticas de cancelación, anticipación, aprobación, moneda, zona horaria, marca, notificaciones y condiciones.

## Ejemplo de recurso
Cancha 01, sede Trujillo, categoría deportiva, capacidad 14, S/ 80 por hora, duración 60–180 minutos, inicios cada 30 minutos, buffer 0, confirmación manual o automática.

## Notificaciones
Nueva solicitud → administrador; confirmación/rechazo/reprogramación/recordatorio → cliente; cancelación → cliente y empresa; invitación → colaborador. Fallos de correo no revierten reservas.
