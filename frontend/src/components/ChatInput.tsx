/**
 * Chat Input Component (DUT)
 * Input field for typing chat messages.
 * Handles submission via Enter key or Send button.
 * Used in Phase 2+ of the AI Course Advisor project.
 */

import React, { useState } from 'react'
import { Button } from './Button'
import './ChatInput.css'

interface ChatInputProps {
  onSend: (message: string) => void
  disabled?: boolean
  placeholder?: string
}

/**
 * Render chat input field with send button.
 * 
 * @param onSend callback when message is sent
 * @param disabled disable input and button
 * @param placeholder custom placeholder text
 * @returns JSX chat input form
 */
export function ChatInput({ onSend, disabled = false, placeholder = "Enter your question..." }: ChatInputProps) {
  const [message, setMessage] = useState('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (message.trim() && !disabled) {
      onSend(message.trim())
      setMessage('')
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSubmit(e)
    }
  }

  return (
    <form className="chat-input-form" onSubmit={handleSubmit}>
      <div className="chat-input-wrapper">
        <input
          type="text"
          className="chat-input"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          disabled={disabled}
        />
        <Button
          type="submit"
          disabled={!message.trim() || disabled}
          className="chat-send-button"
        >
          Send
        </Button>
      </div>
    </form>
  )
}
