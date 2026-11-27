# ADR-002 — Aislamiento multiempresa

Estado: propuesto, pendiente de aprobación.

## Decisión
BD y esquema compartidos con organization_id, membresías verificadas, consultas y FK acotadas.

## Alternativas
Esquema/BD por empresa: mayor coste operativo; RLS como defensa adicional a evaluar.

## Consecuencias
Pruebas de aislamiento obligatorias.
