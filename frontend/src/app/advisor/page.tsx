/**
 * Advisor Page (DUT)
 * Legacy/Alternative advisor interface.
 * Combines upload and chat in a single page.
 * Used in Phase 1 of the AI Course Advisor project.
 */

import React, { useState, useEffect, useRef } from 'react'
import { uploadFile, askQuestion, getCitation, fetchHistory, addHistory, type UploadResponse, type QueryResponse, type Citation, type HistoryItem } from '../../lib/apiClient'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'
import { Input } from '../../components/Input'
import { ChatBubble } from '../../components/ChatBubble'
import { ChatInput } from '../../components/ChatInput'
import { CitationView } from '../../components/CitationView'
import '../../styles/page.css'
import './advisor.css'

interface Message {
  id: string
  text: string
  isUser: boolean
  citations?: Citation[]
  timestamp?: string
}

/**
 * Render Advisor page.
 * Displays upload section and chat interface side-by-side or stacked.
 * 
 * @returns JSX page content
 */
export default function AdvisorPage() {
  // Upload state
  const [file, setFile] = useState<File | null>(null)
  const [uploading, setUploading] = useState(false)
  const [uploadResult, setUploadResult] = useState<UploadResponse | null>(null)
  const [uploadError, setUploadError] = useState<string | null>(null)

  // Chat state
  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(false)
  const [historyLoading, setHistoryLoading] = useState(true)
  const [selectedCitation, setSelectedCitation] = useState<{ id: string; text: string } | null>(null)
  const [citationLoading, setCitationLoading] = useState(false)

  // Auto-scroll ref
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // Load history on component mount
  useEffect(() => {
    const loadHistory = async () => {
      setHistoryLoading(true)
      try {
        const historyItems = await fetchHistory()
        
        // Convert history entries to messages in chronological order (old → new)
        const historyMessages: Message[] = []
        const reversedHistory = [...historyItems].reverse()
        
        reversedHistory.forEach((item) => {
          historyMessages.push({
            id: `history-user-${item.id}`,
            text: item.question,
            isUser: true,
            timestamp: item.timestamp,
          })
          
          historyMessages.push({
            id: `history-ai-${item.id}`,
            text: item.answer,
            isUser: false,
            timestamp: item.timestamp,
          })
        })
        
        setMessages(historyMessages)
      } catch (error) {
        console.error('Failed to load history:', error)
      } finally {
        setHistoryLoading(false)
      }
    }

    loadHistory()
  }, [])

  // Auto-scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, loading])

  // Upload handlers
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0]
    if (selectedFile) {
      setFile(selectedFile)
      setUploadResult(null)
      setUploadError(null)
    }
  }

  const handleUpload = async () => {
    if (!file) {
      setUploadError('Please select a file')
      return
    }

    setUploading(true)
    setUploadError(null)
    setUploadResult(null)

    try {
      const response = await uploadFile(file)
      setUploadResult(response)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Upload failed'
      setUploadError(errorMessage)
      console.error('Upload error:', err)
    } finally {
      setUploading(false)
    }
  }

  const handleReset = () => {
    setFile(null)
    setUploadResult(null)
    setUploadError(null)
    const fileInput = document.getElementById('file-input') as HTMLInputElement
    if (fileInput) {
      fileInput.value = ''
    }
  }

  // Chat handlers
  const handleSend = async (question: string) => {
    const userMessage: Message = {
      id: `user-${Date.now()}`,
      text: question,
      isUser: true,
      timestamp: new Date().toISOString(),
    }
    setMessages(prev => [...prev, userMessage])
    setLoading(true)

    try {
      const response: QueryResponse = await askQuestion(question)

      const aiMessage: Message = {
        id: `ai-${Date.now()}`,
        text: response.answer,
        isUser: false,
        citations: response.citations,
        timestamp: new Date().toISOString(),
      }
      setMessages(prev => [...prev, aiMessage])

      try {
        await addHistory(question, response.answer)
      } catch (historyError) {
        console.error('Failed to save history:', historyError)
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

  const handleCitationClick = async (citation: Citation) => {
    setCitationLoading(true)
    try {
      const detail = await getCitation(citation.id)
      setSelectedCitation(detail)
    } catch (error) {
      console.error('Failed to load citation:', error)
      setSelectedCitation({
        id: citation.id,
        text: citation.content || citation.text || '',
      })
    } finally {
      setCitationLoading(false)
    }
  }

  return (
    <div className="page-container advisor-page">
      {/* Upload Section */}
      <div className="advisor-section">
        <div className="page-header">
          <h1>Upload Course Syllabus</h1>
          <p className="page-subtitle">Upload a PDF, DOCX, or TXT file to get started</p>
        </div>

        <Card className="upload-card">
          <div className="upload-section">
            <Input
              id="file-input"
              type="file"
              accept=".pdf,.docx,.txt"
              onChange={handleFileChange}
              disabled={uploading}
            />
            {file && (
              <div className="file-info">
                <p className="file-name">
                  <span className="text-secondary">Selected:</span> {file.name}
                </p>
                <p className="file-size text-secondary">
                  {(file.size / 1024 / 1024).toFixed(2)} MB
                </p>
              </div>
            )}
          </div>

          <div className="upload-actions">
            <Button
              onClick={handleUpload}
              disabled={!file || uploading}
            >
              {uploading ? 'Uploading...' : 'Upload File'}
            </Button>
            {(file || uploadResult) && (
              <Button
                variant="secondary"
                onClick={handleReset}
                disabled={uploading}
              >
                Reset
              </Button>
            )}
          </div>

          {uploadError && (
            <div className="error-message">
              <p>{uploadError}</p>
            </div>
          )}

          {uploadResult && (
            <div className="upload-result">
              <h3>Upload Successful</h3>
              <div className="result-content">
                <div className="result-item">
                  <span className="result-label">Filename:</span>
                  <span className="result-value">{uploadResult.filename}</span>
                </div>
              </div>
            </div>
          )}
        </Card>
      </div>

      {/* Divider */}
      <div className="advisor-divider">
        <h2>Ask AI</h2>
        <p className="page-subtitle">Ask questions about your uploaded course materials</p>
      </div>

      {/* Chat Section */}
      <div className="advisor-section chat-section">
        <Card className="chat-card">
          <div className="chat-container">
            <div className="chat-messages">
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
              <div ref={messagesEndRef} />
            </div>

            <div className="chat-input-container">
              <ChatInput onSend={handleSend} disabled={loading} />
            </div>
          </div>
        </Card>
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
