/**
 * Home Page (DUT)
 * Landing page for the AI Course Advisor application.
 * Provides overview of features and navigation to key sections.
 * Used in Phase 1+ of the AI Course Advisor project.
 */

import React from 'react'
import { Link } from 'react-router-dom'
import { Button } from '../components/Button'
import { Card } from '../components/Card'
import { useAuth } from '../context/AuthContext'
import '../styles/page.css'
import './home.css'

/**
 * Render Home page.
 * Displays welcome message, feature cards, and calls to action.
 * 
 * @returns JSX page content
 */
export default function HomePage() {
  const { user } = useAuth()

  return (
    <div className="page-container home-page">
      <div className="page-header">
        <h1>AI Course Advisor</h1>
        <p className="page-subtitle">An intelligent assistant that extracts course information from syllabi and answers course-related questions.</p>
      </div>

      <div className="home-content">
        <Card className="intro-card">
          <h2>What We Do</h2>
          <p>
            AI Course Advisor helps students quickly find answers to their course-related questions by analyzing 
            uploaded course documents. Simply upload your syllabus, and ask questions in natural language. 
            Our AI will extract precise answers and provide citations to the original source material.
          </p>
        </Card>

        <Card className="features-card">
          <h2>Features</h2>
          <div className="features-list">
            <div className="feature-item">
              <h3>📄 Supported File Types</h3>
              <p>Upload PDF, DOCX, or TXT files containing your course syllabus and materials.</p>
            </div>
            <div className="feature-item">
              <h3>🤖 AI-Powered Answers</h3>
              <p>Ask questions in natural language and receive accurate answers extracted from your course documents.</p>
            </div>
            <div className="feature-item">
              <h3>📚 Citation System</h3>
              <p>Every answer includes citations showing exactly where the information was found in your documents.</p>
            </div>
            <div className="feature-item">
              <h3>📝 Question History</h3>
              <p>View your previous questions and answers to keep track of important information.</p>
            </div>
          </div>
        </Card>

        <Card className="how-it-works-card">
          <h2>How It Works</h2>
          <div className="steps-list">
            <div className="step-item">
              <div className="step-number">1</div>
              <div className="step-content">
                <h3>Upload Your Syllabus</h3>
                <p>Upload a PDF, DOCX, or TXT file containing your course syllabus or course materials.</p>
              </div>
            </div>
            <div className="step-item">
              <div className="step-number">2</div>
              <div className="step-content">
                <h3>Ask Questions</h3>
                <p>Ask any question about your course in natural language. For example: "What is the late submission policy?" or "When is the final exam?"</p>
              </div>
            </div>
            <div className="step-item">
              <div className="step-number">3</div>
              <div className="step-content">
                <h3>Get Answers with Citations</h3>
                <p>Receive precise answers extracted from your documents, with citations showing the exact source of the information.</p>
              </div>
            </div>
          </div>
        </Card>

        <div className="cta-section">
          {user ? (
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
              <Link to="/upload">
                <Button className="cta-button">
                  Upload Syllabus
                </Button>
              </Link>
              <Link to="/ask">
                <Button className="cta-button" variant="secondary">
                  Ask Questions
                </Button>
              </Link>
            </div>
          ) : (
            <Link to="/upload">
              <Button className="cta-button">
                Get Started →
              </Button>
            </Link>
          )}
        </div>
      </div>
    </div>
  )
}
