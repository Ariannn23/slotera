# Sprint 00 — Inicialización

Estado: CLOSED · Estimación orientativa: 10 horas · Base: `main` · Rama: `sprint/00-bootstrap`

[Ficha del roadmap](../SPRINT-00.md)

## Objetivo

Preparar un entorno reproducible para desarrollar y verificar una API Spring Boot y una aplicación React, con PostgreSQL, migraciones y CI. Aprender el propósito del wrapper, las dependencias, los perfiles, Compose y las migraciones.

## Alcance propuesto

Inicialización de Git y remoto, base documental en `main`, estructura API y web, Java 21, Maven Wrapper, pnpm, PostgreSQL y Compose, Flyway, health, pruebas básicas, GitHub Actions y documentación de arranque. Las versiones se comprobarán en fuentes oficiales antes de seleccionarlas y quedarán registradas en el plan.

Identidad, reservas, lógica multiempresa, despliegue productivo y funciones de negocio corresponden a sprints posteriores.

## Historias y flujos

| ID | Historia | Criterios |
|---|---|---|
| HU-01 | Como desarrollador quiero arrancar el proyecto desde una copia limpia para trabajar con un entorno reproducible. | CA-01, CA-02, CA-03 |
| HU-02 | Como desarrollador quiero verificar automáticamente API y web para detectar fallos antes de integrar cambios. | CA-04, CA-05 |
| HU-03 | Como aprendiz quiero comprender y ejecutar parte del arranque para entender las herramientas. | CA-06 |

Flujo normal: preparar requisitos, iniciar base de datos, ejecutar migración y arrancar API y web. Flujo alternativo: registrar dependencia o servicio ausente y diagnóstico sin presentar la comprobación como aprobada.

## Dependencias y riesgos

El remoto consultado el 27-11-2026 no devolvió referencias y la carpeta local no tiene Git. Debe confirmarse el acceso de escritura antes de publicar. Versiones, herramientas locales y ejecución de CI aún no están verificadas. Las decisiones funcionales de reservas quedan para sus sprints.

## Seguimiento

- [x] Plan aprobado
- [x] Implementación
- [x] Verificación
- [x] Cierre aprobado y merge comprobado

[Plan](implementation-plan.md) · [Aceptación](acceptance-and-test-plan.md) · [Acta](approval.md)
