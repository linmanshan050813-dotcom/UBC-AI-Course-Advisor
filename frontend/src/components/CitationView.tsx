/**
 * Citation View Component (DUT)
 * Modal overlay displaying detailed citation content.
 * Highlights keywords from the user's question within the text.
 * Used in Phase 2+ of the AI Course Advisor project.
 */

import React, { useEffect } from 'react'
import { Card } from './Card'
import './CitationView.css'

const STOP_WORDS = new Set([
  'the','and','for','with','from','that','this','have','will','your','about','into',
  'their','there','which','would','could','should','while','where','when','what',
  'many','most','more','less','than','then','also','been','were','being','such',
  'because','before','after','between','within','each','only','they','them','ours',
  'you','yours','students','course','class','score','points','percent'
])

interface CitationViewProps {
  citation: {
    id: string
    text: string
  }
  question?: string
  onClose: () => void
}

/**
 * Render citation detail modal.
 * 
 * @param citation citation object with id and text
 * @param question original user question for highlighting context
 * @param onClose callback to close the modal
 * @returns JSX citation overlay element
 */
export function CitationView({ citation, question, onClose }: CitationViewProps) {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose()
      }
    }
    document.addEventListener('keydown', handleEscape)
    return () => document.removeEventListener('keydown', handleEscape)
  }, [onClose])

  const extractHighlightKeywords = (text: string, question?: string) => {
    if (!question || question.trim().length === 0) {
      return []
    }
    const textLower = text.toLowerCase()
    const tokens = question.toLowerCase().match(/\b[\w']+\b/g) || []
    const keywords: string[] = []
    for (const token of tokens) {
      const cleaned = token.replace(/[^a-z0-9]/g, '')
      if (cleaned.length < 4) continue
      if (STOP_WORDS.has(cleaned)) continue
      if (!textLower.includes(cleaned)) continue
      if (!keywords.includes(cleaned)) {
        keywords.push(cleaned)
      }
      if (keywords.length >= 5) {
        break
      }
    }
    return keywords
  }

  // Highlight keywords from question
  const highlightText = (text: string, questionText?: string) => {
    const keywordArray = extractHighlightKeywords(text, questionText)
    if (keywordArray.length === 0) {
      return text
    }

    let highlighted = text
    keywordArray.forEach(keyword => {
      const regex = new RegExp(`(${keyword})`, 'gi')
      highlighted = highlighted.replace(regex, '<mark>$1</mark>')
    })

    return highlighted
  }

  const highlightedText = highlightText(citation.text, question)

  return (
    <div className="citation-overlay" onClick={onClose}>
      <Card className="citation-modal">
        <div className="citation-header">
          <h3 className="citation-title">Citation Source</h3>
          <button className="citation-close" onClick={onClose} aria-label="Close">
            ×
          </button>
        </div>
        <div className="citation-content">
          <div className="citation-id">
            <span className="citation-id-label">ID:</span>
            <span className="citation-id-value">{citation.id}</span>
          </div>
          <div 
            className="citation-text"
            dangerouslySetInnerHTML={{ __html: highlightedText }}
          />
        </div>
      </Card>
    </div>
  )
}
