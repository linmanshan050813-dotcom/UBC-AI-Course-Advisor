# Known Issues - AI Course Advisor

**Project**: AI Course Advisor (Project Willow)  
**Maintained by**: Chenkai Xiao  
**Last Updated**: November 30, 2025

---

## Issue Summary

| ID | Severity | Module | Status | Owner |
|----|----------|--------|--------|-------|
| BUG-001 | MEDIUM | UI Accessibility | Reported | Zhanming Sun |
| BUG-002 | MEDIUM | UI Accessibility | Reported | Zhanming Sun |

---

## BUG-001: Lighthouse Accessibility Score Below Target

### Overview
- **Severity**: MEDIUM
- **Requirement**: R-N-05 (WCAG AA, score ≥90)
- **Current Score**: 85/100

### Issues Found (Re-verified/Confirmed)
1.  **5 buttons missing aria-labels** (icon buttons)
    -   Confirmed in `frontend/src/app/upload/page.tsx`: The "Delete" (`×`) button for past uploads has a `title` attribute but no `aria-label`.
2.  **3 text elements** with insufficient contrast (3.2:1 vs 4.5:1)
    -   (Assumed accurate, requires visual inspection/tooling to confirm CSS contrast ratios).
3.  **1 form input** without associated label
    -   Confirmed in `frontend/src/components/ChatInput.tsx`: The main chat `<input type="text">` lacks an explicit `<label>` or `aria-label`.
    -   Confirmed in `frontend/src/app/upload/page.tsx`: The file `<input type="file">` (rendered by `Input` component without a `label` prop) lacks an explicit `<label>` or `aria-label`.

### Solutions

**Missing aria-labels**:
```tsx
// Example Fix for Upload Page Delete Button (conceptual)
<button 
    onClick={...}
    title="Delete"
    aria-label="Delete uploaded file" // Add this
>
    ×
</button>
```

**Color contrast**:
```css
/* Before: #999 on #fff (3.2:1) */
.secondary-text { color: #999999; }

/* After: #595959 on #fff (7.1:1) */
.secondary-text { color: #595959; }
```

**Form input without label**:
```tsx
// Example Fix for ChatInput (conceptual)
<label htmlFor="chat-input" className="sr-only">Ask a question</label>
<input id="chat-input" type="text" ... />

// Example Fix for Upload Page file input (conceptual)
<Input
    id="file-input"
    type="file"
    label="Upload Syllabus File" // Pass label prop
    accept=".pdf,.txt,.docx"
    onChange={handleFileChange}
    disabled={uploading}
/>
```

**Priority**: P2 (Medium)  
**Owner**: Zhanming Sun

---

## BUG-002: Axe-core Critical Accessibility Issues

### Overview
- **Severity**: MEDIUM
- **Requirement**: R-N-05 (0 critical issues)
- **Current**: 2 critical issues

### Issues (Re-verified/Confirmed)

**Issue 1**: Navigation links without accessible names
-   **Analysis**: `frontend/src/components/Navbar.tsx` uses standard `<Link>` components with visible text (e.g., "Home", "Upload", "Ask"). These generally provide sufficient accessible names. The bug description's example implying icon-only links (`<HistoryIcon />`) does not match the current `Navbar.tsx` implementation. This issue might be resolved or the bug description refers to a different component/state.

**Issue 2**: Form controls without labels
-   Confirmed in `frontend/src/components/ChatInput.tsx`: The main chat `<input type="text">` lacks an explicit `<label>` or `aria-label`.
-   Confirmed in `frontend/src/app/upload/page.tsx`: The file `<input type="file">` (rendered by `Input` component without a `label` prop) lacks an explicit `<label>` or `aria-label`.

### Fixes (Conceptual)
```tsx
// Fix for Form controls: Add label element
<label htmlFor="chat-input" className="sr-only">
  Ask a question about the course
</label>
<textarea id="chat-input" placeholder="Ask..." />

// Fix for Upload file input: Pass label prop
<Input label="Select Syllabus File" id="file-input" type="file" ... />
```

**Priority**: P2 (Medium)  
**Owner**: Zhanming Sun

---

## Bug Fix Priority

### P1 - High Priority (This Week)
-   None identified for immediate P1 action based on current code review.

### P2 - Medium Priority (Next Sprint)
- BUG-001: Lighthouse accessibility score
- BUG-002: Axe-core critical issues

### P3 - Low Priority (Backlog)
- None identified for immediate P3 action based on current code review.

---

## Testing Notes

After fixing bugs, run regression tests:
1. Citation highlighting test (with special characters) - `npm test CitationView.test.tsx`
2. Lighthouse accessibility scans
3. Axe-core accessibility validation

---

**Maintained by**: Chenkai Xiao  
**Date**: November 30, 2025
