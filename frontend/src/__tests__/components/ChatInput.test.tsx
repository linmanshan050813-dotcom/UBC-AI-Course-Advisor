import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { ChatInput } from '../../components/ChatInput'
import { describe, it, expect, vi } from 'vitest'

describe('ChatInput', () => {
  it('renders correctly', () => {
    render(<ChatInput onSend={() => {}} />)
    expect(screen.getByRole('textbox')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /send/i })).toBeInTheDocument()
  })

  it('updates input value', () => {
    render(<ChatInput onSend={() => {}} />)
    const input = screen.getByRole('textbox')
    fireEvent.change(input, { target: { value: 'test message' } })
    expect(input).toHaveValue('test message')
  })

  it('calls onSend when form submitted', () => {
    const handleSend = vi.fn()
    render(<ChatInput onSend={handleSend} />)
    const input = screen.getByRole('textbox')
    
    fireEvent.change(input, { target: { value: 'test message' } })
    fireEvent.submit(screen.getByRole('button', { name: /send/i }).closest('form')!)
    
    expect(handleSend).toHaveBeenCalledWith('test message')
    expect(input).toHaveValue('')
  })

  it('calls onSend when Enter key pressed', () => {
    const handleSend = vi.fn()
    render(<ChatInput onSend={handleSend} />)
    const input = screen.getByRole('textbox')
    
    fireEvent.change(input, { target: { value: 'test message' } })
    fireEvent.keyPress(input, { key: 'Enter', code: 'Enter', charCode: 13 })
    
    expect(handleSend).toHaveBeenCalledWith('test message')
  })

  it('does not send empty message', () => {
    const handleSend = vi.fn()
    render(<ChatInput onSend={handleSend} />)
    const button = screen.getByRole('button', { name: /send/i })
    
    expect(button).toBeDisabled()
    
    fireEvent.click(button)
    expect(handleSend).not.toHaveBeenCalled()
  })

  it('is disabled when disabled prop is true', () => {
    render(<ChatInput onSend={() => {}} disabled={true} />)
    expect(screen.getByRole('textbox')).toBeDisabled()
    expect(screen.getByRole('button', { name: /send/i })).toBeDisabled()
  })
})
