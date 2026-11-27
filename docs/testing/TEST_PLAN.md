# Plan de pruebas

JUnit/Mockito para dominio; Testcontainers PostgreSQL para integración real; pruebas Spring Security y Modulith; E2E de portal/panel; k6 para carga. Cobertura orientativa 80% en dominio crítico, no sustituto de casos relevantes.

| Caso | Esperado |
|---|---|
| Reserva dentro de horario | Éxito |
| Fuera de horario / pasado / mantenimiento | Rechazo |
| Duración inválida | Rechazo |
| Intervalos contiguos sin buffer | Permitidos |
| Solapamiento parcial / total | Rechazo |
| 100 solicitudes distintas simultáneas al mismo intervalo | Una creación, 99 conflictos; verificar BD |
| Cancelación válida | Libera intervalo |
| Confirmación expirada | Rechazo |
| Reprogramación conflictiva | Original intacta |
| Organización ajena | Sin acceso |
| Clave idempotente repetida | Misma reserva sin duplicar |
| Cambio de precio | Histórico intacto |
| Fallo de correo | Reserva conservada y reintento |
| Cambio de zona | Instantes conservados |
| Bloqueo concurrente con reserva | Solo una ocupación compatible |
| Buffer | Impide solapamiento de ocupación |
| Expiración concurrente con confirmación | Estado e intervalo coherentes |

Prueba concurrente: usar transacciones/conexiones independientes y barrera de inicio, claves distintas, recurso activo y datos válidos; verificar estados y conteo en DB tras finalizar. No sustituir PostgreSQL por H2 para esta prueba.

Rendimiento: fijar hardware, dataset, concurrencia, duración y calentamiento antes de medir p95 < 500 ms en consultas simples. Registrar configuración, resultados y errores. Pruebas pendientes de ejecución.
