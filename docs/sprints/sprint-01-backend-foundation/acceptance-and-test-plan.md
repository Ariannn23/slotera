# Aceptación y plan de pruebas — Sprint 01

Entorno, commit candidato y resultados: pendientes. Los valores de paginación y errores siguientes son propuestas para aprobación.

| ID | HU | Dado | Cuando | Entonces | Estado |
|---|---|---|---|---|---|
| CA-01 | HU-01 | Un DTO sintético con restricciones | Se envía dato válido e inválido | El válido pasa al controlador y el inválido devuelve 400 con detalle de campo sin su valor. | PENDING |
| CA-02 | HU-01 | Peticiones de fixture | Hay JSON malformado, método no permitido, ruta ausente o fallo inesperado | 400, 405, 404 o 500 según corresponda, ProblemDetail para errores manejados y sin traza ni secretos. | PENDING |
| CA-03 | HU-02 | Un conjunto de datos de prueba con empates | Se solicitan páginas y orden admitido o inválido | Orden estable y páginas correctas; valores inválidos devuelven 400 según el plan. | PENDING |
| CA-04 | HU-03 | Perfil dev o test y fixture | Se consultan OpenAPI y UI | Especificación válida que documenta operaciones de prueba; en prod la exposición queda desactivada. | PENDING |
| CA-05 | HU-04 | Configuración de dev, test y prod | Se inicia la aplicación con variables apropiadas o faltantes | Perfiles funcionan según plan; prod sin credenciales exigidas falla sin revelarlas y health del Sprint 00 sigue operativo. | PENDING |
| CA-06 | HU-04 | Una petición y un error sintético | Se revisa la salida de logs | Formato elegido válido, sin tokens, contraseñas, cuerpos ni PII de prueba. | PENDING |
| CA-07 | HU-05 | Módulos o fixtures representativos | Se ejecuta verificación Modulith | Detecta límites válidos e infracciones preparadas; si no es técnicamente demostrable se registra BLOCKED y se acuerda solución. | PENDING |

## Pruebas automatizadas previstas

Pruebas Spring MVC de serialización, `Content-Type`, validación, JSON inválido, errores 404/405/500, omisión de secretos y campos; paginación por defecto, límites, orden con empates y orden prohibido; perfiles y disponibilidad de OpenAPI; verificación de modularidad con caso negativo; regresión de arranque y Flyway en PostgreSQL. Inspeccionar número de pruebas, fallos y skips en reportes. Si una suite depende de Docker/Testcontainers, comprobar su disponibilidad y registrar cualquier bloqueo.

## Pruebas manuales previstas

1. Iniciar PostgreSQL y API en `dev` con datos sintéticos. Consultar health, `/v3/api-docs` y Swagger UI; registrar códigos de respuesta y observación sin secretos.
2. Arrancar en perfil `prod` con configuración incompleta y verificar fallo seguro. Repetir con variables sintéticas válidas y verificar que la documentación interactiva no sea accesible.
3. Revisar salida de logs frente a muestras sintéticas y ejecutar el ejercicio de DTO con el desarrollador.

## Casos fuera de alcance

Reservas, concurrencia, idempotencia, aislamiento de datos de organizaciones y responsive se evaluarán en sus módulos. No marcar como aprobadas esas capacidades por pruebas sintéticas de infraestructura. No se prevén migraciones nuevas; verificar que V1 siga aplicando y validando.

## Evidencia de ejecución

| Fecha | Comando y directorio | SHA y entorno | Salida y suites | Estado | Evidencia |
|---|---|---|---|---|---|
| — | Pendiente | — | — | PENDING | — |

Registrar defectos, causa, corrección, revalidación y evidencia. Estados: PENDING, PASS, FAIL, BLOCKED y NOT_APPLICABLE con motivo.
