/**
 * History Page (DUT)
 * Displays the user's past Q&A interactions.
 * Provides options to view citations, delete entries, or clear history.
 * Used in Phase 3+ of the AI Course Advisor project.
 */

import React, { useState, useEffect } from 'react'
import { fetchHistory, deleteHistoryEntry, clearHistory, type HistoryItem, type Citation } from '../../lib/apiClient'
import { Button } from '../../components/Button'
import { CitationView } from '../../components/CitationView'
import '../../components/ChatBubble.css'
import '../../styles/page.css'
import './history.css'

/**
 * Render Q/A history list.
 * 
 * @returns JSX page content
 */
export default function HistoryPage() {
  const [history, setHistory] = useState<HistoryItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedCitation, setSelectedCitation] = useState<{ id: string; text: string } | null>(null)

  const loadHistory = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchHistory()
      setHistory(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load history')
    } finally {
      setLoading(false)
    }
  }

  const handleCitationClick = (citation: Citation) => {
    if (citation.text) {
      setSelectedCitation({
        id: citation.id,
        text: citation.text
      })
    }
  }

  useEffect(() => {
    loadHistory()
    
    // Auto-refresh every 2 seconds to catch new history items
    const interval = setInterval(loadHistory, 2000)
    return () => clearInterval(interval)
  }, [])

  return (
    <div className="page-container history-page">
      <div className="page-header">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
                <h1>History</h1>
                <p className="page-subtitle">View your Q&A history</p>
            </div>
            {history.length > 0 && (
                <Button variant="secondary" onClick={async () => {
                    if (window.confirm('Are you sure you want to clear all history?')) {
                        await clearHistory();
                        loadHistory();
                    }
                }}>
                    Clear History
                </Button>
            )}
        </div>
      </div>

      {loading && history.length === 0 ? (
        <div className="history-loading">
          <div className="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      ) : error ? (
        <div className="error-message">
          <p>{error}</p>
          <Button onClick={loadHistory}>
            Retry
          </Button>
        </div>
      ) : history.length === 0 ? (
        <div className="history-empty">
          <div className="history-empty-content">
            <p className="history-empty-text">Start by asking your first question!</p>
            <p className="history-empty-subtitle">History will be automatically saved after asking questions on the Advisor page</p>
          </div>
        </div>
      ) : (
        <div className="history-list">
          {history.map((item) => (
            <div key={item.id} className="history-card" style={{ position: 'relative' }}>
              <button 
                onClick={async (e) => {
                    e.stopPropagation();
                    if (window.confirm('Delete this entry?')) {
                        await deleteHistoryEntry(item.id);
                        loadHistory();
                    }
                }}
                style={{
                    position: 'absolute',
                    top: '10px',
                    right: '10px',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '1.2rem',
                    color: 'var(--text-secondary)'
                }}
                title="Delete"
              >
                ×
              </button>
              <div className="history-card-header">
                <span className="history-timestamp">{item.timestamp}</span>
              </div>
              <div className="history-card-content">
                <div className="history-question">
                  <span className="history-label">Q:</span>
                  <p>{item.question}</p>
                </div>
                <div className="history-answer" style={{ flexDirection: 'column' }}>
                  <div style={{ display: 'flex', gap: 'var(--spacing-sm)' }}>
                    <span className="history-label">A:</span>
                    <p>{item.answer}</p>
                  </div>
                  {item.citations && item.citations.length > 0 && (
                    <div className="chat-bubble-citations" style={{ marginTop: '0.5rem', marginLeft: '24px', borderTop: 'none', padding: 0 }}>
                      {item.citations.map((text, idx) => (
                        <button
                          key={idx}
                          className="citation-chip"
                          onClick={() => handleCitationClick({ id: `hist-cit-${item.id}-${idx}`, text })}
                        >
                          <span className="citation-chip-icon">📎</span>
                          <span className="citation-chip-text">Source {idx + 1}</span>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      {selectedCitation && (
        <CitationView
          citation={selectedCitation}
          onClose={() => setSelectedCitation(null)}
        />
      )}
    </div>
  )
}
