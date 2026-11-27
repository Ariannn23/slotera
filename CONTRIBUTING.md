# Flujo Git y calidad

Sprint 00 continúa desde y hacia `main`. Después de su cierre, crear `develop` desde `main` y crear cada nueva rama de sprint desde `develop` actualizado. Los PR de sprint apuntan a `develop`. La promoción de `develop` a `main` requiere autorización y revisión específicas.

```
git switch develop
git pull --ff-only
git switch -c sprint/01-backend-foundation
# implementar y verificar
git add .
git commit -m "feat: inicializar infraestructura del proyecto"
git push -u origin sprint/01-backend-foundation
```

Abrir PR hacia develop, revisar CI y cambios, fusionar tras aprobación; después actualizar develop y eliminar rama local/remota si corresponde. No forzar push ni eliminar ramas ajenas.

Tipos: feat, fix, docs, test, refactor, chore, ci. Ejemplos: `test: validar aislamiento entre organizaciones`, `fix: corregir superposición de intervalos`.

Definition of Done: criterios funcionales satisfechos, pruebas ejecutadas, migraciones verificadas, sin secretos, documentación actualizada, CI verde y revisión aprobada. Los comandos definitivos se fijan en Sprint 00.

Para ejecución detallada y puertas de aprobación, aplicar [SPRINT\_WORKFLOW.md](docs/engineering/SPRINT_WORKFLOW.md) y [ENGINEERING\_STANDARDS.md](docs/engineering/ENGINEERING_STANDARDS.md). Los cuatro documentos por sprint son obligatorios.
