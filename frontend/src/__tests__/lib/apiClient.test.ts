import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import * as apiClient from '../../lib/apiClient'
import { supabase } from '../../lib/supabaseClient'

// Mock supabase
vi.mock('../../lib/supabaseClient', () => ({
  supabase: {
    auth: {
      getSession: vi.fn()
    }
  }
}))

// Mock fetch
global.fetch = vi.fn()

describe('apiClient', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    vi.mocked(supabase.auth.getSession).mockResolvedValue({
      data: { session: { access_token: 'test-token' } as any },
      error: null
    })
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('checkHealth calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => ({ status: 'ok', phase: 1 })
    } as Response)

    const response = await apiClient.checkHealth()
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/health'))
    expect(response).toEqual({ status: 'ok', phase: 1 })
  })

  it('uploadFile calls correct endpoint with auth header', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => ({ success: true, filename: 'test.pdf' })
    } as Response)

    const file = new File(['content'], 'test.pdf', { type: 'application/pdf' })
    await apiClient.uploadFile(file)

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/upload'),
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          'Authorization': 'Bearer test-token'
        })
      })
    )
  })

  it('getUserUploads calls correct endpoint', async () => {
    const mockUploads = [{ id: '1', filename: 'test.pdf' }]
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => mockUploads
    } as Response)

    const result = await apiClient.getUserUploads()
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/uploads'),
      expect.objectContaining({ method: 'GET' })
    )
    expect(result).toEqual(mockUploads)
  })

  it('askQuestion calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => ({ answer: '42' })
    } as Response)

    await apiClient.askQuestion('What is life?', 'doc1')

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/ask'),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ question: 'What is life?', docId: 'doc1' })
      })
    )
  })

  it('fetchHistory calls correct endpoint', async () => {
    const mockHistory = [{ id: '1', question: 'Q' }]
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => mockHistory
    } as Response)

    const result = await apiClient.fetchHistory()
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/history'),
      expect.objectContaining({ method: 'GET' })
    )
    expect(result).toEqual(mockHistory)
  })

  it('addHistory calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
      json: async () => ({ id: '1' })
    } as Response)

    await apiClient.addHistory('Q', 'A', [{ id: '1', text: 'citation' }])

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/history'),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          question: 'Q',
          answer: 'A',
          citations: ['citation']
        })
      })
    )
  })

  it('deleteHistoryEntry calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({ ok: true } as Response)

    await apiClient.deleteHistoryEntry('1')

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/history/1'),
      expect.objectContaining({ method: 'DELETE' })
    )
  })

  it('clearHistory calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({ ok: true } as Response)

    await apiClient.clearHistory()

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/history'),
      expect.objectContaining({ method: 'DELETE' })
    )
  })

  it('deleteUpload calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({ ok: true } as Response)

    await apiClient.deleteUpload('1')

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/upload/1'),
      expect.objectContaining({ method: 'DELETE' })
    )
  })

  it('clearUploads calls correct endpoint', async () => {
    vi.mocked(fetch).mockResolvedValue({ ok: true } as Response)

    await apiClient.clearUploads()

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/uploads'),
      expect.objectContaining({ method: 'DELETE' })
    )
  })

  it('handles auth token missing', async () => {
    vi.mocked(supabase.auth.getSession).mockResolvedValue({
      data: { session: null },
      error: null
    })

    vi.mocked(fetch).mockResolvedValue({ ok: true, json: async () => [] } as Response)

    await apiClient.getUserUploads()

    expect(fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.not.objectContaining({
        headers: expect.objectContaining({ 'Authorization': expect.any(String) })
      })
    )
  })

  it('mock functions calls', async () => {
    vi.mocked(fetch).mockResolvedValue({ ok: true, json: async () => ({}) } as Response)
    await apiClient.mockUpload()
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/api/upload/mock'))
    
    await apiClient.mockQuery()
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/api/query/mock'))
    
    await apiClient.mockHistory()
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('/api/history/mock'))
  })
})
