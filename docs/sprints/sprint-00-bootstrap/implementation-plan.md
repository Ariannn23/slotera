# Plan de implementación — Sprint 00

Estado: APPROVED · Base SHA: pendiente · Aprobación: usuario, 2026-11-27, conversación de Sprint 00

## Diseño y alternativas

Generar una API mínima Java 21 con Maven Wrapper, Spring Boot, Actuator, acceso a PostgreSQL y Flyway. Crear web React/Vite/TypeScript con pnpm. Compose ofrecerá PostgreSQL para desarrollo; Keycloak y Mailpit se incorporarán cuando sus flujos se implementen. La migración inicial será mínima y verificable, sin anticipar tablas de negocio. La CI ejecutará los controles disponibles para ambos proyectos.

Versiones exactas pendientes de contrastar con documentación oficial y compatibilidad entre Java, Spring Boot, Modulith, Flyway y PostgreSQL. Toda dependencia y versión efectiva se registrará antes de su incorporación. Spring Modulith se integrará si la combinación verificada lo permite; si surge incompatibilidad, se detendrá esa decisión para revisión.

## Archivos previstos

| Ruta | Cambio previsto | Motivo |
|---|---|---|
| `.gitignore`, `README.md`, `CONTRIBUTING.md` | Crear o actualizar | Excluir artefactos y documentar arranque y comandos reales. |
| `apps/api/` | Crear, incluido README de módulo | Wrapper, aplicación, configuración, migración y pruebas. |
| `apps/web/` | Crear, incluido README de módulo | Aplicación, scripts pnpm y pruebas pertinentes. |
| `compose.yaml`, `.env.example` | Crear | Base local reproducible con valores de ejemplo. |
| `.github/workflows/` | Crear | Compilación y verificaciones en PR. |
| `docs/sprints/sprint-00-bootstrap/`, `docs/sprints/SPRINT-00.md` | Completar y enlazar | Plan, criterios y evidencia trazable. |

## Secuencia y Git

1. Tras aprobación, comprobar nuevamente referencias y estado. Inicializar Git con `main`, registrar únicamente la documentación existente y estos borradores en un commit inicial y publicar `main` en el remoto vacío. Esto permite crear el PR exigido por el flujo; si el remoto deja de estar vacío o falla el acceso, detener y revisar.
2. Crear `sprint/00-bootstrap` desde `main` y verificar herramientas locales. Comprobar versiones compatibles y registrar elecciones.
3. Crear API, web e infraestructura en incrementos pequeños con pruebas y README. Registrar comandos y explicar cada paso antes de ejecutarlo.
4. Verificar desde una copia limpia o entorno equivalente, revisar diff y secretos, crear commits atómicos en español y publicar rama. Abrir PR a `main` si el acceso lo permite.
5. Comprobar CI del commit candidato y solicitar aprobación humana del resultado y autorización de merge por separado.

Comandos previstos: `git init -b main` crea la base local; `git remote add origin <URL>` vincula el remoto; `git push -u origin main` publica la base documental; `git switch -c sprint/00-bootstrap` crea la rama. `docker compose config --quiet` valida configuración; `docker compose up -d` inicia servicios; `./mvnw.cmd clean verify` compila y ejecuta verificaciones Maven; scripts pnpm de typecheck, lint, test y build verificarán la web. Sus nombres finales se confirmarán al implementarlos.

## Contratos, recuperación y riesgos

Health será el único endpoint operativo previsto. Sin contratos de negocio. La migración se verificará en PostgreSQL vacío y con una segunda ejecución. No se modifica ninguna migración ya aplicada. Para revertir el bootstrap se usarán commits posteriores y limpieza manual acordada de datos locales; se preservarán datos existentes. Acceso remoto, Docker, CI, puertos y disponibilidad de versiones pueden bloquear verificaciones.

## Aprendizaje

Ejercicio reservado al usuario: iniciar PostgreSQL con Compose, explicar qué servicio, puerto y volumen utiliza y consultar el resultado de health siguiendo las instrucciones del proyecto. Se revisará el resultado y cualquier error juntos.

## Aprobación previa

Estado: APPROVED · Aprobador: usuario · Fecha: 2026-11-27 · Referencia: respuesta «Si aprobado» en la conversación. Una aprobación del plan no autoriza el merge.

## Registro de decisiones

- El usuario autorizó el 27-11-2026 que Sprint 00 continúe desde `main` y que, después de su cierre, `develop` se cree desde `main` y sirva de base a las ramas futuras.
- Selección inicial generada por Spring Initializr: Spring Boot 4.0.10, Spring Modulith 2.0.8 y Maven Wrapper 3.10.2 con Java 21. La integración JPA de eventos Modulith queda para el sprint de eventos, pues necesita su propio esquema.
- PostgreSQL usa el puerto local 55432 porque 5432 estaba ocupado. La comprobación manual de health utilizó 18080 porque 8080 también estaba ocupado.
- En el equipo se usa Podman Compose como proveedor compatible y Microsoft OpenJDK 21.0.13.1. Frontend: Node 24.18.0 y pnpm 10.33.0.
