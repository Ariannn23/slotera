# Reglas de negocio

- RN-001: Un recurso no admite ocupaciones activas superpuestas.
- RN-002: No reservas en fechas pasadas.
- RN-003: La reserva debe estar dentro del horario disponible.
- RN-004: Respetar duración mínima y máxima.
- RN-005: Recursos inactivos no admiten reservas.
- RN-006: Mantenimiento bloquea disponibilidad.
- RN-007: Fin posterior al inicio.
- RN-008: Toda reserva pertenece a una organización.
- RN-009: Prohibido acceder a datos de otra organización.
- RN-010: Cancelación libera ocupación.
- RN-011: Pendiente bloquea temporalmente.
- RN-012: Pendientes tienen expiración configurable.
- RN-013: Cancelación del cliente respeta política.
- RN-014: Solo transiciones explícitas.
- RN-015: Operaciones críticas auditadas.
- RN-016: Precio calculado en servidor.
- RN-017: Conservar tarifa e importe históricos.
- RN-018: Misma clave idempotente no duplica reserva.
- RN-019: Instantes de reservas en UTC; presentación en zona correspondiente. Horarios semanales se expresan en hora local y zona IANA.
- RN-020: Reprogramación atómica; si falla, conservar original.
- RN-021: Inicio respeta granularidad.
- RN-022: No borrar físicamente reservas históricas.
- RN-023: Cambios de horario no invalidan reservas existentes automáticamente.
- RN-024: Buffer integra intervalo ocupado.
- RN-025: No confirmar solicitudes expiradas.

## Semántica temporal
Intervalos semiabiertos [inicio, fin): los límites contiguos no colisionan sin buffer. Solapamiento: inicio A < fin B y fin A > inicio B. Usar Instant para instantes, LocalTime para reglas semanales y ZoneId para interpretación. Definir tratamiento de cambios de horario estacional y cruces de medianoche antes del sprint de disponibilidad.

## Estados
PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, REJECTED, EXPIRED, CANCELLED, NO_SHOW. Estados finales no se reabren arbitrariamente. Consultar FLOWS.md. Las políticas numéricas (expiración, cancelación, anticipación) son configurables; valores iniciales pendientes.
