# Slotera

Plan maestro v0.1 · Desarrollo backend-first

SaaS multiempresa de reservas exclusivas de recursos físicos. Java 21, Spring Boot, PostgreSQL y React. Horizonte orientativo: 16 semanas / 160 horas. El Sprint 00 inicializó el proyecto; consultar el roadmap para el estado de cada sprint.

## Índice

*   [Producto](docs/product/PRODUCT.md)
*   [Requisitos](docs/product/REQUIREMENTS.md)
*   [Funcionalidades](docs/product/FEATURES.md)
*   [Reglas de negocio](docs/product/BUSINESS_RULES.md)
*   [Flujos](docs/product/FLOWS.md)
*   [Roadmap](docs/product/ROADMAP.md)
*   [Arquitectura](docs/architecture/ARCHITECTURE.md)
*   [Módulos](docs/architecture/MODULES.md)
*   [Despliegue](docs/architecture/DEPLOYMENT.md)
*   [Modelo de datos](docs/database/ERD.md)
*   [Diccionario](docs/database/DATA_DICTIONARY.md)
*   [API](docs/api/API_CONVENTIONS.md)
*   [Seguridad](docs/security/THREAT_MODEL.md)
*   [Pruebas](docs/testing/TEST_PLAN.md)
*   [Diseño](docs/design/UI_GUIDE.md)
*   [Aprendizaje](docs/learning/LEARNING.md)
*   [Operaciones](docs/operations/OPERATIONS.md)
*   [Decisiones pendientes](docs/adr/README.md)
*   [Instrucciones Codex](AGENTS.md)
*   [Git](CONTRIBUTING.md)

## Inicio

Requiere Java 21, Node 24, pnpm 10 y Docker Compose o Podman Compose. Copiar `.env.example` a `.env` y elegir credenciales locales para Keycloak y su base. Desde la raíz `podman compose up -d` inicia PostgreSQL de Slotera, PostgreSQL de identidad, Keycloak y Mailpit. El realm se importa al crear el entorno por primera vez; los cambios posteriores del archivo no actualizan un realm ya existente. Evitar borrar volúmenes con datos. Ver [API](apps/api/README.md) y [web](apps/web/README.md).

El entorno predeterminado usa PostgreSQL de Slotera en 55432, Keycloak en `http://localhost:18088` y Mailpit en `http://localhost:18025`. La API arranca por defecto en 8080 y la SPA en `http://localhost:5173`. Si un puerto está ocupado, configurar las variables de entorno y mantener coherentes el issuer, el origen web y las URI autorizadas en Keycloak. El realm incluido utiliza datos y correo locales. No utilizar `start-dev`, Mailpit ni credenciales de ejemplo en producción.

1. Leer AGENTS.md y las decisiones pendientes.
2. Revisar y aprobar el plan del sprint correspondiente antes de implementar.
3. Mantener los documentos actualizados junto con cada cambio.

## Metodología de ingeniería

*   [Instrucciones del agente](AGENTS.md)
*   [Ciclo de sprints](docs/engineering/SPRINT_WORKFLOW.md)
*   [Estándares](docs/engineering/ENGINEERING_STANDARDS.md)
*   [Plantillas](docs/engineering/templates/README.md)
