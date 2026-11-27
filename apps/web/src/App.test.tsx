import { renderToStaticMarkup } from 'react-dom/server'
import { expect, test, vi } from 'vitest'

vi.mock('./features/identity/auth', () => ({
  initializeSession: vi.fn(), getProfile: vi.fn(), refreshSession: vi.fn(),
  signIn: vi.fn(), register: vi.fn(), signOut: vi.fn(), clearSession: vi.fn(),
}))

import App from './App'

test('muestra el estado inicial accesible mientras comprueba la sesión', () => {
  const content = renderToStaticMarkup(<App />)
  expect(content).toContain('Preparando tu sesión')
  expect(content).toContain('role="status"')
})
