import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { Input } from '../../components/Input'
import { describe, it, expect, vi } from 'vitest'

describe('Input', () => {
  it('renders correctly with label', () => {
    render(<Input label="Username" id="username" />)
    expect(screen.getByLabelText('Username')).toBeInTheDocument()
  })

  it('renders error message', () => {
    render(<Input error="Invalid input" />)
    expect(screen.getByText('Invalid input')).toBeInTheDocument()
    expect(screen.getByRole('textbox')).toHaveClass('input-error')
  })

  it('handles text input changes', () => {
    const handleChange = vi.fn()
    render(<Input onChange={handleChange} />)
    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'test' } })
    expect(handleChange).toHaveBeenCalledTimes(1)
  })

  it('handles file input correctly', () => {
    const handleChange = vi.fn()
    render(<Input type="file" onChange={handleChange} label="Upload" />)
    
    const file = new File(['hello'], 'hello.png', { type: 'image/png' })
    const input = screen.getByLabelText('Upload')
    
    // Check initial state
    expect(screen.getByText('No file selected')).toBeInTheDocument()

    // Upload file
    fireEvent.change(input, { target: { files: [file] } })
    
    expect(handleChange).toHaveBeenCalledTimes(1)
    expect(screen.getByText('hello.png')).toBeInTheDocument()
  })
})
