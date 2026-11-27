# Sprint 01 — Fundamentos backend

Estado: CLOSED | Semana orientativa: 2 | Estimación del roadmap: 10 h
Base prevista: `develop` en `23df02d` | Rama propuesta: `sprint/01-backend-foundation`
[Ficha del roadmap](../SPRINT-01.md)

## Objetivo y aprendizaje

Construir una base REST comprobable para los siguientes módulos, con validación de entrada, respuestas de error, paginación, documentación, perfiles, registros y límites modulares. Aprender el ciclo de una petición Spring MVC, DTO mediante `record`, validación Jakarta, `ProblemDetail`, perfiles e inyección de dependencias.

## Alcance propuesto

Implementar infraestructura transversal reutilizable, sustentada por controladores y datos sintéticos exclusivos de pruebas. Documentar y verificar los mecanismos sin crear endpoints de negocio provisionales. Spring MVC y Modulith ya son dependencias de la API: comprobar su integración y conservar health y Flyway.

La autenticación y Keycloak pertenecen al Sprint 02; organizaciones y permisos al Sprint 03. Este sprint no incorpora tablas, migraciones, datos de negocio, UI ni operaciones públicas nuevas. La decisión de exposición segura de documentación en producción queda pendiente de identidad y despliegue.

## Historias

| ID | Como | Quiero | Para | Trazabilidad | Criterios |
|---|---|---|---|---|---|
| HU-01 | Desarrollador | Validar DTO de peticiones y recibir errores coherentes | Diagnosticar errores sin exponer detalles internos | API_CONVENTIONS, RNF-12 | CA-01, CA-02 |
| HU-02 | Desarrollador | Aplicar paginación y orden predecibles | Consultar futuros listados de forma consistente | API_CONVENTIONS | CA-03 |
| HU-03 | Desarrollador | Consultar OpenAPI en desarrollo | Conocer los contratos disponibles | RNF-06 | CA-04 |
| HU-04 | Operador técnico | Distinguir perfiles y leer registros | Ejecutar y diagnosticar la API por entorno | RNF-08, RNF-11 | CA-05, CA-06 |
| HU-05 | Mantenedor | Verificar límites entre módulos | Detectar dependencias indebidas | RNF-05, ADR-001 propuesto | CA-07 |

## Flujos y dependencias

El controlador recibe DTO, Spring valida y el caso de uso responde o produce un error traducido de forma consistente. Se prueban sintaxis JSON incorrecta, validaciones y fallos internos. Listados posteriores deberán definir sus campos de orden permitidos. PostgreSQL local, Java 21, Maven Wrapper y Spring Boot 4.0.10 provienen de Sprint 00. La rama `develop` contiene su cierre verificado.

## Decisiones propuestas y riesgos

El usuario aprobó las cifras de paginación, el formato de error y los perfiles del plan. No se ha fijado un contrato global de logs ni una política completa de OpenAPI en producción. Las pruebas sintéticas no demuestran aislamiento multiempresa, que se implementará y verificará con módulos reales. ADR-001 sigue documentado como propuesto y no se declara aprobado por este sprint. Diez horas fueron una referencia; no se registró una medición de tiempo real.

## Seguimiento

- [x] Plan aprobado
- [x] Rama e implementación
- [x] Verificación y evidencia
- [x] Resultado aprobado, merge y cierre verificados

[Plan de implementación](implementation-plan.md) · [Aceptación y pruebas](acceptance-and-test-plan.md) · [Acta](approval.md)
