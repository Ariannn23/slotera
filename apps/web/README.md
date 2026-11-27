# Web Slotera

Aplicación React, Vite y TypeScript con acceso OIDC y perfil autenticado. Requiere Node 24 y pnpm 10.33.0.

Desde esta carpeta ejecutar `pnpm install --frozen-lockfile` y `pnpm dev --host localhost --port 5173 --strictPort`. `pnpm lint` verifica el código, `pnpm test` ejecuta pruebas y `pnpm build` compila. `pnpm exec playwright install chromium` instala el navegador de pruebas y `pnpm test:e2e` ejecuta el flujo real con los servicios y la API iniciados. Ver `src/features/identity/README.md`.
