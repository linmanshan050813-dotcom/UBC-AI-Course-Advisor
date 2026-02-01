/**
 * Application Entry Point (DUT)
 * Initializes React application and renders root component.
 * Uses React 18 createRoot API for rendering.
 * Used throughout all phases of the AI Course Advisor project.
 */

import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { AuthProvider } from './context/AuthContext'

// Render application to root DOM element with StrictMode for development checks
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>,
)
