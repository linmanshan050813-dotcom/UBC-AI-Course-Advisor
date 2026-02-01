import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import AskPage from '../../../app/ask/page'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as apiClient from '../../../lib/apiClient'

// Mock apiClient
vi.mock('../../../lib/apiClient', () => ({
  askQuestion: vi.fn(),
  getCitation: vi.fn(),
  fetchHistory: vi.fn(),
  addHistory: vi.fn(),
  getUserUploads: vi.fn()
}))

// Mock localStorage
const localStorageMock = (() => {
  let store: any = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => { store[key] = value.toString() },
    removeItem: (key: string) => { delete store[key] },
    clear: () => { store = {} }
  }
})()
Object.defineProperty(window, 'localStorage', { value: localStorageMock })

describe('AskPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorageMock.clear()
    vi.mocked(apiClient.fetchHistory).mockResolvedValue([])
    vi.mocked(apiClient.getUserUploads).mockResolvedValue([])
  })

  it('renders correctly with no syllabus selected', async () => {
    render(<AskPage />)
    await waitFor(() => {
      expect(screen.getByText('Ask AI')).toBeInTheDocument()
      expect(screen.getByText('Please select or upload a syllabus.')).toBeInTheDocument()
      expect(screen.getByPlaceholderText('Enter your question...')).toBeDisabled()
    })
  })

  it('renders correctly with syllabus selected', async () => {
    localStorageMock.setItem('selectedDocId', 'doc1')
    vi.mocked(apiClient.getUserUploads).mockResolvedValue([
      { id: 'doc1', filename: 'syllabus.pdf', originalFilename: 'Syllabus', uploadedAt: 'now' }
    ])

    render(<AskPage />)

    await waitFor(() => {
      expect(screen.getByText('Now answering question about Syllabus')).toBeInTheDocument()
      expect(screen.getByPlaceholderText('Enter your question...')).not.toBeDisabled()
    })
  })

  it('loads history on mount', async () => {
    const historyItems = [
      { id: '1', question: 'Q1', answer: 'A1', citations: ['C1'], timestamp: 'now' }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(historyItems)

    render(<AskPage />)

    await waitFor(() => {
      expect(screen.getByText('Q1')).toBeInTheDocument()
      expect(screen.getByText('A1')).toBeInTheDocument()
    })
  })

  it('sends question and displays answer', async () => {
    localStorageMock.setItem('selectedDocId', 'doc1')
    vi.mocked(apiClient.getUserUploads).mockResolvedValue([
      { id: 'doc1', filename: 'syllabus.pdf', originalFilename: 'Syllabus', uploadedAt: 'now' }
    ])
    vi.mocked(apiClient.askQuestion).mockResolvedValue({
      answer: 'This is the answer',
      citations: [{ id: 'c1', text: 'Citation text' }]
    })
    vi.mocked(apiClient.addHistory).mockResolvedValue({} as any)

    render(<AskPage />)

    await waitFor(() => {
      expect(screen.getByText('Now answering question about Syllabus')).toBeInTheDocument()
    })

    const input = screen.getByPlaceholderText('Enter your question...')
    fireEvent.change(input, { target: { value: 'What is this?' } })
    fireEvent.click(screen.getByText('Send'))

    expect(screen.getByText('What is this?')).toBeInTheDocument()
    
    await waitFor(() => {
      expect(screen.getByText('This is the answer')).toBeInTheDocument()
      expect(apiClient.askQuestion).toHaveBeenCalledWith('What is this?', 'doc1')
      expect(apiClient.addHistory).toHaveBeenCalled()
    })
  })

  it('handles error during question submission', async () => {
    localStorageMock.setItem('selectedDocId', 'doc1')
    vi.mocked(apiClient.getUserUploads).mockResolvedValue([
      { id: 'doc1', filename: 'syllabus.pdf', originalFilename: 'Syllabus', uploadedAt: 'now' }
    ])
    vi.mocked(apiClient.askQuestion).mockRejectedValue(new Error('Network error'))

    render(<AskPage />)

    await waitFor(() => {
      expect(screen.getByPlaceholderText('Enter your question...')).not.toBeDisabled()
    })

    const input = screen.getByPlaceholderText('Enter your question...')
    fireEvent.change(input, { target: { value: 'What is this?' } })
    fireEvent.click(screen.getByText('Send'))

    await waitFor(() => {
      expect(screen.getByText('Network error')).toBeInTheDocument()
    })
  })

  it('displays citation when clicked', async () => {
    const historyItems = [
      { id: '1', question: 'Q1', answer: 'A1', citations: ['Citation text'], timestamp: 'now' }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(historyItems)

    render(<AskPage />)

    await waitFor(() => {
      expect(screen.getByText('Source 1')).toBeInTheDocument()
    })

    fireEvent.click(screen.getByText('Source 1'))

    expect(screen.getByText('Citation Source')).toBeInTheDocument()
    expect(screen.getByText(/Citation text/)).toBeInTheDocument()
  })
})
