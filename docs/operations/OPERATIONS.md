# Operaciones

Health/readiness, logs estructurados con correlation ID, métricas latencia/error, pool DB, conflictos de reserva, expiraciones y entregas fallidas. Restringir endpoints internos. Alertas propuestas: indisponibilidad, errores sostenidos, agotamiento de conexiones y cola de notificaciones.

Backup diario PostgreSQL y datos de identidad/archivos relevantes; cifrar y guardar fuera del servidor. Ensayar restauración en entorno separado. RPO/RTO por definir; objetivo de disponibilidad 99.5% no garantizado.

Runbook: detectar incidente → preservar evidencias sin secretos → identificar versión y alcance → mitigar o rollback → comprobar datos y smoke tests → documentar causa y acciones. No ejecutar migraciones destructivas sin revisión y respaldo.
