# API Slotera

Aplicación Spring Boot inicial. Actuator expone `GET /actuator/health`. PostgreSQL guarda el historial de Flyway y la migración inicial no crea tablas de negocio.

Requiere Java 21 y PostgreSQL disponible. Desde esta carpeta, `./mvnw.cmd clean verify` en Windows o `./mvnw clean verify` en Unix ejecuta compilación y pruebas. Para iniciar la API usar `./mvnw.cmd spring-boot:run` en Windows. La conexión local usa el puerto 55432, usuario y base `slotera`. `DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD` permiten configurarla. Las credenciales predeterminadas son locales.
