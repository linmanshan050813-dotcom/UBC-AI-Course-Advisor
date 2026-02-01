/**
 * Input Component (DUT)
 * Reusable input field component with label and error display.
 * Extends standard HTML input attributes.
 * Used throughout all phases of the AI Course Advisor project.
 */

import React, { useRef, useState } from 'react'
import './Input.css'

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
}

/**
 * Render input field with optional label and error message.
 * For file inputs, provides a custom button with English text.
 * 
 * @param label optional label text for input
 * @param error optional error message to display
 * @param className additional CSS classes
 * @param id optional input ID (auto-generated from label if not provided)
 * @param props all other standard input HTML attributes
 * @returns JSX input wrapper element
 */
export function Input({ label, error, className = '', id, type, ...props }: InputProps) {
  // Generate ID from label if not provided
  const inputId = id || (label ? `input-${label.toLowerCase().replace(/\s+/g, '-')}` : undefined)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [fileName, setFileName] = useState<string>('No file selected')
  
  // Custom file input with English button text
  if (type === 'file') {
    const handleFileButtonClick = () => {
      fileInputRef.current?.click()
    }
    
    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
      setFileName(file ? file.name : 'No file selected')
      if (props.onChange) {
        props.onChange(e)
      }
    }
    
    return (
      <div className="input-wrapper">
        {label && <label htmlFor={inputId} className="input-label">{label}</label>}
        <div className="file-input-custom">
          <input
            ref={fileInputRef}
            id={inputId}
            type="file"
            className={`input input-file-hidden ${error ? 'input-error' : ''} ${className}`}
            {...props}
            onChange={handleFileChange}
          />
          <button
            type="button"
            className="file-input-button"
            onClick={handleFileButtonClick}
            disabled={props.disabled}
          >
            Choose File
          </button>
          <span className="file-input-text">
            {fileName}
          </span>
        </div>
        {error && <span className="input-error-text">{error}</span>}
      </div>
    )
  }
  
  return (
    <div className="input-wrapper">
      {label && <label htmlFor={inputId} className="input-label">{label}</label>}
      <input
        id={inputId}
        type={type}
        className={`input ${error ? 'input-error' : ''} ${className}`}
        {...props}
      />
      {error && <span className="input-error-text">{error}</span>}
    </div>
  )
}

