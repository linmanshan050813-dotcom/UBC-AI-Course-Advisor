import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { ChatBubble } from '../../components/ChatBubble'
import { describe, it, expect, vi } from 'vitest'
import { Citation } from '../../lib/apiClient'

describe('ChatBubble', () => {
  it('renders user message correctly', () => {
    render(<ChatBubble message="Hello world" isUser={true} />)
    expect(screen.getByText('Hello world')).toBeInTheDocument()
    // User message should not show citations even if provided (though type suggests it might not matter, logic says AI usually has citations)
  })

  it('renders AI message correctly', () => {
    render(<ChatBubble message="AI response" isUser={false} />)
    expect(screen.getByText('AI response')).toBeInTheDocument()
  })

  it('renders citations for AI message', () => {
    const citations: Citation[] = [
      { id: '1', text: 'Citation 1 text' },
      { id: '2', text: 'Citation 2 text' }
    ]
    render(<ChatBubble message="AI response" isUser={false} citations={citations} />)
    expect(screen.getByText('Source 1')).toBeInTheDocument()
    expect(screen.getByText('Source 2')).toBeInTheDocument()
  })

  it('handles citation click', () => {
    const citations: Citation[] = [
      { id: '1', text: 'Citation 1 text' }
    ]
    const handleCitationClick = vi.fn()
    render(
      <ChatBubble 
        message="AI response" 
        isUser={false} 
        citations={citations} 
        onCitationClick={handleCitationClick} 
      />
    )
    
    fireEvent.click(screen.getByText('Source 1'))
    expect(handleCitationClick).toHaveBeenCalledWith(citations[0])
  })
})
