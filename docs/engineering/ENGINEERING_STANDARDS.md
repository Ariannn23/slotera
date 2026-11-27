# Estándares de ingeniería Slotera

## Arquitectura y backend
Java 21, Spring Boot, Maven Wrapper, Spring MVC, JPA/Hibernate, PostgreSQL/Flyway, Spring Security/Keycloak y Spring Modulith según la arquitectura aprobada. Organizar por dominio en `apps/api/src/main/java/com/slotera/<modulo>/`:

```text
<modulo>/
  domain/          # entidades, valores, reglas, eventos
  application/     # casos de uso, puertos, transacciones
  infrastructure/  # persistencia, integraciones, configuración
  web/             # controllers, request, response, mappers
  README.md
```
Pruebas en `apps/api/src/test/java/com/slotera/<modulo>/`. No introducir capas vacías en CRUD sencillos. Controladores delgados, DTOs separados de entidades, validación Jakarta, inyección por constructor, excepciones de dominio y ProblemDetail RFC 9457. Evitar dependencias circulares y acceso directo a internals de otros módulos. Validar límites con Spring Modulith. Los cambios de esquema son migraciones Flyway versionadas; no editar migraciones aplicadas. Verificar integridad, índices, reversión/recuperación y compatibilidad.

## Invariantes críticas
- Toda consulta y modificación de datos de empresa verifica organización y pertenencia del recurso; no confiar en el encabezado del cliente. Roles por membresía.
- Disponibilidad dinámica, intervalos semiabiertos `[inicio, fin)`, buffers y bloqueos centralizados en ocupaciones. Protección transaccional y restricción de exclusión PostgreSQL; probar carreras con DB real. No usar caché como fuente de verdad.
- `Instant`/`TIMESTAMPTZ` para instantes y zona IANA para reglas locales; `BigDecimal` para dinero. Precios calculados en servidor, conservar snapshot.
- Idempotencia y transiciones válidas; reprogramación atómica; expiración no confirmable. Eventos y notificaciones confiables después de commit, sin revertir reserva por fallo SMTP.
- Validar tokens, permisos, límites públicos; no exponer PII, tokens o credenciales en logs, screenshots ni fixtures.

## Frontend y diseño
React, Vite, TypeScript, Tailwind, shadcn/ui, TanStack Query, React Hook Form, Zod y FullCalendar según necesidad y versiones acordadas. Usar `apps/web/src/features/<modulo>/` con `components/`, `forms/`, `hooks/`, `pages/`, `services/`, `types/`, `utils/`, `README.md` únicamente cuando se necesiten. Clientes HTTP centralizados, estados remotos consistentes, validación visual sin sustituir controles de servidor.

Reutilizar primitivas de `@/components/ui/` cuando existan. Catálogo objetivo (no asumir que ya está implementado): Button, SmartSelect o equivalente accesible, Dialog/Modal, DataTable, DatePicker, DateRangePicker, ModuleMetrics, EmptyState, ErrorState, Pagination y PageHeader. Crear componentes comunes solo con necesidad justificada. Mantener `docs/design/UI_GUIDE.md` como fuente de tokens y tipografía de Slotera, no copiar clases de Torcly.

- Encabezados consistentes, jerarquía de título y descripción, errores visibles en color destructivo y texto accesible.
- Skeletons para carga, estados vacíos y error con reintento; acciones con loading y prevención de doble envío.
- Modales con título limpio, foco controlado, Escape cuando corresponda; sin iconos decorativos salvo alerta. No `window.alert()` ni `window.confirm()`; usar toast o diálogo de confirmación proporcional al riesgo.
- Selectores reutilizables y accesibles cuando existan; no duplicar primitivas con HTML sin estilo. Navegación por teclado, contraste y etiquetas. Validar 375, 768 y 1280+ px y consola sin warnings/errores.

## Verificaciones y comandos
Son comandos de referencia para el stack planificado; Sprint 00 debe confirmar scripts, gestor web, plugins Maven, perfiles e infraestructura. Ejecutar desde la carpeta indicada. No inventar scripts ni asumir que están disponibles. Si falta un comando obligatorio, definirlo en el plan aprobado o registrar bloqueo. No sustituir integración PostgreSQL por H2.

| Área | Directorio | Comando de referencia |
|---|---|---|
| API Unix | apps/api | `./mvnw clean verify` |
| API Windows PowerShell | apps/api | `./mvnw.cmd clean verify` |
| Frontend (si se adopta pnpm) | apps/web | `pnpm run typecheck` |
| Frontend | apps/web | `pnpm run lint` |
| Frontend | apps/web | `pnpm run test` (modo CI sin watch según script) |
| Frontend | apps/web | `pnpm run build` |
| Infra | raíz | `docker compose config --quiet` |
| Infra | raíz | `docker compose ps` |

Configurar Surefire/Failsafe o equivalente para que `verify` ejecute suites unitarias e integración; verificar reportes, no solo código de salida. Usar JUnit, Mockito, Testcontainers, pruebas de modularidad, seguridad, concurrencia y k6 según impacto. Verificar migraciones desde DB vacía y versión anterior cuando aplique. Pruebas manuales API con cliente HTTP/Swagger y UI en navegador; registrar pasos, datos y observación. No exigir typecheck de TypeScript en backend Java. Los comandos finales deben reflejarse en README y CI.

## Documentación y calidad
README obligatorio por módulo: responsabilidad y límites, permisos reales, entidades/contratos, endpoints/rutas, dependencias, invariantes, errores, tests y ejemplos. Mantener al día documentación y ADR. No cambiar decisiones sin autorización. Nunca guardar evidencias sensibles. Consultar `docs/testing/TEST_PLAN.md`, `docs/security/THREAT_MODEL.md` y `CONTRIBUTING.md`.
