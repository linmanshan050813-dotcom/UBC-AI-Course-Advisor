/**
 * Supabase Client (DUT)
 * Initializes the Supabase client for authentication and database interactions.
 * Reads configuration from environment variables.
 */

import { createClient } from '@supabase/supabase-js'

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL || 'YOUR_SUPABASE_URL_PLACEHOLDER'
const supabaseKey = import.meta.env.VITE_SUPABASE_ANON_KEY || 'YOUR_SUPABASE_ANON_KEY_PLACEHOLDER'

export const supabase = createClient(supabaseUrl, supabaseKey)
