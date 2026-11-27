import { useEffect, useState } from 'react'
import { clearSession, getProfile, initializeSession, refreshSession, register, signIn, signOut } from './features/identity/auth'
import type { Profile } from './features/identity/auth'
import './App.css'

type SessionState = 'loading' | 'anonymous' | 'authenticated' | 'error' | 'expired'

function App() {
  const [state, setState] = useState<SessionState>('loading')
  const [profile, setProfile] = useState<Profile | null>(null)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    let active = true
    const controller = new AbortController()
    async function start() {
      try {
        const authenticated = await initializeSession()
        if (!active) return
        if (!authenticated) {
          setState('anonymous')
          return
        }
        const currentProfile = await getProfile(controller.signal)
        if (active) {
          setProfile(currentProfile)
          setState('authenticated')
        }
      } catch (error) {
        if (active) setState(error instanceof Error && error.message === 'SESSION_EXPIRED' ? 'expired' : 'error')
      }
    }
    void start()
    return () => {
      active = false
      controller.abort()
    }
  }, [])

  useEffect(() => {
    if (state !== 'authenticated') return
    const timer = window.setInterval(() => {
      void refreshSession().catch(() => {
        void clearSession()
        setProfile(null)
        setState('expired')
      })
    }, 30_000)
    return () => window.clearInterval(timer)
  }, [state])

  async function run(action: () => Promise<void>) {
    setBusy(true)
    try {
      await action()
    } catch {
      setState('error')
      setBusy(false)
    }
  }

  return (
    <main className="page">
      <div className="shell">
        <a className="brand" href="/" aria-label="Slotera, inicio">slotera<span>.</span></a>
        <section className="card" aria-labelledby="page-title">
          {state === 'loading' && <><h1 id="page-title">Preparando tu sesión</h1><p role="status">Un momento, por favor.</p></>}
          {state === 'anonymous' && <>
            <p className="eyebrow">Tu espacio de reservas</p>
            <h1 id="page-title">Bienvenido a Slotera</h1>
            <p>Inicia sesión para acceder a tu cuenta o crea una nueva.</p>
            <div className="actions">
              <button disabled={busy} onClick={() => void run(signIn)}>Iniciar sesión</button>
              <button className="secondary" disabled={busy} onClick={() => void run(register)}>Crear cuenta</button>
            </div>
            <p className="help">¿Olvidaste tu contraseña? Puedes recuperarla desde la pantalla de acceso.</p>
          </>}
          {state === 'authenticated' && profile && <>
            <p className="eyebrow">Tu cuenta</p>
            <h1 id="page-title">Sesión iniciada</h1>
            <p>Este es el correo asociado a tu perfil.</p>
            <div className="profile"><span>Correo electrónico</span><strong>{profile.email}</strong><small>Correo verificado</small></div>
            <button className="secondary" disabled={busy} onClick={() => void run(signOut)}>Cerrar sesión</button>
          </>}
          {state === 'expired' && <>
            <h1 id="page-title">Tu sesión terminó</h1>
            <p role="status">Inicia sesión nuevamente para continuar.</p>
            <button disabled={busy} onClick={() => void run(signIn)}>Iniciar sesión</button>
          </>}
          {state === 'error' && <>
            <h1 id="page-title">No pudimos abrir tu cuenta</h1>
            <p role="alert">Revisa tu conexión y confirma que tu correo esté verificado.</p>
            <button onClick={() => window.location.reload()}>Volver a intentar</button>
          </>}
        </section>
        <p className="footer">Reservas y disponibilidad en un solo lugar.</p>
      </div>
    </main>
  )
}

export default App
