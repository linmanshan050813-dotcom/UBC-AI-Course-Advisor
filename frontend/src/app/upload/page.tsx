/**
 * Upload Page (DUT)
 * Allows users to upload course syllabus files (PDF/TXT/DOCX).
 * Displays upload history and allows file management.
 * Used in Phase 1+ of the AI Course Advisor project.
 */

import React, { useState, useEffect } from 'react'
import { uploadFile, getUserUploads, deleteUpload, clearUploads, type UploadResponse, type UploadedDoc } from '../../lib/apiClient'
import { Card } from '../../components/Card'
import { Button } from '../../components/Button'
import { Input } from '../../components/Input'
import { useAuth } from '../../context/AuthContext'
import '../../styles/page.css'
import './upload.css'

/**
 * Render the Upload page.
 * Includes file selection input, upload button, and past uploads list.
 * 
 * @returns JSX page content
 */
export default function UploadPage() {
  const { session } = useAuth()
  const [file, setFile] = useState<File | null>(null)
  const [uploading, setUploading] = useState(false)
  const [result, setResult] = useState<UploadResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [pastUploads, setPastUploads] = useState<UploadedDoc[]>([])
  const [loadingHistory, setLoadingHistory] = useState(false)

  useEffect(() => {
    if (session) {
      loadHistory()
    }
  }, [session])

  const loadHistory = async () => {
    setLoadingHistory(true)
    try {
      const uploads = await getUserUploads()
      setPastUploads(uploads)
    } catch (err) {
      console.error('Failed to load uploads:', err)
    } finally {
      setLoadingHistory(false)
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0]
    if (selectedFile) {
      setFile(selectedFile)
      setResult(null)
      setError(null)
    }
  }

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a file')
      return
    }

    setUploading(true)
    setError(null)
    setResult(null)

    try {
      const response = await uploadFile(file)
      setResult(response)
      // Refresh history
      loadHistory()
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Upload failed'
      setError(errorMessage)
      console.error('Upload error:', err)
    } finally {
      setUploading(false)
    }
  }

  const handleReset = () => {
    setFile(null)
    setResult(null)
    setError(null)
    // Reset file input
    const fileInput = document.getElementById('file-input') as HTMLInputElement
    if (fileInput) {
      fileInput.value = ''
    }
  }

  const [selectedDocId, setSelectedDocId] = useState<string | null>(localStorage.getItem('selectedDocId'))

  const handleSelectPast = (doc: UploadedDoc) => {
    if (selectedDocId === doc.id) {
      // Deselect
      localStorage.removeItem('selectedDocId')
      setSelectedDocId(null)
    } else {
      // Select
      localStorage.setItem('selectedDocId', doc.id)
      setSelectedDocId(doc.id)
    }
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Upload Course Syllabus</h1>
        <p className="page-subtitle">Upload a PDF, DOCX, or TXT file to get started</p>
      </div>

      <Card className="upload-card">
        <div className="upload-section">
          <Input
            id="file-input"
            type="file"
            accept=".pdf,.txt,.docx"
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
          {(file || result) && (
            <Button
              variant="secondary"
              onClick={handleReset}
              disabled={uploading}
            >
              Reset
            </Button>
          )}
        </div>

        {error && (
          <div className="upload-result error">
            <h3>Upload Failed</h3>
            <div className="result-content">
              <div className="result-item">
                <span className="result-value" style={{ color: 'var(--error)' }}>
                  {error.includes("syllabus") ? "Not a syllabus!" : error}
                </span>
              </div>
            </div>
          </div>
        )}

        {result && (
          <div className="upload-result">
            <h3>Upload Successful</h3>
            <div className="result-content">
              <div className="result-item">
                <span className="result-label">Filename:</span>
                <span className="result-value">{result.filename}</span>
              </div>
            </div>
          </div>
        )}
      </Card>

      {/* Past Uploads Section */}
      {session && (
        <div style={{ marginTop: '2rem' }}>
          <Card className="history-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <h3>Past Uploads</h3>
                {pastUploads.length > 0 && (
                    <Button variant="secondary" onClick={async () => {
                        if (window.confirm('Are you sure you want to delete all uploads?')) {
                            await clearUploads();
                            loadHistory();
                        }
                    }}>
                        Delete All
                    </Button>
                )}
            </div>
            {loadingHistory ? (
              <p>Loading...</p>
            ) : pastUploads.length === 0 ? (
              <p className="text-secondary">No past uploads found.</p>
            ) : (
              <div className="past-uploads-list">
                {pastUploads.map((doc) => (
                  <div key={doc.id} className="upload-item" style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'center',
                    padding: '1rem',
                    borderBottom: '1px solid var(--border)',
                    position: 'relative'
                  }}>
                    <button 
                        onClick={async (e) => {
                            e.stopPropagation();
                            if (window.confirm('Delete this file?')) {
                                await deleteUpload(doc.id);
                                loadHistory();
                                if (selectedDocId === doc.id) {
                                    localStorage.removeItem('selectedDocId');
                                    setSelectedDocId(null);
                                }
                            }
                        }}
                        style={{
                            position: 'absolute',
                            top: '5px',
                            right: '5px',
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            fontSize: '1rem',
                            color: 'var(--text-secondary)',
                            zIndex: 10
                        }}
                        title="Delete"
                    >
                        ×
                    </button>
                    <div>
                      <p style={{ fontWeight: 600, paddingRight: '20px' }}>{doc.originalFilename}</p>
                      <p className="text-secondary" style={{ fontSize: '0.8rem' }}>{doc.uploadedAt}</p>
                    </div>
                    <Button 
                      variant={selectedDocId === doc.id ? 'primary' : 'secondary'} 
                      onClick={() => handleSelectPast(doc)}
                    >
                      {selectedDocId === doc.id ? 'Selected' : 'Select'}
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>
      )}
    </div>
  )
}
