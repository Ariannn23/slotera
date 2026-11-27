# Límites modulares

Paquete base `com.slotera`, clase `SloteraApplication`.

| Módulo | Responsabilidad |
|---|---|
| identity | Perfil local y vínculo con identidad externa |
| organizations | Empresas, contexto, políticas |
| memberships | Roles e invitaciones |
| branches | Sedes |
| resources | Recursos, categorías, imágenes |
| availability | Reglas temporales, excepciones y ocupaciones |
| bookings | Ciclo de vida, precio, reserva y reprogramación |
| customers | Directorio aislado por organización |
| notifications | Entregas, recordatorios, reintentos |
| reporting | Lecturas agregadas, calendario y dashboard |
| audit | Trazabilidad |
| shared | Tipos realmente transversales, sin lógica de negocio genérica excesiva |

Cada módulo publica contratos explícitos; no importar repositorios internos ajenos. Validar grafo con Spring Modulith y pruebas. El motor de reservas orquesta disponibilidad dentro de una transacción; la propiedad exacta de las ocupaciones debe quedar fijada en ADR-003. Portal público es adaptador web de casos de uso existentes.

```text
bookings/
  domain/model/ (Booking, BookingStatus, TimeRange, Money)
  domain/service/
  domain/event/
  domain/exception/
  application/usecase/
  application/command/
  application/query/
  application/port/
  infrastructure/persistence/
  infrastructure/configuration/
  web/controller/
  web/request/
  web/response/
  web/mapper/
```

Eventos: BookingCreatedEvent, BookingConfirmedEvent, BookingCancelledEvent, BookingRescheduledEvent, BookingExpiredEvent, BookingCompletedEvent. Considerar también rechazo y notificaciones de invitación. Entrega al menos una vez; consumidores idempotentes, registro de publicación Spring Modulith o outbox según ADR. No prometer exactamente una vez para correo.
