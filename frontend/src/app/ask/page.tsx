/**
 * Ask Page (DUT)
 * Primary chat interface for asking questions about course materials.
 * Handles AI conversation, message history loading, and citation viewing.
 * Used in Phase 2+ of the AI Course Advisor project.
 */

import React, { useState, useEffect } from 'react'
import { askQuestion, getCitation, fetchHistory, addHistory, getUserUploads, type QueryResponse, type Citation, type HistoryItem } from '../../lib/apiClient'
import { ChatBubble } from '../../components/ChatBubble'
import { ChatInput } from '../../components/ChatInput'
import { CitationView } from '../../components/CitationView'
import '../../styles/page.css'
import './ask.css'

interface Message {
  id: string
  text: string
  isUser: boolean
  citations?: Citation[]
  timestamp?: string
}

/**
 * Render Ask/Chat page.
 * Loads previous conversation history and provides input for new questions.
 * 
 * @returns JSX page content
 */
export default function AskPage() {
  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(false)
  const [courseName, setCourseName] = useState<string | null>(null)
  const [historyLoading, setHistoryLoading] = useState(true)
  const [selectedCitation, setSelectedCitation] = useState<{ id: string; text: string } | null>(null)
  const [citationLoading, setCitationLoading] = useState(false)

  // Load history on component mount
  useEffect(() => {
    const loadHistory = async () => {
      setHistoryLoading(true)
      try {
        const historyItems = await fetchHistory()
        
        // Convert history entries to messages in chronological order (old → new)
        // History comes sorted newest first, so we reverse it
        const historyMessages: Message[] = []
        const reversedHistory = [...historyItems].reverse()
        
        reversedHistory.forEach((item) => {
          // Add user message
          historyMessages.push({
            id: `history-user-${item.id}`,
            text: item.question,
            isUser: true,
            timestamp: item.timestamp,
          })
          
          // Add AI message
          let citations: Citation[] | undefined = undefined;
          if (item.citations && Array.isArray(item.citations)) {
            // Convert string array to Citation objects
            citations = item.citations.map((text, index) => ({
              id: `hist-cit-${item.id}-${index}`,
              text: text
            }));
          }

          historyMessages.push({
            id: `history-ai-${item.id}`,
            text: item.answer,
            isUser: false,
            citations: citations,
            timestamp: item.timestamp,
          })
        })
        
        setMessages(historyMessages)
      } catch (error) {
        console.error('Failed to load history:', error)
        // Continue with empty messages if history load fails
      } finally {
        setHistoryLoading(false)
      }
    }

    loadHistory()
  }, [])

  const handleSend = async (question: string) => {
    // Add user message immediately
    const userMessage: Message = {
      id: `user-${Date.now()}`,
      text: question,
      isUser: true,
      timestamp: new Date().toISOString(),
    }
    setMessages(prev => [...prev, userMessage])
    setLoading(true)

    try {
      // Get selected doc ID from storage
      const docId = localStorage.getItem('selectedDocId')
      
      // Call API to get AI response
      const response: QueryResponse = await askQuestion(question, docId)

      // Add AI response immediately
      const aiMessage: Message = {
        id: `ai-${Date.now()}`,
        text: response.answer,
        isUser: false,
        citations: response.citations,
        timestamp: new Date().toISOString(),
      }
      setMessages(prev => [...prev, aiMessage])

      // Save to history AFTER getting the answer
      try {
        await addHistory(question, response.answer, response.citations)
      } catch (historyError) {
        console.error('Failed to save history:', historyError)
        // Don't fail the request if history save fails
      }
    } catch (error) {
      const errorMessage: Message = {
        id: `error-${Date.now()}`,
        text: error instanceof Error ? error.message : 'Request failed, please try again',
        isUser: false,
        timestamp: new Date().toISOString(),
      }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setLoading(false)
    }
  }

  // Fetch selected document details to get course name
  useEffect(() => {
    const loadCourseInfo = async () => {
      const docId = localStorage.getItem('selectedDocId')
      if (docId) {
        try {
          const uploads = await getUserUploads();
          const doc = uploads.find(u => u.id === docId);
          if (doc) {
             setCourseName((doc as any).courseName || doc.originalFilename);
          }
        } catch (e) {
          console.error("Failed to load course info", e);
        }
      }
    }
    loadCourseInfo();
  }, []);

  const handleCitationClick = async (citation: Citation) => {
    if (citation.text) {
      setSelectedCitation({
        id: citation.id,
        text: citation.text
      })
      return
    }

    setCitationLoading(true)
    try {
      const detail = await getCitation(citation.id)
      setSelectedCitation(detail)
    } catch (error) {
      console.error('Failed to load citation:', error)
      // Fallback to content if text missing
      setSelectedCitation({
        id: citation.id,
        text: citation.content || 'Citation unavailable',
      })
    } finally {
      setCitationLoading(false)
    }
  }

  return (
    <div className="page-container ask-page">
      <div className="page-header">
        <h1>Ask AI</h1>
        <p className="page-subtitle">Ask the AI any questions about the course</p>
      </div>

      <div className="chat-container">
        <div className="chat-messages">
          {courseName ? (
            <div style={{ 
              textAlign: 'center', 
              padding: '10px', 
              backgroundColor: '#e6fffa', 
              color: '#047857', 
              borderRadius: '8px',
              marginBottom: '1rem',
              fontSize: '0.9rem',
              fontWeight: 500,
              position: 'sticky',
              top: 0,
              zIndex: 10,
              boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
            }}>
              Now answering question about {courseName}
            </div>
          ) : (
            <div style={{ 
              textAlign: 'center', 
              padding: '10px', 
              backgroundColor: '#fee2e2', 
              color: '#dc2626', 
              borderRadius: '8px',
              marginBottom: '1rem',
              fontSize: '0.9rem',
              fontWeight: 500,
              position: 'sticky',
              top: 0,
              zIndex: 10,
              boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
            }}>
              Please select or upload a syllabus.
            </div>
          )}
          {historyLoading ? (
            <div className="chat-empty">
              <div className="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          ) : messages.length === 0 ? (
            <div className="chat-empty">
              <p className="text-secondary">Start a conversation, ask the AI questions about the course</p>
            </div>
          ) : (
            messages.map((message) => (
              <ChatBubble
                key={message.id}
                message={message.text}
                isUser={message.isUser}
                citations={message.citations}
                onCitationClick={handleCitationClick}
              />
            ))
          )}
          {loading && (
            <div className="chat-bubble chat-bubble-ai">
              <div className="chat-bubble-content">
                <div className="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="chat-input-container">
          <ChatInput onSend={handleSend} disabled={loading || !courseName} />
        </div>
      </div>

      {selectedCitation && (
        <CitationView
          citation={selectedCitation}
          question={messages.find(m => m.isUser)?.text}
          onClose={() => setSelectedCitation(null)}
        />
      )}
    </div>
  )
}
