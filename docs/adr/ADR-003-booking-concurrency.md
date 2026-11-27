# ADR-003 — Consistencia de reservas

Estado: propuesto, pendiente de aprobación.

## Decisión
Transacción, bloqueo pesimista del recurso, tabla común de ocupaciones y exclusión GiST.

## Alternativas
Validación sola u optimistic locking sin invariante SQL: riesgo de carrera.

## Consecuencias
Serializar operaciones de mismo recurso; mapear conflicto SQL a 409. Bloquear en orden estable si intervienen varios recursos. Resolver expiraciones bajo el mismo protocolo.
