/**
 * Card Component (DUT)
 * Reusable card container component for content sections.
 * Provides consistent styling and layout.
 * Used throughout all phases of the AI Course Advisor project.
 */

import React from 'react'
import './Card.css'

interface CardProps {
  children: React.ReactNode
  className?: string
}

/**
 * Render card container with optional custom classes.
 * 
 * @param children card content
 * @param className additional CSS classes
 * @returns JSX card div element
 */
export function Card({ children, className = '' }: CardProps) {
  return (
    <div className={`card ${className}`}>
      {children}
    </div>
  )
}

