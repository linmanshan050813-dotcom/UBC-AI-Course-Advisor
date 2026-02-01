/**
 * Chat Bubble Component (DUT)
 * Displays a single chat message (user or AI) with optional citations.
 * Citations are rendered as interactive chips.
 * Used in Phase 2+ of the AI Course Advisor project.
 */

import React from 'react'
import { Citation } from '../lib/apiClient'
import './ChatBubble.css'

interface ChatBubbleProps {
  message: string
  isUser: boolean
  citations?: Citation[]
  onCitationClick?: (citation: Citation) => void
}

/**
 * Render chat message bubble.
 * 
 * @param message message text content
 * @param isUser true if message is from user, false if from AI
 * @param citations list of citations (AI only)
 * @param onCitationClick callback when citation chip is clicked
 * @returns JSX chat bubble element
 */
export function ChatBubble({ message, isUser, citations = [], onCitationClick }: ChatBubbleProps) {
  return (
    <div className={`chat-bubble ${isUser ? 'chat-bubble-user' : 'chat-bubble-ai'}`}>
      <div className="chat-bubble-content">
        <p className="chat-bubble-text">{message}</p>
        {!isUser && citations.length > 0 && (
          <div className="chat-bubble-citations">
            {citations.map((citation, index) => (
              <button
                key={citation.id}
                className="citation-chip"
                onClick={() => onCitationClick?.(citation)}
              >
                <span className="citation-chip-icon">📎</span>
                <span className="citation-chip-text">Source {index + 1}</span>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
