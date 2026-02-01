/**
 * Button Component (DUT)
 * Reusable button component with primary and secondary variants.
 * Extends standard HTML button attributes.
 * Used throughout all phases of the AI Course Advisor project.
 */

import React from 'react'
import './Button.css'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary'
  children: React.ReactNode
}

/**
 * Render button with variant styling.
 * 
 * @param variant button style variant (primary or secondary)
 * @param children button content
 * @param className additional CSS classes
 * @param props all other standard button HTML attributes
 * @returns JSX button element
 */
export function Button({ variant = 'primary', children, className = '', ...props }: ButtonProps) {
  return (
    <button
      className={`btn btn-${variant} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

