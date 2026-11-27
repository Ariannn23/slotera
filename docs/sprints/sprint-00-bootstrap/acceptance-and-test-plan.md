# Aceptación y pruebas — Sprint 00

Entorno y commit candidato: pendientes. Estados iniciales PENDING.

| ID | Historia | Dado | Cuando | Entonces | Estado |
|---|---|---|---|---|---|
| CA-01 | HU-01 | Una copia limpia y requisitos documentados | Se inicia PostgreSQL y la API | Flyway aplica la migración y health responde correctamente. | PASS |
| CA-02 | HU-01 | Una base ya migrada | Se inicia de nuevo la API | Flyway valida el historial y el arranque concluye. | PASS |
| CA-03 | HU-01 | Una copia limpia | Se instalan dependencias y arranca web | La aplicación inicial carga y sus pasos están documentados. | PASS |
| CA-04 | HU-02 | El proyecto configurado | Se ejecutan verificaciones API y web | Compilación, pruebas, lint, typecheck y build aplicables terminan correctamente. | PASS |
| CA-05 | HU-02 | Un PR con el commit candidato | Se ejecuta GitHub Actions | Las verificaciones configuradas terminan en verde y quedan registradas. | PENDING |
| CA-06 | HU-03 | Instrucciones de arranque | El desarrollador realiza el ejercicio | Explica servicio, puerto y volumen y verifica health con acompañamiento. | PASS |

## Casos adicionales

Verificar configuración Compose, conexión fallida con PostgreSQL y mensaje de diagnóstico, aplicación de migración sobre PostgreSQL real, reinicio, arranque de web, ausencia de secretos en archivos versionados y README de ambos módulos. Registrar pasos manuales, salidas, entorno y evidencia. Los controles de reservas, aislamiento y concurrencia se marcarán NOT_APPLICABLE con motivo en este sprint.

## Ejecución

| Fecha | Comando y directorio | SHA y entorno | Resultado | Evidencia |
|---|---|---|---|---|
| Pendiente | Pendiente | Pendiente | PENDING | — |

Defectos, correcciones y revalidaciones: pendientes.

## Evidencia parcial del 27-11-2026

- `podman compose config --quiet` terminó con código 0 y `podman compose ps` mostró PostgreSQL 17 saludable en 55432.
- `mvnw.cmd clean verify` falló inicialmente por la tabla de eventos de Modulith ausente. Tras retirar la integración JPA de eventos, terminó con código 0 y una prueba de contexto aprobada. Flyway aplicó V1 y validó el historial en el segundo arranque.
- `GET http://localhost:18080/actuator/health` respondió `UP`.
- En `apps/web`, `pnpm lint`, `pnpm build`, `pnpm typecheck` y `pnpm test` terminaron con código 0. Vitest ejecutó una prueba.
- En ese momento quedaban pendientes la copia limpia, el fallo de conexión, la interfaz, CI y el ejercicio. Las secciones siguientes registran los resultados posteriores.

## CI y diagnóstico adicional

- El PR 1 para `main` ejecutó CI en el commit `5ddc1f7`. Web pasó; API falló por permiso de ejecución de `mvnw` en Linux. El commit `7aceec1` corrigió el modo del archivo.
- La ejecución 46060512990 en el commit `7aceec1` terminó con API y web en PASS. La CI se debe volver a comprobar para cualquier commit posterior.
- El servidor Vite respondió HTTP 200 en el puerto 15173. La inspección visual y el ejercicio siguen pendientes.
- La API con `DATABASE_URL` apuntando al puerto local 1 terminó con código 1 al fallar la conexión, como se esperaba para una base inaccesible.

## Verificación desde copia limpia

- El 27-11-2026 se clonó el commit `a0853cc` en un directorio temporal. En la web, `pnpm install --frozen-lockfile`, lint, typecheck, test y build terminaron correctamente. Vitest ejecutó una prueba.
- En una base PostgreSQL 17 vacía, creada separadamente en el contenedor de desarrollo, `mvnw.cmd --batch-mode clean verify` terminó correctamente y Flyway aplicó V1. Un segundo arranque de la API validó la migración y `GET /actuator/health` respondió `UP` en el puerto de prueba 18082.
- Playwright revisó la pantalla inicial generada por Vite en anchos 375, 768 y 1280 px. La página se adaptó sin desbordamiento horizontal y la consola no mostró errores ni advertencias. La pantalla sigue siendo la plantilla de Vite.
- La CI del commit candidato final debe comprobarse después de incorporar esta evidencia.
- Se inició Compose desde la copia con un proyecto y volumen nuevos. Para evitar el puerto ya ocupado se cambió en una copia temporal de Compose el puerto anfitrión a 55433 y se ajustó `DATABASE_URL`. Flyway aplicó V1 y health respondió `UP` en el puerto de prueba 18083.
- El desarrollador compartió una respuesta health con estado `UP` y el fragmento de Compose del servicio PostgreSQL. Se revisaron conjuntamente el mapeo de puertos y la función del volumen, aclarando su consulta sobre persistencia.
- El usuario aceptó expresamente el 27-11-2026 la revisión acompañada como cumplimiento de CA-06. El criterio original pedía una explicación del desarrollador; se acepta la revisión conjunta realizada en la conversación.
