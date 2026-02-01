import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import LoginPage from '../../../app/login/page'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { BrowserRouter } from 'react-router-dom'

// Mock supabase
const mockSignUp = vi.fn()
const mockSignInWithPassword = vi.fn()

vi.mock('../../../lib/supabaseClient', () => ({
  supabase: {
    auth: {
      signUp: (...args) => mockSignUp(...args),
      signInWithPassword: (...args) => mockSignInWithPassword(...args)
    }
  }
}))

// Mock navigate
const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

// Mock alert
window.alert = vi.fn()

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders sign in form by default', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )
    expect(screen.getByRole('heading', { name: 'Sign In' })).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Email')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Sign In' })).toBeInTheDocument()
  })

  it('toggles to sign up mode', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )
    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' }))
    expect(screen.getByRole('heading', { name: 'Sign Up' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Sign Up' })).toBeInTheDocument()
  })

  it('handles successful sign in', async () => {
    mockSignInWithPassword.mockResolvedValue({ error: null })
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )
    
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Sign In' }))
    
    await waitFor(() => {
      expect(mockSignInWithPassword).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password'
      })
      expect(mockNavigate).toHaveBeenCalledWith('/')
    })
  })

  it('handles sign in error', async () => {
    mockSignInWithPassword.mockResolvedValue({ error: new Error('Invalid credentials') })
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )
    
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Sign In' }))
    
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument()
    })
  })

  it('handles successful sign up', async () => {
    mockSignUp.mockResolvedValue({ error: null })
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )
    
    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' })) // Switch to sign up
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Sign Up' })) // Submit
    
    await waitFor(() => {
      expect(mockSignUp).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password'
      })
      expect(window.alert).toHaveBeenCalledWith('Check your email for the login link!')
    })
  })
})
