# Requisitos

## Funcionales

| ID | Módulo | Prioridad | Criterio general |
|---|---|---|---|
| RF-01 | Autenticación y autorización | Alta | Registro, login/logout, verificación, recuperación/cambio de contraseña, sesiones, invitaciones, protección de endpoints y límite de intentos. Keycloak gestiona credenciales; Spring valida tokens y permisos. |
| RF-02 | Organizaciones | Alta | Crear espacio de trabajo, nombre, slug, razón social, correo, teléfono, zona horaria, moneda, logo, estado; selección de organización. |
| RF-03 | Usuarios y permisos | Alta | Membresías, invitaciones, roles por organización, suspensión y actividad; usuario puede pertenecer a varias empresas. |
| RF-04 | Sedes | Alta | Nombre, dirección, horario general, zona horaria, estado y recursos asociados. |
| RF-05 | Recursos y categorías | Alta | Catálogo, fotografías, descripción, capacidad informativa, tarifa, duración mínima/máxima, granularidad, buffer, confirmación y estados ACTIVE/INACTIVE/MAINTENANCE. |
| RF-06 | Horarios y disponibilidad | Alta | Reglas semanales, excepciones, festivos, mantenimiento, bloqueos, anticipación, buffer y cálculo dinámico de intervalos. |
| RF-07 | Reservas | Alta | Crear, aprobar, rechazar, expirar, cancelar, reprogramar, check-in, completar, inasistencia, historial, precio y observaciones. |
| RF-08 | Clientes | Alta | Directorio por empresa, nombres, correo, teléfono, fecha de registro, historial, cancelaciones, inasistencias y notas. |
| RF-09 | Portal público | Alta | Catálogo por slug, selección de fecha y horario, datos del cliente, aceptación de condiciones, verificación de correo y enlace seguro de gestión. |
| RF-10 | Calendario | Alta | Vista diaria, semanal y mensual; filtros por sede, recurso y estado. |
| RF-11 | Notificaciones | Media | Correo e internas: solicitud, confirmación, rechazo, cancelación, reprogramación, recordatorio e invitación; reintentos y registro. |
| RF-12 | Dashboard | Media | Reservas hoy, pendientes, ocupación, importe de reservas completadas (no pagos), cancelaciones, demanda, clientes frecuentes, inasistencias y sede; CSV básico. |
| RF-13 | Auditoría | Media | Actor, organización, acción, entidad, cambios permitidos, fecha y correlación. Excluir secretos y datos innecesarios. |
| RF-14 | Configuración | Media | Políticas de cancelación, anticipación, aprobación, moneda, zona horaria, marca, notificaciones y condiciones. |

## Roles
- SUPER_ADMIN: administración de plataforma, estado de organizaciones; sin acceso ordinario a datos privados.
- OWNER: control de organización y configuración.
- ADMIN: administración operativa y personal.
- OPERATOR: reservas, clientes, disponibilidad y asistencia.
- CUSTOMER: consulta pública y gestión de sus propias reservas, por identidad o enlace seguro.

| Acción | OWNER | ADMIN | OPERATOR | CUSTOMER |
|---|---|---|---|---|
| Configurar empresa | Sí | No | No | No |
| Personal, sedes, recursos, horarios | Sí | Sí | No | No |
| Crear reserva | Sí | Sí | Sí | Sí |
| Confirmar y gestionar todas | Sí | Sí | Sí | No |
| Cancelar propia | Sí | Sí | Sí | Sí |
| Reportes y auditoría | Sí | Sí | No | No |

## No funcionales

- RNF-01: **Rendimiento** — p95 < 500 ms en consultas simples bajo carga definida.
- RNF-02: **Concurrencia** — Cero reservas activas duplicadas.
- RNF-03: **Seguridad** — Cero accesos no autorizados en pruebas de aislamiento.
- RNF-04: **Disponibilidad** — Objetivo 99.5% mensual en producción, no verificado.
- RNF-05: **Mantenibilidad** — Sin dependencias circulares.
- RNF-06: **Documentación** — Endpoints públicos documentados.
- RNF-07: **Trazabilidad** — Operaciones críticas auditadas.
- RNF-08: **Portabilidad** — Docker Compose reproducible.
- RNF-09: **Accesibilidad** — Responsive y navegación por teclado.
- RNF-10: **Recuperación** — Backup diario y restauración comprobada.
- RNF-11: **Observabilidad** — Logs, métricas y health checks.
- RNF-12: **Consistencia** — Integridad transaccional.

Objetivos pendientes de medición. Los criterios específicos se desarrollan en cada sprint y en TEST_PLAN.md.
