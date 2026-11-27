# Slotera — Instrucciones obligatorias para agentes

## Propósito y lectura inicial

Slotera es un SaaS multiempresa de reservas y un proyecto de aprendizaje de Java 21 y Spring Boot. Antes de cualquier tarea, leer `README.md`, `CONTRIBUTING.md`, `docs/product/ROADMAP.md`, los requisitos, reglas de negocio, arquitectura y ADR pertinentes. Aplicar [el ciclo de entrega](docs/engineering/SPRINT_WORKFLOW.md) y [los estándares de ingeniería](docs/engineering/ENGINEERING_STANDARDS.md). Usar las [plantillas](docs/engineering/templates/README.md).

## Mandatos

*   No codificar, instalar dependencias ni modificar configuración funcional antes de presentar el plan y recibir aprobación explícita del usuario. La documentación previa sí está permitida. No interpretar silencio como autorización.
*   No ampliar alcance, alterar arquitectura, cambiar dependencias o contratos sin justificarlo y obtener autorización. Si documentos discrepan, detener la parte afectada y solicitar resolución; no inventar requisitos.
*   Preservar trabajo ajeno, secretos y datos. No ejecutar operaciones destructivas, force push, despliegues productivos ni migraciones de producción sin autorización específica.
*   Mantener monolito modular, arquitectura hexagonal pragmática en reservas y disponibilidad, aislamiento por organización y consistencia de ocupaciones. No introducir microservicios ni sobreingeniería.
*   Priorizar el aprendizaje: explicar conceptos Java/Spring, comandos, alternativas y decisiones; ofrecer un ejercicio acotado al desarrollador, revisar su solución y explicar errores. No resolver automáticamente el ejercicio reservado al usuario salvo petición.
*   Crear pruebas y documentación junto al código. Nunca afirmar que una verificación pasó si no se ejecutó. Registrar bloqueos, resultados reales y riesgos.
*   Usar `main` como base de cada sprint, rama `sprint/XX-nombre`, commits atómicos en español y PR hacia `main`. No fusionar sin aprobación explícita, CI y Definition of Done satisfechos.
*   Crear un README por módulo nuevo de API o frontend; actualizarlo cuando cambie su contrato o comportamiento.
*   En cada turno de trabajo comunicar fase, cambios, pruebas y próximo paso. Al retomar, reconstruir estado a partir de Git y documentos; no asumir que un sprint está aprobado por existir un archivo.

## Puertas obligatorias

1.  Análisis y documentación previa → aprobación del plan.
2.  Implementación aislada → verificación y evidencia.
3.  Solicitud de cierre → aprobación humana del resultado y autorización de merge.
4.  Merge verificado → actualizar estado y eliminar exclusivamente la rama propia si corresponde.

No saltar puertas por conveniencia. Una prueba bloqueada no equivale a aprobada. El agente no se autoaprueba. Si no tiene acceso a Git remoto, navegador, CI o entorno de pruebas, registrar la limitación y entregar instrucciones para que el usuario complete la verificación.