/**
 * API Client (DUT)
 * Centralized client for backend API communication.
 * Handles all HTTP requests to backend endpoints.
 * Used throughout all phases of the AI Course Advisor project.
 */

import { supabase } from './supabaseClient'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

/**
 * Helper to get auth header with JWT token from Supabase session.
 * @returns Headers object with Authorization header if logged in
 */
async function getAuthHeader(): Promise<HeadersInit> {
  const { data: { session } } = await supabase.auth.getSession()
  const token = session?.access_token
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

export interface HealthResponse {
  status: string;
  phase: number;
}

export interface MockUploadResponse {
  ok: boolean;
}

export interface MockQueryResponse {
  answer: string;
}

export interface MockHistoryResponse {
  entries: any[];
}

export interface UploadResponse {
  success: boolean;
  filename: string;
  docId: string;
  originalFilename: string;
}

export interface UploadedDoc {
  id: string;
  filename: string;
  originalFilename: string;
  uploadedAt: string;
}

export interface Citation {
  id: string;
  content?: string;  // API returns "content"
  text?: string;     // Fallback for internal use
}

export interface QueryResponse {
  answer: string;
  citations: Citation[];
}

export interface CitationDetail {
  id: string;
  text: string;
}

export interface HistoryItem {
  id: string;
  question: string;
  answer: string;
  citations?: string[];
  timestamp: string;
}

export interface HistoryRequest {
  question: string;
  answer: string;
  citations?: string[];
}

/**
 * Check backend health status.
 * @returns HealthResponse with status and phase
 */
export async function checkHealth(): Promise<HealthResponse> {
  const url = `${API_BASE_URL}/health`;
  const response = await fetch(url);
  if (!response.ok) throw new Error('Health check failed');
  return response.json();
}

/**
 * Perform mock upload (for testing).
 * @returns MockUploadResponse
 */
export async function mockUpload(): Promise<MockUploadResponse> {
  const response = await fetch(`${API_BASE_URL}/api/upload/mock`);
  if (!response.ok) throw new Error('Upload mock failed');
  return response.json();
}

/**
 * Perform mock query (for testing).
 * @returns MockQueryResponse
 */
export async function mockQuery(): Promise<MockQueryResponse> {
  const response = await fetch(`${API_BASE_URL}/api/query/mock`);
  if (!response.ok) throw new Error('Query mock failed');
  return response.json();
}

/**
 * Perform mock history fetch (for testing).
 * @returns MockHistoryResponse
 */
export async function mockHistory(): Promise<MockHistoryResponse> {
  const response = await fetch(`${API_BASE_URL}/api/history/mock`);
  if (!response.ok) throw new Error('History mock failed');
  return response.json();
}

/**
 * Upload a course document file.
 * @param file file object to upload
 * @returns UploadResponse with document ID
 */
export async function uploadFile(file: File): Promise<UploadResponse> {
  const formData = new FormData();
  formData.append('file', file);
  const authHeader = await getAuthHeader();

  const response = await fetch(`${API_BASE_URL}/api/upload`, {
    method: 'POST',
    headers: { ...authHeader },
    body: formData,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Upload failed: ${errorText}`);
  }
  return response.json();
}

/**
 * Get list of uploaded documents for the current user.
 * @returns list of UploadedDoc objects
 */
export async function getUserUploads(): Promise<UploadedDoc[]> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/uploads`, {
    method: 'GET',
    headers: { ...authHeader, 'Accept': 'application/json' },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch uploads');
  }
  return response.json();
}

/**
 * Send a question to the AI advisor.
 * @param question user question
 * @param docId optional document ID to query against
 * @returns QueryResponse with answer and citations
 */
export async function askQuestion(question: string, docId?: string | null): Promise<QueryResponse> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/ask`, {
    method: 'POST',
    headers: {
      ...authHeader,
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify({ question, docId }),
  });

  if (!response.ok) {
    throw new Error('Query failed');
  }
  return response.json();
}

/**
 * Get citation details (deprecated/stub).
 * @param id citation ID
 * @returns citation detail
 */
export async function getCitation(id: string): Promise<CitationDetail> {
  // Since we removed the backend endpoint, we mock it or throw error.
  // Frontend should use the text in the citation object directly.
  throw new Error("Citation details not available from server");
}

/**
 * Fetch Q/A history for the current user.
 * @returns list of HistoryItem objects
 */
export async function fetchHistory(): Promise<HistoryItem[]> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/history`, {
    method: 'GET',
    headers: {
      ...authHeader,
      'Accept': 'application/json',
    },
  });

  if (!response.ok) throw new Error('History fetch failed');
  return response.json();
}

/**
 * Add a new entry to the user's history.
 * @param question question asked
 * @param answer answer received
 * @param citations optional citations
 * @returns the created HistoryItem
 */
export async function addHistory(question: string, answer: string, citations?: Citation[]): Promise<HistoryItem> {
  try {
    const authHeader = await getAuthHeader();
    
    // Extract text strings from Citation objects
    let citationStrings: string[] | undefined = undefined;
    if (citations && citations.length > 0) {
      citationStrings = citations.map(c => c.text || c.content || '').filter(s => s.length > 0);
    }

    const url = `${API_BASE_URL}/api/history`;
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        ...authHeader,
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify({ 
        question, 
        answer,
        citations: citationStrings
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('History save failed:', response.status, errorText);
      throw new Error(`History add failed: ${errorText}`);
    }
    
    const result = await response.json();
    console.log('History saved successfully:', result.id);
    return result;
  } catch (error) {
    console.error('Exception saving history:', error);
    if (error instanceof Error) throw error;
    throw new Error(`History add failed: ${String(error)}`);
  }
}

/**
 * Delete a specific history entry.
 * @param id history entry ID
 */
export async function deleteHistoryEntry(id: string): Promise<void> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/history/${id}`, {
    method: 'DELETE',
    headers: { ...authHeader },
  });

  if (!response.ok) {
    throw new Error('Failed to delete history entry');
  }
}

/**
 * Clear all history entries for the user.
 */
export async function clearHistory(): Promise<void> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/history`, {
    method: 'DELETE',
    headers: { ...authHeader },
  });

  if (!response.ok) {
    throw new Error('Failed to clear history');
  }
}

/**
 * Delete a specific uploaded document.
 * @param id document ID
 */
export async function deleteUpload(id: string): Promise<void> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/upload/${id}`, {
    method: 'DELETE',
    headers: { ...authHeader },
  });

  if (!response.ok) {
    throw new Error('Failed to delete upload');
  }
}

/**
 * Clear all uploaded documents for the user.
 */
export async function clearUploads(): Promise<void> {
  const authHeader = await getAuthHeader();
  const response = await fetch(`${API_BASE_URL}/api/uploads`, {
    method: 'DELETE',
    headers: { ...authHeader },
  });

  if (!response.ok) {
    throw new Error('Failed to clear uploads');
  }
}
