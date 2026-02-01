/**
 * App Component (DUT)
 * Main application component with routing configuration.
 * Sets up React Router and renders navigation and page components.
 * Used throughout all phases of the AI Course Advisor project.
 */

import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Navbar } from './components/Navbar'
import HomePage from './app/page'
import AdvisorPage from './app/advisor/page'
import HistoryPage from './app/history/page'
import TestPage from './app/test/page'
import LoginPage from './app/login/page'
import UploadPage from './app/upload/page'
import AskPage from './app/ask/page'
import { useAuth } from './context/AuthContext'
import './App.css'

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth()
  
  if (loading) return <div>Loading...</div>
  
  return user ? children : <Navigate to="/login" />
}

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/advisor" element={<AdvisorPage />} />
            <Route path="/upload" element={
              <PrivateRoute>
                <UploadPage />
              </PrivateRoute>
            } />
            <Route path="/ask" element={
              <PrivateRoute>
                <AskPage />
              </PrivateRoute>
            } />
            <Route path="/history" element={
              <PrivateRoute>
                <HistoryPage />
              </PrivateRoute>
            } />
            <Route path="/test" element={<TestPage />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App
