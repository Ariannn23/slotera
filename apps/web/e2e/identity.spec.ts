import { expect, test } from '@playwright/test'
import { randomBytes } from 'node:crypto'

const mailpit = process.env.MAILPIT_URL ?? 'http://127.0.0.1:18025'
const api = process.env.API_URL ?? 'http://localhost:18089'

test('registro, verificación, acceso, cierre y recuperación con Keycloak', async ({ page, request }) => {
  const suffix = `${Date.now()}${Math.floor(Math.random() * 10000)}`
  const email = `sprint02-${suffix}@example.test`
  const username = `sprint02${suffix}`
  const password = `Aa1!${randomBytes(20).toString('hex')}`
  const newPassword = `Bb2!${randomBytes(20).toString('hex')}`
  let accessToken = ''
  let idToken = ''
  let refreshToken = ''
  let pkceObserved = false
  let refreshObserved = false
  const pageErrors: string[] = []
  page.on('pageerror', error => {
    if (new URL(page.url()).origin === 'http://localhost:5173') pageErrors.push(error.message)
  })

  page.on('request', outgoing => {
    if (outgoing.url().includes('/protocol/openid-connect/registrations')) {
      pkceObserved = new URL(outgoing.url()).searchParams.get('code_challenge_method') === 'S256'
    }
    if (outgoing.url().endsWith('/protocol/openid-connect/token') && outgoing.postData()?.includes('grant_type=refresh_token')) {
      refreshObserved = true
    }
  })
  page.on('response', async response => {
    if (response.url().endsWith('/protocol/openid-connect/token') && response.ok()) {
      const body = await response.json().catch(() => null)
      if (body?.access_token && body?.id_token) {
        accessToken = body.access_token as string
        idToken = body.id_token as string
        refreshToken = body.refresh_token as string
      }
    }
  })

  async function emailLink(subject: string): Promise<string> {
    let link = ''
    await expect.poll(async () => {
      const listing = await (await request.get(`${mailpit}/api/v1/messages`)).json()
      const message = (listing.messages ?? []).find((item: { Subject: string, To: { Address: string }[] }) =>
        item.Subject === subject && item.To.some(recipient => recipient.Address === email))
      if (!message) return false
      const content = await (await request.get(`${mailpit}/api/v1/message/${message.ID}`)).json()
      link = /https?:\/\/\S+/.exec(content.Text)?.[0] ?? ''
      return Boolean(link)
    }, { timeout: 30_000 }).toBe(true)
    return link
  }

  await page.clock.install()
  await page.goto('/')
  await page.getByRole('button', { name: 'Crear cuenta' }).click()
  await expect(page.getByRole('heading', { name: 'Register' })).toBeVisible()
  expect(pkceObserved).toBe(true)
  await page.locator('#username').fill(username)
  await page.locator('#email').fill(email)
  await page.getByLabel('First name').fill('Prueba')
  await page.getByLabel('Last name').fill('Slotera')
  await page.getByRole('button', { name: 'Register' }).click()
  await page.goto(await emailLink('Verify email'))
  const confirm = page.getByRole('link', { name: /Click here to proceed/i })
  if (await confirm.isVisible()) await confirm.click()
  await page.getByLabel('New Password', { exact: true }).fill(password)
  await page.getByLabel('Confirm password').fill(password)
  await page.getByRole('button', { name: 'Submit' }).click()
  await page.goto('/')
  if (await page.getByRole('button', { name: 'Iniciar sesión' }).isVisible()) {
    await page.getByRole('button', { name: 'Iniciar sesión' }).click()
    if (await page.getByLabel('Username or email').isVisible()) {
      await page.getByLabel('Username or email').fill(username)
      await page.getByLabel('Password', { exact: true }).fill(password)
      await page.getByRole('button', { name: 'Sign In' }).click()
    }
  }
  await expect(page.getByText(email)).toBeVisible()
  await expect.poll(() => Boolean(accessToken && idToken && refreshToken)).toBe(true)
  await page.clock.fastForward('04:40')
  await expect.poll(() => refreshObserved).toBe(true)
  for (const width of [375, 768, 1280]) {
    await page.setViewportSize({ width, height: 850 })
    await expect(page.getByText(email)).toBeVisible()
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true)
  }
  expect((await request.get(`${api}/api/v1/me`, { headers: { Authorization: `Bearer ${accessToken}` } })).status()).toBe(200)
  expect((await request.get(`${api}/api/v1/me`, { headers: { Authorization: `Bearer ${idToken}` } })).status()).toBe(401)
  expect((await request.get(`${api}/api/v1/me`)).status()).toBe(401)
  await page.getByRole('button', { name: 'Cerrar sesión' }).click()
  await expect(page.getByRole('button', { name: 'Iniciar sesión' })).toBeVisible()
  expect((await request.get(`${api}/api/v1/me`, { headers: { Authorization: `Bearer ${accessToken}` } })).status()).toBe(200)

  await page.getByRole('button', { name: 'Iniciar sesión' }).click()
  await page.getByRole('link', { name: 'Forgot Password?' }).click()
  await page.getByLabel('Username or email').fill(email)
  await page.getByRole('button', { name: 'Submit' }).click()
  await page.goto(await emailLink('Reset password'))
  await page.getByLabel('New Password', { exact: true }).fill(newPassword)
  await page.getByLabel('Confirm password').fill(newPassword)
  await page.getByRole('button', { name: 'Submit' }).click()
  await expect(page.getByText(email)).toBeVisible()
  await page.getByRole('button', { name: 'Cerrar sesión' }).click()
  await page.getByRole('button', { name: 'Iniciar sesión' }).click()
  await page.getByLabel('Username or email').fill(username)
  await page.getByLabel('Password', { exact: true }).fill(newPassword)
  await page.getByRole('button', { name: 'Sign In' }).click()
  await expect(page.getByText(email)).toBeVisible()
  const tokensPersisted = await page.evaluate(({ access, refresh }) =>
    [localStorage, sessionStorage].some(storage =>
      Object.keys(storage).some(key => {
        const value = storage.getItem(key) ?? ''
        return value.includes(access) || value.includes(refresh)
      })), { access: accessToken, refresh: refreshToken })
  expect(tokensPersisted).toBe(false)
  expect(pageErrors).toEqual([])
  const invalidLinkPage = await page.context().newPage()
  await invalidLinkPage.goto('http://localhost:18088/realms/slotera/login-actions/action-token?key=invalid')
  await expect(invalidLinkPage.locator('#kc-error-message')).toContainText('error')
  await invalidLinkPage.close()
})
