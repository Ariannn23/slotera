# Aceptación y pruebas — Sprint 00

Entorno y commit candidato: pendientes. Estados iniciales PENDING.

| ID | Historia | Dado | Cuando | Entonces | Estado |
|---|---|---|---|---|---|
| CA-01 | HU-01 | Una copia limpia y requisitos documentados | Se inicia PostgreSQL y la API | Flyway aplica la migración y health responde correctamente. | PENDING |
| CA-02 | HU-01 | Una base ya migrada | Se inicia de nuevo la API | Flyway valida el historial y el arranque concluye. | PENDING |
| CA-03 | HU-01 | Una copia limpia | Se instalan dependencias y arranca web | La aplicación inicial carga y sus pasos están documentados. | PENDING |
| CA-04 | HU-02 | El proyecto configurado | Se ejecutan verificaciones API y web | Compilación, pruebas, lint, typecheck y build aplicables terminan correctamente. | PENDING |
| CA-05 | HU-02 | Un PR con el commit candidato | Se ejecuta GitHub Actions | Las verificaciones configuradas terminan en verde y quedan registradas. | PENDING |
| CA-06 | HU-03 | Instrucciones de arranque | El desarrollador realiza el ejercicio | Explica servicio, puerto y volumen y verifica health con acompañamiento. | PENDING |

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
- Falta verificar copia limpia, fallo de conexión, interfaz en navegador, CI del commit candidato y ejercicio del desarrollador. Estos casos permanecen PENDING.

## CI y diagnóstico adicional

- El PR 1 para `main` ejecutó CI en el commit `5ddc1f7`. Web pasó; API falló por permiso de ejecución de `mvnw` en Linux. El commit `7aceec1` corrigió el modo del archivo.
- La ejecución 46060512990 en el commit `7aceec1` terminó con API y web en PASS. La CI se debe volver a comprobar para cualquier commit posterior.
- El servidor Vite respondió HTTP 200 en el puerto 15173. La inspección visual y el ejercicio siguen pendientes.
- La API con `DATABASE_URL` apuntando al puerto local 1 terminó con código 1 al fallar la conexión, como se esperaba para una base inaccesible.
