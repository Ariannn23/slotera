# Módulo shared

Contiene mecanismos HTTP transversales sin lógica de negocio. `web.ApiExceptionHandler` transforma errores de validación, solicitudes inválidas, rutas ausentes, métodos no admitidos y errores inesperados en ProblemDetail. Los errores de campo solo incluyen su nombre y un mensaje genérico. Las excepciones inesperadas producen un registro genérico sin cuerpo ni datos de la excepción.

`web.Pagination` interpreta `page`, `size` y `sort` para futuros controladores. Cada controlador suministra sus campos permitidos, incluido `id`, como desempate estable. No expone endpoints ni accede a datos. No realiza autenticación ni selección de organización.

`config.ProfileSafetyConfiguration` detiene el arranque si se combina `prod` con `dev` o `test`, para evitar que el orden de perfiles active documentación de desarrollo en producción.

Pruebas: desde `apps/api`, ejecutar `./mvnw.cmd clean verify` en Windows o `./mvnw clean verify` en Unix. `ApiWebTest` usa un controlador sintético exclusivamente en test. `ModularityTest` verifica el módulo real y detecta un ciclo en fixtures. Ver el contrato en `docs/api/API_CONVENTIONS.md` y el plan del Sprint 01.
