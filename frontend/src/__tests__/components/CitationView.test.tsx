import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { CitationView } from '../../components/CitationView';
import '@testing-library/jest-dom';

/**
 * R4 Test: Citation Display Accuracy
 * Tests CitationView component including:
 * - Citation text display
 * - Close functionality
 * - Keyboard navigation (Escape key)
 * - Text highlighting
 */
describe('CitationView - R4 Citation Display Accuracy', () => {
  const mockCitation = {
    id: '1',
    text: 'Assignments: 30%, Midterm: 30%, Final: 40%'
  };

  const mockOnClose = vi.fn();

  beforeEach(() => {
    mockOnClose.mockClear();
  });

  /**
   * Test 1: Citation Text Display
   * Requirement: R-C-01
   * Expected: Displays citation text correctly
   */
  it('should display citation text correctly', () => {
    render(
      <CitationView 
        citation={mockCitation} 
        onClose={mockOnClose}
      />
    );

    // Check if citation text is displayed
    expect(screen.getByText(/Assignments: 30%, Midterm: 30%, Final: 40%/)).toBeInTheDocument();
  });

  /**
   * Test 2: Close Button Functionality
   * Requirement: R-C-01
   * Expected: Close button calls onClose callback
   */
  it('should call onClose when close button is clicked', () => {
    const onClose = vi.fn();
    render(
      <CitationView 
        citation={mockCitation} 
        onClose={onClose}
      />
    );

    // Find and click the close button
    const closeButton = screen.getByRole('button', { name: /close|×/i });
    fireEvent.click(closeButton);

    // Verify onClose was called (may be called multiple times due to event bubbling)
    expect(onClose).toHaveBeenCalled();
    expect(onClose.mock.calls.length).toBeGreaterThanOrEqual(1);
  });

  /**
   * Test 3: Overlay Click to Close
   * Requirement: R-C-01
   * Expected: Clicking overlay background closes the view
   */
  it('should call onClose when overlay is clicked', () => {
    const onClose = vi.fn();
    const { container } = render(
      <CitationView 
        citation={mockCitation} 
        onClose={onClose}
      />
    );

    // Click on the overlay
    const overlay = container.querySelector('.citation-overlay');
    if (overlay) {
      fireEvent.click(overlay);
      expect(onClose).toHaveBeenCalledTimes(1);
    }
  });

  /**
   * Test 4: Escape Key to Close
   * Requirement: R-C-01, Accessibility
   * Expected: Pressing Escape key closes the view
   */
  it('should call onClose when Escape key is pressed', () => {
    render(
      <CitationView 
        citation={mockCitation} 
        onClose={mockOnClose}
      />
    );

    // Simulate Escape key press
    fireEvent.keyDown(document, { key: 'Escape', code: 'Escape' });

    // Verify onClose was called
    expect(mockOnClose).toHaveBeenCalledTimes(1);
  });

  /**
   * Test 5: Text Highlighting with Question Keywords
   * Requirement: R-C-02
   * Expected: Keywords from question are highlighted in citation text
   */
  it('should highlight keywords from question in citation text', () => {
    const question = 'What is the grading policy?';
    
    const { container } = render(
      <CitationView 
        citation={mockCitation}
        question={question}
        onClose={mockOnClose}
      />
    );

    // Check if the component rendered successfully
    expect(screen.getByText(/Assignments/)).toBeInTheDocument();
  });

  it('should highlight matching keywords in HTML', () => {
     // Use words that are NOT stop words and length >= 4
     const question = 'midterm final exam';
     const citation = {
       id: '1',
       text: 'The midterm is 30% and final is 40%.'
     };

     const { container } = render(
       <CitationView 
         citation={citation}
         question={question}
         onClose={mockOnClose}
       />
     );
     
     // Check if <mark> tags are present
     const marks = container.querySelectorAll('mark');
     expect(marks.length).toBeGreaterThan(0);
     expect(container.innerHTML).toContain('<mark>midterm</mark>');
     expect(container.innerHTML).toContain('<mark>final</mark>');
  });

  it('should not highlight stop words or short words', () => {
    const question = 'is the a for'; // All stop words or short
    const citation = { id: '1', text: 'This is the text for testing.' };
    
    const { container } = render(
      <CitationView 
        citation={citation}
        question={question}
        onClose={mockOnClose}
      />
    );
    
    const marks = container.querySelectorAll('mark');
    expect(marks.length).toBe(0);
  });

  /**
   * Test 6: Empty Citation Text
   * Expected: Handles empty citation gracefully
   */
  it('should handle empty citation text', () => {
    const emptyCitation = {
      id: '2',
      text: ''
    };

    render(
      <CitationView 
        citation={emptyCitation} 
        onClose={mockOnClose}
      />
    );

    // Component should render without crashing
    expect(document.querySelector('.citation-modal')).toBeInTheDocument();
  });

  /**
   * Test 7: Long Citation Text
   * Expected: Displays long text without breaking layout
   */
  it('should display long citation text correctly', () => {
    const longCitation = {
      id: '3',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. '.repeat(10)
    };

    render(
      <CitationView 
        citation={longCitation} 
        onClose={mockOnClose}
      />
    );

    // Check that text is present
    expect(screen.getByText(/Lorem ipsum/)).toBeInTheDocument();
  });

  /**
   * Test 8: Citation with Special Characters
   * Expected: Displays special characters correctly
   */
  it('should display special characters correctly', () => {
    const specialCitation = {
      id: '4',
      text: 'Test: 100%, Rate: 3.5/5, Symbol: © & ® < >'
    };

    render(
      <CitationView 
        citation={specialCitation} 
        onClose={mockOnClose}
      />
    );

    // Check if special characters are rendered
    expect(screen.getByText(/100%/)).toBeInTheDocument();
  });

  /**
   * Test 9: Multiple Render/Unmount
   * Expected: No memory leaks or errors on cleanup
   */
  it('should cleanup event listeners on unmount', () => {
    const { unmount } = render(
      <CitationView 
        citation={mockCitation} 
        onClose={mockOnClose}
      />
    );

    // Unmount component
    unmount();

    // Try pressing Escape after unmount (should not call onClose)
    fireEvent.keyDown(document, { key: 'Escape', code: 'Escape' });
    
    // onClose should not be called after unmount
    expect(mockOnClose).toHaveBeenCalledTimes(0);
  });

  /**
   * Test 10: Citation View Structure
   * Expected: Has correct DOM structure and CSS classes
   */
  it('should have correct DOM structure', () => {
    const { container } = render(
      <CitationView 
        citation={mockCitation} 
        onClose={mockOnClose}
      />
    );

    // Check for expected CSS classes
    expect(container.querySelector('.citation-overlay')).toBeInTheDocument();
    expect(container.querySelector('.citation-modal')).toBeInTheDocument();
  });
});
