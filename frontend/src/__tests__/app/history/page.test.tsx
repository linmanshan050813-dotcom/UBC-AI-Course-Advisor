import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import HistoryPage from '../../../app/history/page'
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import * as apiClient from '../../../lib/apiClient'

// Mock apiClient
vi.mock('../../../lib/apiClient', () => ({
  fetchHistory: vi.fn(),
  deleteHistoryEntry: vi.fn(),
  clearHistory: vi.fn()
}))

// Mock window.confirm
window.confirm = vi.fn(() => true)

describe('HistoryPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders correctly loading state', () => {
    vi.mocked(apiClient.fetchHistory).mockReturnValue(new Promise(() => {})) // Never resolves
    render(<HistoryPage />)
    expect(screen.getByText('History')).toBeInTheDocument()
    // Check loading state if implemented visible, but here it's hard to check DOM for loading dots without test id
  })

  it('renders empty history', async () => {
    vi.mocked(apiClient.fetchHistory).mockResolvedValue([])
    render(<HistoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText('Start by asking your first question!')).toBeInTheDocument()
    })
  })

  it('renders history items', async () => {
    const mockHistory = [
      { id: '1', question: 'Q1', answer: 'A1', timestamp: '2023-01-01', citations: ['C1'] }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(mockHistory)
    render(<HistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Q1')).toBeInTheDocument()
      expect(screen.getByText('A1')).toBeInTheDocument()
      expect(screen.getByText('Source 1')).toBeInTheDocument()
    })
  })

  it('deletes history entry', async () => {
    const mockHistory = [
      { id: '1', question: 'Q1', answer: 'A1', timestamp: '2023-01-01' }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(mockHistory)
    render(<HistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Q1')).toBeInTheDocument()
    })

    const deleteButton = screen.getByTitle('Delete')
    fireEvent.click(deleteButton)

    expect(window.confirm).toHaveBeenCalled()
    await waitFor(() => {
      expect(apiClient.deleteHistoryEntry).toHaveBeenCalledWith('1')
      // Should reload history
      expect(apiClient.fetchHistory).toHaveBeenCalledTimes(2) 
    })
  })

  it('clears all history', async () => {
    const mockHistory = [
      { id: '1', question: 'Q1', answer: 'A1', timestamp: '2023-01-01' }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(mockHistory)
    render(<HistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Clear History')).toBeInTheDocument()
    })

    fireEvent.click(screen.getByText('Clear History'))

    expect(window.confirm).toHaveBeenCalled()
    await waitFor(() => {
      expect(apiClient.clearHistory).toHaveBeenCalled()
      expect(apiClient.fetchHistory).toHaveBeenCalledTimes(2)
    })
  })

  it('handles error loading history', async () => {
    vi.mocked(apiClient.fetchHistory).mockRejectedValue(new Error('Network error'))
    render(<HistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Network error')).toBeInTheDocument()
      expect(screen.getByText('Retry')).toBeInTheDocument()
    })

    // Retry
    vi.mocked(apiClient.fetchHistory).mockResolvedValue([])
    fireEvent.click(screen.getByText('Retry'))
    
    await waitFor(() => {
      expect(screen.queryByText('Network error')).not.toBeInTheDocument()
    })
  })

  it('shows citation on click', async () => {
    const mockHistory = [
      { id: '1', question: 'Q1', answer: 'A1', timestamp: '2023-01-01', citations: ['Citation text'] }
    ]
    vi.mocked(apiClient.fetchHistory).mockResolvedValue(mockHistory)
    render(<HistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Source 1')).toBeInTheDocument()
    })

    fireEvent.click(screen.getByText('Source 1'))
    
    expect(screen.getByText('Citation Source')).toBeInTheDocument()
    expect(screen.getByText('Citation text')).toBeInTheDocument()
  })
})
