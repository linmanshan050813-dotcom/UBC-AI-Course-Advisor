import React from 'react'
import { render, screen, waitFor, act } from '@testing-library/react'
import { AuthProvider, useAuth } from '../../context/AuthContext'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { supabase } from '../../lib/supabaseClient'

// Mock supabase
const mockGetSession = vi.fn()
const mockOnAuthStateChange = vi.fn()
const mockSignOut = vi.fn()
const mockUnsubscribe = vi.fn()

vi.mock('../../lib/supabaseClient', () => ({
  supabase: {
    auth: {
      getSession: (...args) => mockGetSession(...args),
      onAuthStateChange: (...args) => mockOnAuthStateChange(...args),
      signOut: (...args) => mockSignOut(...args)
    }
  }
}))

const TestComponent = () => {
  const { user, loading, signOut } = useAuth()
  if (loading) return <div>Loading...</div>
  return (
    <div>
      <div>{user ? `User: ${user.email}` : 'No user'}</div>
      <button onClick={signOut}>Sign Out</button>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockGetSession.mockResolvedValue({ data: { session: null } })
    mockOnAuthStateChange.mockReturnValue({ data: { subscription: { unsubscribe: mockUnsubscribe } } })
  })

  it('provides loading state initially', async () => {
    mockGetSession.mockReturnValue(new Promise(() => {})) // Pending promise
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })

  it('provides user when session exists', async () => {
    const mockUser = { id: '1', email: 'test@example.com' }
    const mockSession = { user: mockUser, access_token: 'token' }
    mockGetSession.mockResolvedValue({ data: { session: mockSession } })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await waitFor(() => {
      expect(screen.getByText('User: test@example.com')).toBeInTheDocument()
    })
  })

  it('updates user on auth state change', async () => {
    let authCallback: any
    mockOnAuthStateChange.mockImplementation((cb) => {
      authCallback = cb
      return { data: { subscription: { unsubscribe: mockUnsubscribe } } }
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await waitFor(() => {
      expect(screen.getByText('No user')).toBeInTheDocument()
    })

    const mockUser = { id: '1', email: 'new@example.com' }
    const mockSession = { user: mockUser, access_token: 'token' }
    
    act(() => {
      authCallback('SIGNED_IN', mockSession)
    })

    await waitFor(() => {
      expect(screen.getByText('User: new@example.com')).toBeInTheDocument()
    })
  })

  it('handles sign out', async () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await waitFor(() => {
      expect(screen.getByText('No user')).toBeInTheDocument()
    })

    const signOutButton = screen.getByText('Sign Out')
    await act(async () => {
        signOutButton.click()
    })
    
    expect(mockSignOut).toHaveBeenCalled()
  })
})
