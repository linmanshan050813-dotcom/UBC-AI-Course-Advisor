import React from 'react'
import { render, screen } from '@testing-library/react'
import App from '../App'
import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock context
vi.mock('../context/AuthContext', () => ({
  useAuth: vi.fn(() => ({ user: { id: '1' }, loading: false })),
  AuthProvider: ({ children }) => <div>{children}</div>
}))

// Mock components to avoid deep rendering
vi.mock('../components/Navbar', () => ({
  Navbar: () => <div>Navbar</div>
}))
vi.mock('../app/page', () => ({
  default: () => <div>HomePage</div>
}))
vi.mock('../app/login/page', () => ({
  default: () => <div>LoginPage</div>
}))
vi.mock('../app/advisor/page', () => ({
  default: () => <div>AdvisorPage</div>
}))
vi.mock('../app/upload/page', () => ({
  default: () => <div>UploadPage</div>
}))
vi.mock('../app/ask/page', () => ({
  default: () => <div>AskPage</div>
}))
vi.mock('../app/history/page', () => ({
  default: () => <div>HistoryPage</div>
}))
vi.mock('../app/test/page', () => ({
  default: () => <div>TestPage</div>
}))

import { useAuth } from '../context/AuthContext'

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders HomePage on default route', () => {
    window.history.pushState({}, 'Home', '/')
    render(<App />)
    expect(screen.getByText('HomePage')).toBeInTheDocument()
    expect(screen.getByText('Navbar')).toBeInTheDocument()
  })

  it('renders LoginPage', () => {
    window.history.pushState({}, 'Login', '/login')
    render(<App />)
    expect(screen.getByText('LoginPage')).toBeInTheDocument()
  })

  it('renders protected route when authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ user: { id: '1' }, loading: false })
    window.history.pushState({}, 'Upload', '/upload')
    render(<App />)
    expect(screen.getByText('UploadPage')).toBeInTheDocument()
  })

  it('redirects to login when not authenticated', () => {
    vi.mocked(useAuth).mockReturnValue({ user: null, loading: false })
    window.history.pushState({}, 'Upload', '/upload')
    render(<App />)
    // Should redirect to login, but testing redirection with MemoryRouter/history mock in full App is tricky without router props.
    // Since App uses BrowserRouter, we rely on window.location.
    // But wait, if it redirects to /login, we should see LoginPage
    expect(screen.getByText('LoginPage')).toBeInTheDocument()
  })

  it('shows loading state', () => {
    vi.mocked(useAuth).mockReturnValue({ user: null, loading: true })
    window.history.pushState({}, 'Upload', '/upload')
    render(<App />)
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })
})
