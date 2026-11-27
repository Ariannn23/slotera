# Plan de implementación — Sprint 01

Estado: APPROVED | Versión: 1 | Base examinada: `23df02d` | Aprobación: usuario, 2026-11-27

## Diseño propuesto

1. Mantener el entrypoint MVC existente. Añadir Jakarta Bean Validation mediante el starter compatible con Spring Boot 4 gestionado por el BOM de Boot. Usar `record` como DTO de prueba, `@Valid` en el límite HTTP y restricciones en el DTO. Evitar entidades JPA y endpoints permanentes de ejemplo.
2. Centralizar errores HTTP en un manejador transversal con `ProblemDetail` RFC 9457. Propuesta de contrato: `type`, `title`, `status`, `detail`, `instance` y `code`; errores de validación pueden incluir una lista de campos y mensajes sin reflejar valores. Respuestas 400 para validación o JSON inválido y 500 genérico para excepción inesperada; rutas inexistentes y método no permitido conservarán 404 y 405. No incluir trazas, SQL ni valores sensibles. Verificar que se mantenga `application/problem+json`.
3. Proponer paginación con `page` desde 0, `size` predeterminado 20 y máximo 100; parámetros negativos, tamaño cero, tamaño superior al máximo o campo de orden no permitido responden 400. Cada futuro endpoint tendrá lista explícita de campos ordenables y desempate estable mediante identificador. La forma exacta del JSON de cada listado se definirá con su módulo; no exponer serialización interna de `Page` como contrato. Probar la configuración con un controlador de fixture en `src/test`.
4. Añadir springdoc compatible con Boot 4.0.x; la matriz oficial indica springdoc 3.0.x. Fijar una versión estable específica tras comprobar disponibilidad y compatibilidad antes de editar `pom.xml`. Proponer OpenAPI y Swagger UI únicamente en `dev` y `test`; producción queda desactivada hasta acordar protección. Verificar documentación del fixture en un contexto de prueba y health existente sin prometer cobertura de futuros endpoints.
5. Definir perfiles `dev`, `test` y `prod` con configuración externa de conexión. Preservar el arranque local documentado, evitando que un entorno de producción use credenciales locales por defecto. Probar que configuración ausente en prod produce fallo explícito y que test/dev no exponen secretos. No activar perfiles por defecto de forma que convierta accidentalmente un despliegue en dev.
6. Usar infraestructura de logging existente de Spring Boot con formato estructurado disponible de fábrica y salida apropiada por perfil. Correlation ID solo si se define su generación, validación, propagación y limpieza del contexto; excluir credenciales, tokens, cuerpos de solicitudes y datos personales. Un identificador de correlación de infraestructura no se mostrará como referencia al usuario final.
7. Añadir prueba de Spring Modulith de límites reales y detectar que no sea trivial si aún no hay módulos de negocio. Evaluar fixtures de prueba con módulos deliberadamente válidos e inválidos sin crear paquetes de producción vacíos; si la herramienta no permite una prueba significativa, registrar el límite y proponer la comprobación junto al primer módulo real, sin marcarla PASS por ausencia de módulos.

Alternativas evaluadas: confiar en la serialización de errores predeterminada no garantiza el campo `code` documentado; un contrato HTTP artificial permanente aumentaría la superficie del producto; cambiar Boot para OpenAPI ampliaría el alcance. Este plan requiere aprobar las propuestas concretas y las dependencias nuevas.

## Archivos previstos

| Ruta | Acción | Motivo | Criterios |
|---|---|---|---|
| `apps/api/pom.xml` | Modificar | Starter de validación y springdoc con versión compatible verificada | CA-01, CA-04 |
| `apps/api/src/main/java/com/slotera/` | Crear o modificar componentes transversales necesarios | Errores, paginación y configuración; sin capas vacías | CA-01–03, CA-06 |
| `apps/api/src/main/resources/application*.properties` | Modificar o crear perfiles | Configuración por entorno y OpenAPI | CA-04–06 |
| `apps/api/src/test/` | Crear fixtures y pruebas MVC, perfiles y modularidad | Casos reales de error y límites sin API ficticia | CA-01–07 |
| `apps/api/README.md`, `docs/api/API_CONVENTIONS.md` | Actualizar tras implementar | Contrato efectivo, perfiles, seguridad y comandos | CA-01–06 |
| `docs/sprints/sprint-01-backend-foundation/`, `docs/sprints/SPRINT-01.md` | Completar y enlazar | Criterios, aprobación y evidencia | Todas |
| `README.md` | Actualizar al ejecutar | Corregir el estado de bootstrap y documentar arranque aplicable | CA-05 |

## Datos, contratos y recuperación

Sin nuevas tablas, migraciones ni cambios de datos. No implementar autenticación ni prometer seguridad por organización antes de S02–03. Los cambios de configuración de producción deben fallar con mensaje de configuración ausente sin exponer secretos. Reversión mediante commit inverso tras revisar compatibilidad, preservando V1 de Flyway. El contrato de errores y las reglas de paginación son propuestas explícitas para aprobación, no una modificación ya aplicada.

## Secuencia y comandos

1. Tras aprobar, `git status --short --branch`, `git fetch origin` y `git pull --ff-only origin develop` comprueban estado y actualizan base. Conservar archivos no versionados `.playwright-cli/` y `build/`; si interfieren, detener y acordar cómo proceder. Crear `git switch -c sprint/01-backend-foundation` desde develop actualizado. Incorporar estos borradores preservando su contenido.
2. Verificar compatibilidad y fijar dependencias aprobadas; introducir validación, errores, paginación y pruebas de fixture en incrementos revisables.
3. Configurar perfiles, OpenAPI y logs; comprobar modularidad, regresiones de health y migración existente. Actualizar documentación y resultados observados.
4. Desde `apps/api`, `./mvnw.cmd clean verify` compila y prueba; inspeccionar reportes Surefire/Failsafe. Desde `apps/web`, `pnpm run typecheck`, `pnpm run lint`, `pnpm run test` y `pnpm run build` comprueban regresiones aplicables. Desde raíz `podman compose config --quiet` valida configuración y `podman compose ps` verifica servicios. Realizar pruebas HTTP manuales de la API y OpenAPI en el perfil apropiado; registrar entorno y salida.
5. Revisar diff, comprometer cambios en commits atómicos en español, publicar rama y abrir PR hacia `develop`. La aprobación del cierre y autorización del merge son independientes de esta aprobación inicial.

## Aprendizaje y estimación

Explicar `@RestController`, `record`, `@Valid`, excepciones y perfiles al avanzar. Ejercicio reservado al usuario: escribir un DTO de prueba con una restricción de campo y proponer una petición válida y otra inválida; revisar su solución antes de integrarla. No resolverlo automáticamente. Presupuesto orientativo: análisis y lectura 1,5 h; MVC/validación/errores 2,5 h; paginación 1,5 h; perfiles/OpenAPI/logs 2 h; modularidad, pruebas y evidencia 2,5 h. La suma es 10 h y no garantiza que el alcance termine en ese tiempo.

## Aprobación previa

Estado: APPROVED | Aprobador: usuario | Fecha: 2026-11-27 | Referencia: «Apruebo el plan y las decisiones propuestas comienza con la implementacion» | Versión aprobada: 1.

## Fuentes técnicas consultadas

- [Matriz de compatibilidad de springdoc](https://springdoc.org/)
- [Logging estructurado de Spring Boot](https://docs.spring.io/spring-boot/reference/features/logging.html)

## Decisiones de implementación

- Se seleccionó springdoc 3.0.3 para Spring Boot 4.0.10, de la línea 3.0.x indicada en la matriz oficial. El starter de validación usa la versión administrada por Spring Boot.
- La combinación de `prod` con `dev` o `test` se rechaza al iniciar, con independencia del orden, para impedir exposición accidental de OpenAPI. Sin perfil activo se preserva el arranque local del Sprint 00.
- El mensaje público para cada campo inválido es genérico para no reflejar valores. El registro del manejador para errores inesperados es genérico y no incluye la excepción; se registra como limitación para diagnóstico posterior.
- El ejercicio entregado por el usuario se incorporó exclusivamente en tests con el import de Jakarta Validation. Se probaron su JSON válido y el nombre con solo espacios.
