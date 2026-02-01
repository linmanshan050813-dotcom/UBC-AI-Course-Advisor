/**
 * Test Page (DUT)
 * Dev-only page for verifying backend API connectivity.
 * Tests mock endpoints for all major features.
 * Used in Phase 0-1 of the AI Course Advisor project.
 */

import React, { useState } from 'react'
import { mockUpload, mockQuery, mockHistory } from '../../lib/apiClient'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'
import '../../styles/page.css'
import './test.css'

/**
 * Render API test page.
 * Provides buttons to trigger mock API calls and displays raw JSON results.
 * 
 * @returns JSX page content
 */
function TestPage() {
  const [uploadResult, setUploadResult] = useState<any>(null)
  const [queryResult, setQueryResult] = useState<any>(null)
  const [historyResult, setHistoryResult] = useState<any>(null)

  const testUpload = async () => {
    try {
      setUploadResult(null)
      const result = await mockUpload()
      setUploadResult(result)
    } catch (err) {
      setUploadResult({ error: err instanceof Error ? err.message : 'Unknown error' })
    }
  }

  const testQuery = async () => {
    try {
      setQueryResult(null)
      const result = await mockQuery()
      setQueryResult(result)
    } catch (err) {
      setQueryResult({ error: err instanceof Error ? err.message : 'Unknown error' })
    }
  }

  const testHistory = async () => {
    try {
      setHistoryResult(null)
      const result = await mockHistory()
      setHistoryResult(result)
    } catch (err) {
      setHistoryResult({ error: err instanceof Error ? err.message : 'Unknown error' })
    }
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>API Test Page</h1>
        <p className="page-subtitle">Phase 1 - Testing mock endpoints</p>
      </div>

      <div className="test-grid">
        <Card className="test-card">
          <h3>Upload Mock</h3>
          <p className="text-secondary">Test /api/upload/mock endpoint</p>
          <Button onClick={testUpload} className="test-button">
            Test Upload
          </Button>
          {uploadResult && (
            <pre className="code-block">
              {JSON.stringify(uploadResult, null, 2)}
            </pre>
          )}
        </Card>

        <Card className="test-card">
          <h3>Query Mock</h3>
          <p className="text-secondary">Test /api/query/mock endpoint</p>
          <Button onClick={testQuery} className="test-button">
            Test Query
          </Button>
          {queryResult && (
            <pre className="code-block">
              {JSON.stringify(queryResult, null, 2)}
            </pre>
          )}
        </Card>

        <Card className="test-card">
          <h3>History Mock</h3>
          <p className="text-secondary">Test /api/history/mock endpoint</p>
          <Button onClick={testHistory} className="test-button">
            Test History
          </Button>
          {historyResult && (
            <pre className="code-block">
              {JSON.stringify(historyResult, null, 2)}
            </pre>
          )}
        </Card>
      </div>
    </div>
  )
}

export default TestPage
