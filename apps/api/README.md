# API Slotera

Aplicación Spring Boot inicial. Actuator expone `GET /actuator/health`. PostgreSQL guarda el historial de Flyway y la migración inicial no crea tablas de negocio.

Requiere Java 21 y PostgreSQL disponible. Desde esta carpeta, `./mvnw.cmd clean verify` en Windows o `./mvnw clean verify` en Unix ejecuta compilación y pruebas. Para iniciar la API usar `./mvnw.cmd spring-boot:run` en Windows. La conexión local usa el puerto 55432, usuario y base `slotera`. `DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD` permiten configurarla. Las credenciales predeterminadas son locales.

## Perfiles y documentación

La configuración sin perfil conserva el arranque local y mantiene OpenAPI deshabilitado. Para desarrollo, `./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev` habilita `/v3/api-docs` y `/swagger-ui/index.html`. El perfil `test` también los habilita. `prod` exige `DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD`, desactiva ambas rutas y emite logs estructurados ECS. No se puede combinar `prod` con `dev` o `test`. Estos perfiles no habilitan autenticación. La identidad se implementará en el Sprint 02.

Las convenciones de errores y paginación están en `docs/api/API_CONVENTIONS.md`; los mecanismos transversales se describen en `src/main/java/com/slotera/shared/README.md`.
