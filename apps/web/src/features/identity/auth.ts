import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:18088',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'slotera',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'slotera-web',
})

const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
let initialization: Promise<boolean> | undefined

export type Profile = {
  id: string
  email: string
  emailVerified: boolean
}

export function initializeSession(): Promise<boolean> {
  initialization ??= keycloak.init({
    onLoad: 'check-sso',
    flow: 'standard',
    pkceMethod: 'S256',
    checkLoginIframe: false,
    scope: 'profile email',
  })
  return initialization
}

export function signIn(): Promise<void> {
  return keycloak.login({ redirectUri: window.location.origin + '/' })
}

export function register(): Promise<void> {
  return keycloak.register({ redirectUri: window.location.origin + '/' })
}

export async function signOut(): Promise<void> {
  const logoutUrl = keycloak.createLogoutUrl({ redirectUri: window.location.origin + '/' })
  await keycloak.clearToken()
  window.location.assign(logoutUrl)
}

export async function refreshSession(): Promise<void> {
  await keycloak.updateToken(30)
}

export async function getProfile(signal?: AbortSignal): Promise<Profile> {
  await refreshSession()
  if (!keycloak.token) {
    throw new Error('SESSION_EXPIRED')
  }
  const response = await fetch(`${apiUrl}/api/v1/me`, {
    headers: { Authorization: `Bearer ${keycloak.token}` },
    signal,
  })
  if (!response.ok) {
    throw new Error(response.status === 401 ? 'SESSION_EXPIRED' : 'PROFILE_UNAVAILABLE')
  }
  return response.json() as Promise<Profile>
}

export async function clearSession(): Promise<void> {
  await keycloak.clearToken()
}
