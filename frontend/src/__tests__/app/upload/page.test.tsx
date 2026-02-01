import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import UploadPage from '../../../app/upload/page'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as apiClient from '../../../lib/apiClient'

// Mock apiClient
vi.mock('../../../lib/apiClient', () => ({
  uploadFile: vi.fn(),
  getUserUploads: vi.fn(),
  deleteUpload: vi.fn(),
  clearUploads: vi.fn()
}))

// Mock auth context
const mockSession = { user: { id: '1' } }
vi.mock('../../../context/AuthContext', () => ({
  useAuth: () => ({ session: mockSession })
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

// Mock confirm
window.confirm = vi.fn(() => true)

describe('UploadPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorageMock.clear()
    // Default mock implementation
    vi.mocked(apiClient.getUserUploads).mockResolvedValue([])
  })

  it('renders upload page correctly', () => {
    render(<UploadPage />)
    expect(screen.getByText('Upload Course Syllabus')).toBeInTheDocument()
    expect(screen.getByText('Upload File')).toBeInTheDocument()
  })

  it('loads history on mount', async () => {
    const mockUploads = [
      { id: '1', filename: 'test.pdf', originalFilename: 'test.pdf', uploadedAt: '2023-01-01' }
    ]
    vi.mocked(apiClient.getUserUploads).mockResolvedValue(mockUploads)

    render(<UploadPage />)

    await waitFor(() => {
      expect(screen.getByText('test.pdf')).toBeInTheDocument()
    })
  })

  it('handles file selection', () => {
    render(<UploadPage />)
    const file = new File(['content'], 'syllabus.pdf', { type: 'application/pdf' })
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    
    fireEvent.change(input, { target: { files: [file] } })
    
    expect(screen.getByText(/Selected:/)).toBeInTheDocument()
    // Using getAllByText since filename appears twice (input display and selection info)
    expect(screen.getAllByText('syllabus.pdf').length).toBeGreaterThan(0)
  })

  it('handles file upload success', async () => {
    render(<UploadPage />)
    const file = new File(['content'], 'syllabus.pdf', { type: 'application/pdf' })
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    
    vi.mocked(apiClient.uploadFile).mockResolvedValue({ 
      success: true, filename: 'syllabus.pdf', docId: '1', originalFilename: 'syllabus.pdf' 
    })

    fireEvent.change(input, { target: { files: [file] } })
    fireEvent.click(screen.getByText('Upload File'))

    expect(screen.getByText('Uploading...')).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByText('Upload Successful')).toBeInTheDocument()
      expect(apiClient.uploadFile).toHaveBeenCalledWith(file)
      expect(apiClient.getUserUploads).toHaveBeenCalled() // History refreshed
    })
  })

  it('handles upload failure', async () => {
    render(<UploadPage />)
    const file = new File(['content'], 'syllabus.pdf', { type: 'application/pdf' })
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    
    vi.mocked(apiClient.uploadFile).mockRejectedValue(new Error('Upload failed'))

    fireEvent.change(input, { target: { files: [file] } })
    fireEvent.click(screen.getByText('Upload File'))

    await waitFor(() => {
      expect(screen.getByText('Upload Failed')).toBeInTheDocument()
      expect(screen.getByText('Upload failed')).toBeInTheDocument()
    })
  })

  it('handles reset', () => {
    render(<UploadPage />)
    const file = new File(['content'], 'syllabus.pdf', { type: 'application/pdf' })
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    
    fireEvent.change(input, { target: { files: [file] } })
    expect(screen.getAllByText('syllabus.pdf').length).toBeGreaterThan(0)

    fireEvent.click(screen.getByText('Reset'))
    // It might still be in the DOM if "No file selected" logic isn't perfect in test environment
    // But check if the "Selected:" text is gone
    expect(screen.queryByText(/Selected:/)).not.toBeInTheDocument()
  })

  it('selects and deselects past upload', async () => {
    const mockUploads = [
      { id: '1', filename: 'test.pdf', originalFilename: 'test.pdf', uploadedAt: '2023-01-01' }
    ]
    vi.mocked(apiClient.getUserUploads).mockResolvedValue(mockUploads)

    render(<UploadPage />)

    await waitFor(() => {
      expect(screen.getByText('test.pdf')).toBeInTheDocument()
    })

    const selectButton = screen.getByText('Select')
    fireEvent.click(selectButton)
    expect(localStorageMock.getItem('selectedDocId')).toBe('1')
    expect(screen.getByText('Selected')).toBeInTheDocument()

    fireEvent.click(screen.getByText('Selected'))
    expect(localStorageMock.getItem('selectedDocId')).toBe(null)
    expect(screen.getByText('Select')).toBeInTheDocument()
  })

  it('deletes an upload', async () => {
    const mockUploads = [
      { id: '1', filename: 'test.pdf', originalFilename: 'test.pdf', uploadedAt: '2023-01-01' }
    ]
    vi.mocked(apiClient.getUserUploads).mockResolvedValue(mockUploads)

    render(<UploadPage />)

    await waitFor(() => {
      expect(screen.getByText('test.pdf')).toBeInTheDocument()
    })

    const deleteButton = screen.getByTitle('Delete')
    fireEvent.click(deleteButton)

    expect(window.confirm).toHaveBeenCalled()
    await waitFor(() => {
        expect(apiClient.deleteUpload).toHaveBeenCalledWith('1')
        expect(apiClient.getUserUploads).toHaveBeenCalledTimes(2)
    })
  })

  it('clears all uploads', async () => {
    const mockUploads = [
      { id: '1', filename: 'test.pdf', originalFilename: 'test.pdf', uploadedAt: '2023-01-01' }
    ]
    vi.mocked(apiClient.getUserUploads).mockResolvedValue(mockUploads)

    render(<UploadPage />)

    await waitFor(() => {
      expect(screen.getByText('Delete All')).toBeInTheDocument()
    })

    fireEvent.click(screen.getByText('Delete All'))

    expect(window.confirm).toHaveBeenCalled()
    await waitFor(() => {
        expect(apiClient.clearUploads).toHaveBeenCalled()
        expect(apiClient.getUserUploads).toHaveBeenCalledTimes(2)
    })
  })
})
