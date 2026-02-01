import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { Navbar } from '../../components/Navbar'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { BrowserRouter } from 'react-router-dom'

// Mock context and hooks
const mockSignOut = vi.fn()
const mockNavigate = vi.fn()
let mockUser: any = null

vi.mock('../../context/AuthContext', () => ({
  useAuth: () => ({
    user: mockUser,
    signOut: mockSignOut
  })
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ pathname: '/' })
  }
})

describe('Navbar', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUser = null
  })

  it('renders correctly when logged out', () => {
    render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    )
    expect(screen.getByText('AI Course Advisor')).toBeInTheDocument()
    expect(screen.getByText('Home')).toBeInTheDocument()
    expect(screen.getByText('Sign In')).toBeInTheDocument()
    expect(screen.queryByText('Upload')).not.toBeInTheDocument()
  })

  it('renders correctly when logged in', () => {
    mockUser = { id: '1', email: 'test@example.com' }
    render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    )
    expect(screen.getByText('Upload')).toBeInTheDocument()
    expect(screen.getByText('Ask')).toBeInTheDocument()
    expect(screen.getByText('History')).toBeInTheDocument()
    expect(screen.getByText('Sign Out')).toBeInTheDocument()
    expect(screen.queryByText('Sign In')).not.toBeInTheDocument()
  })

  it('handles sign out', async () => {
    mockUser = { id: '1', email: 'test@example.com' }
    render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    )
    fireEvent.click(screen.getByText('Sign Out'))
    expect(mockSignOut).toHaveBeenCalled()
    // navigate is called after await, might need wait
  })
})
