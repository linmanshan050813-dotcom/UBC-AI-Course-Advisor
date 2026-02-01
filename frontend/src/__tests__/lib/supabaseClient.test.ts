import { describe, it, expect, vi } from 'vitest'

// Mock createClient
const mockCreateClient = vi.fn()
vi.mock('@supabase/supabase-js', () => ({
  createClient: (...args) => mockCreateClient(...args)
}))

describe('supabaseClient', () => {
  it('creates supabase client with env vars', async () => {
    // Re-import to trigger execution
    vi.resetModules()
    // Mock env vars
    vi.stubEnv('VITE_SUPABASE_URL', 'https://test.supabase.co')
    vi.stubEnv('VITE_SUPABASE_ANON_KEY', 'test-key')
    
    await import('../../lib/supabaseClient')
    
    expect(mockCreateClient).toHaveBeenCalledWith(
      'https://test.supabase.co',
      'test-key'
    )
  })
})
