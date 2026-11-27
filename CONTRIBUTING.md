# Flujo Git y calidad

Ramas: `main`, `sprint/00-bootstrap`, `sprint/01-backend-foundation`, etc. Crear cada rama desde main actualizado. No trabajar directamente en main.

```
git switch main
git pull --ff-only
git switch -c sprint/00-bootstrap
# implementar y verificar
git add .
git commit -m "feat: inicializar infraestructura del proyecto"
git push -u origin sprint/00-bootstrap
```

Abrir PR, revisar CI y cambios, fusionar tras aprobación; después actualizar main y eliminar rama local/remota si corresponde. No forzar push ni eliminar ramas ajenas.

Tipos: feat, fix, docs, test, refactor, chore, ci. Ejemplos: `test: validar aislamiento entre organizaciones`, `fix: corregir superposición de intervalos`.

Definition of Done: criterios funcionales satisfechos, pruebas ejecutadas, migraciones verificadas, sin secretos, documentación actualizada, CI verde y revisión aprobada. Los comandos definitivos se fijan en Sprint 00.

Para ejecución detallada y puertas de aprobación, aplicar [SPRINT\_WORKFLOW.md](docs/engineering/SPRINT_WORKFLOW.md) y [ENGINEERING\_STANDARDS.md](docs/engineering/ENGINEERING_STANDARDS.md). Los cuatro documentos por sprint son obligatorios.