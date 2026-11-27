# Decisiones de arquitectura

- [ADR-001: Monolito modular](ADR-001-modular-monolith.md)
- [ADR-002: Aislamiento multiempresa](ADR-002-multitenancy.md)
- [ADR-003: Consistencia de reservas](ADR-003-booking-concurrency.md)
- [ADR-004: Identidad externa](ADR-004-authentication.md)

## Decisiones pendientes de validación
- Nombre comercial y dominio; modelo SaaS y alcance de recursos exclusivos.
- Versiones compatibles de Java/Spring y dependencias; presupuesto y proveedor de despliegue.
- Política numérica de anticipación, expiración, cancelación, redondeo y buffer.
- Precedencia de horarios sede/recurso, excepciones, horario estacional y medianoche.
- Momento de verificación del correo público, protección contra acaparamiento, tokens de gestión.
- Transiciones excepcionales, ventanas de check-in/no-show y permisos finos de gestión de personal (evitar escalada a OWNER).
- Persistencia de idempotencia, tratamiento de expiraciones y estrategia de eventos; contratos API completos.
- Definición exacta de métricas, privacidad/retención, RPO/RTO y escenario de carga.
- Estimación de 160 horas: revisar tras primeros sprints; no sacrificar calidad para cumplirla.

No se han ejecutado pruebas, creado infraestructura ni verificado versiones. Los diagramas y SQL son diseños conceptuales.
