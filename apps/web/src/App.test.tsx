import { renderToStaticMarkup } from 'react-dom/server'
import { expect, test } from 'vitest'
import App from './App'

test('renderiza la pantalla inicial', () => {
  expect(renderToStaticMarkup(<App />)).toContain('Get started')
})
