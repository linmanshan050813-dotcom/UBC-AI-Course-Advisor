/**
 * Navigation Bar Component (DUT)
 * Provides application-wide navigation links.
 * Shows different links based on authentication state.
 * Used in Phase 1+ of the AI Course Advisor project.
 */

import React from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

/**
 * Render the top navigation bar.
 * Contains logo, navigation links, and auth controls.
 * 
 * @returns JSX navbar element
 */
export function Navbar() {
  const location = useLocation()
  const { user, signOut } = useAuth()
  const navigate = useNavigate()

  const handleSignOut = async () => {
    await signOut()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          AI Course Advisor
        </Link>
        <div className="navbar-links">
          <Link
            to="/"
            className={`navbar-link ${location.pathname === '/' ? 'active' : ''}`}
          >
            Home
          </Link>
          {user && (
            <>
              <Link
                to="/upload"
                className={`navbar-link ${location.pathname === '/upload' ? 'active' : ''}`}
              >
                Upload
              </Link>
              <Link
                to="/ask"
                className={`navbar-link ${location.pathname === '/ask' ? 'active' : ''}`}
              >
                Ask
              </Link>
              <Link
                to="/history"
                className={`navbar-link ${location.pathname === '/history' ? 'active' : ''}`}
              >
                History
              </Link>
            </>
          )}
          {user ? (
            <button onClick={handleSignOut} className="navbar-link" style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
              Sign Out
            </button>
          ) : (
            <Link
              to="/login"
              className={`navbar-link ${location.pathname === '/login' ? 'active' : ''}`}
            >
              Sign In
            </Link>
          )}
        </div>
      </div>
    </nav>
  )
}
