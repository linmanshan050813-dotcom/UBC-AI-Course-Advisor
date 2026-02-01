import React from 'react'
import { render, screen } from '@testing-library/react'
import HomePage from '../../app/page'
import { describe, it, expect, vi } from 'vitest'
import { BrowserRouter } from 'react-router-dom'

// Mock useAuth
const mockUser = { id: '1' }
vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(() => ({ user: null }))
}))

import { useAuth } from '../../context/AuthContext'

describe('HomePage', () => {
  it('renders correctly for guests', () => {
    vi.mocked(useAuth).mockReturnValue({ user: null } as any)
    render(
      <BrowserRouter>
        <HomePage />
      </BrowserRouter>
    )
    expect(screen.getByText('AI Course Advisor')).toBeInTheDocument()
    expect(screen.getByText('Get Started →')).toBeInTheDocument()
  })

  it('renders correctly for authenticated users', () => {
    vi.mocked(useAuth).mockReturnValue({ user: mockUser } as any)
    render(
      <BrowserRouter>
        <HomePage />
      </BrowserRouter>
    )
    expect(screen.getByText('AI Course Advisor')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Upload Syllabus' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Ask Questions' })).toBeInTheDocument()
    expect(screen.queryByText('Get Started →')).not.toBeInTheDocument()
  })
})
