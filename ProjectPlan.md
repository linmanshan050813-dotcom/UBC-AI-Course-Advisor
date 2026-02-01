# Project Plan — AI Course Advisor

## 1. Coordination — How will you coordinate your work?

**Coordinator:**  
Manshan Lin (PM) coordinates all work, monitors progress, and ensures deliverables meet deadlines.

**Project-management practices:**  
- Weekly Zoom meetings every Friday evening (approximately 1 hour).  
- PM prepares agendas and moderates meetings.  
- Each member reports progress, blockers, and next tasks.  
- GitHub Projects and Issues are used for assignment tracking; every feature branch must pass peer review before merging to main.  

**Justification:**  
A fixed weekly cadence ensures accountability and keeps everyone synchronized without excessive meetings.  
Zoom provides reliable cross-platform meetings and screen-sharing for live debugging and demos.

---

## 2. Communication Tools — What tools will you use to communicate?

| Tool | Purpose | Alternatives | Rationale |
|------|----------|---------------|------------|
| Zoom | Weekly team meeting and real-time discussion | Discord, Google Meet | Stable video, good screen sharing for live debugging |
| Google Docs | Collaborative editing of specs and reports | Notion | Allows real-time multi-user editing and transparent history |
| Instagram Group Chat | Quick updates and informal coordination | Slack, Discord | All members already use Instagram; convenient for short discussions |
| GitHub Issues/Projects | Task tracking and milestone management | Trello, Jira | Integrated with repository and CI/CD pipeline |

**Justification:**  
These tools are free, accessible, and already part of the team’s workflow. They minimize friction while supporting both synchronous and asynchronous collaboration.

---

## 3. Component Ownership — Who will own each component

Each component listed below is assigned to one team member, who is responsible for implementing, testing, and ensuring the correctness and stability of that component.  
Ownership means accountability for functionality, code quality, and integration with other modules.

| Component | Description | Primary Owner | Supporting Member |
|------------|-------------|----------------|-------------------|
| **UploadModel** | Handles syllabus uploads and stores metadata. | **Harpreet Singh (Backend)** | **ChengKai Xiao** |
| **IndexModel** | Stores searchable syllabus indexes for query retrieval. | **Anthony Lu (Storage/Cache)** | **ChengKai Xiao** |
| **AIQueryModel** | Manages query processing and communicates with AI engine. | **Manshan Lin (AI Engine)** | **ChengKai Xiao** |
| **CitationAPI** | Fetches original syllabus text segments for citations. | **Anthony Lu (Storage/Cache)** | **ChengKai Xiao** |
| **UploadView** | Client UI for uploading syllabus files and showing status. | **ZhanMing Sun (Frontend Designer)** | **ChengKai Xiao** |
| **FileParserModel** | Extracts and cleans text from uploaded syllabus files. | **Harpreet Singh (Backend)** | Anthony Lu |
| **AIEngine** | Generates summarized answers using pretrained AI models. | **Manshan Lin (AI Engine)** | Harpreet Singh |
| **CitationModel** | Stores and retrieves syllabus text segments used in citations. | **Anthony Lu (Storage/Cache)** | Manshan Lin |
| **HistoryModel** | Maintains query–response history per course (client + server). | **Anthony Lu (Storage/Cache)** | Harpreet Singh |
| **AuthModel** | Manages authentication and user roles. | **Harpreet Singh (Backend)** | Manshan Lin |
| **AIQueryAPI** | Handles query requests; retrieves and returns AI responses. | **Manshan Lin (AI Engine)** | Harpreet Singh |
| **HistoryAPI** | Retrieves and saves user query histories. | **Anthony Lu (Storage/Cache)** | Harpreet Singh |
| **AuthAPI** | Handles login and authentication requests. | **Harpreet Singh (Backend)** | Manshan Lin |
| **ChatView** | Displays user–AI conversation and handles query submissions. | **ZhanMing Sun (Frontend Designer)** | Manshan Lin |
| **CitationView** | Shows cited syllabus segments retrieved from server. | **ZhanMing Sun (Frontend Designer)** | Anthony Lu |
| **HistoryView** | Displays query history and previous responses. | **ZhanMing Sun (Frontend Designer)** | Anthony Lu |

---

**Notes:**
- Backend and API development are primarily owned by **Harpreet Singh**.  
- AI-related logic and NLP processing are managed by **Manshan Lin**.  
- Data persistence, caching, and performance optimization are handled by **Anthony Lu**.  
- All frontend (Views) and user experience design are developed by **ZhanMing Sun**.  
- **ChengKai Xiao** is responsible for testing, end-to-end validation, and CI/CD integration.


## 4. Timeline and Milestones

| Date | Milestone | Deliverables | Responsible |
|------|------------|---------------|--------------|
| Oct 27 (Week 1) | Define team and problem | README.md, TeamDesign.md | PM, Designer |
| Oct 31 (Week 2) | Complete design specification | Solution_Design.md | Designer, PM |
| Nov 7 (Week 3) | Submit project plan | ProjectPlan.md | All members |
| Nov 14 (Week 4) | Develop backend and AI integration | API and model modules | Backend, AI Developer |
| Nov 21 (Week 5) | Integrate frontend and testing | Functional MVP demo | Frontend, Tester |
| Nov 28 (Week 6) | Finalize release | Demo video and documentation | All members |

**Justification:**  
The timeline aligns with course milestones.  
Development between November 7 and November 28 focuses on integration and preparing the product launch.

---

## 5. Verification — How will you verify that you’ve met your requirements?
## Verification Plan

| Req ID | Feature | Target Files/Components | Verification Method | Pass Criteria | Responsible |
|--------|---------|------------------------|---------------------|---------------|-------------|
| R1 | Upload Syllabus | UploadController.java, uploadApi.js, UploadView.jsx | Unit + Integration Tests | HTTP 200 for valid files, proper error codes, metadata stored | Harpreet Singh |
| R2 | AI Query and Response Generation | queryApi.js, AIEngine.java, ChatView.jsx | Integration + Mock Tests | 100% accuracy, ≥1 citation/response, avg <2s | ChengKai Xiao |
| R3 | Storage and Cache Performance | IndexModel.java, CitationModel.java, HistoryModel.java | Unit + Performance Tests | P95 latency < 1.5s (warm), cache hit rate ≥ 60%, no data loss | Anthony Lu |
| R4 | Citation Display Accuracy | CitationView.jsx, citationApi.js | Manual + Integration + E2E Tests | Highlights match backend offsets, correct sources open, clear error messages |ZhanMing Sun |
| R5 | Chat History Persistence | HistoryController.java, historyApi.js, HistoryView.jsx | Integration Tests | Correct ordering, no record loss, functional pagination | Anthony Lu |
| R6 | UI Responsiveness and Accessibility | ChatView.jsx, UploadPanel.jsx, CitationView.jsx, HistoryView.jsx | Accessibility + Responsiveness Tests (Lighthouse, axe-core) | Layout adapts to all screens, full keyboard navigation, WCAG AA contrast met |ZhanMing Sun |
| R7 | End-to-End System + CI/CD | Full system, GitHub Actions pipeline | E2E Tests + CI/CD Automation | 4 stages pass, auto-run on push/nightly, >95% success | ChengKai Xiao |
| R8 | Authentication and Authorization | AuthController.java, authApi.js | Integration + Security Tests | Valid login returns 200, invalid returns 401, JWT validation works | Harpreet Singh |

---

## R1: Upload Syllabus

### Verification Method
Unit tests for file validation + integration tests for full upload flow.

### Test Cases

**Test 1: Valid PDF Upload**
- **Input:** Valid PDF file (under 5MB)
- **Expected:** HTTP 200 response + file saved + metadata stored in database

**Test 2: Invalid File Types**
- **Input:** Non-PDF files (.txt, .docx, .jpg)
- **Expected:** HTTP 400 error + clear error message

**Test 3: File Size Limits**
- **Input:** PDF file over size limit (>5MB)
- **Expected:** HTTP 413 error + "File too large" message

### Pass Criteria
- Valid PDFs return HTTP 200 and save correctly
- Invalid files return proper 4xx error codes
- File metadata (name, size, upload time) stored in database
- No crashes or memory leaks during upload

### Code Review Focus
- Input validation for file type and size
- Error handling for corrupt or empty files
- Database transaction handling for metadata storage

### Integration Timing
- **On commit:** Unit tests run automatically
- **On PR:** Full integration tests + code review required
- **Daily:** Performance tests with large files

___

## R2: AI Query and Response Generation

### Verification Method
Integration tests with mock AI responses in CI, real API in staging.

### Test Cases

**Test 1: Grading Policy Query**
- **Input:** "What is the grading breakdown for this course?"
- **Expected:** Accurate grading percentages + valid citation to "Grading" section + response time <2s

**Test 2: Deadline Query**
- **Input:** "When is the final project due?"
- **Expected:** Accurate deadline date + valid citation + response time <2s

**Test 3: Office Hours Query**
- **Input:** "What are the professor's office hours?"
- **Expected:** Complete office hours info (time, location) + valid citation + response time <2s

### Pass Criteria
-  All 3 tests: 100% accuracy (no fabricated info)
-  Every response has ≥1 valid citation with correct paragraph index
-  Average response time <2s (95th percentile <3s)

### Code Review Focus
- Prompt structure requires citations and prohibits fabrication
- Truncation logic preserves paragraph boundaries
- Timeout set to 3-5s with exponential backoff retry

### Integration Timing
- **On push:** CI runs all tests automatically (mock AI)
- **On PR:** Full test suite + code review required
- **Nightly:** Tests with real AI API + performance benchmarks

___

## **R3: Storage and Cache Performance**

### Verification Method  
Unit and performance tests focusing on basic cache functionality, latency, and data correctness under normal load.

### Target Files / Components  
`IndexModel.java`, `CitationModel.java`, `HistoryModel.java`

### Test Cases  

**Test 1: Cache Warm-Up Performance**  
- **Input:** Repeated queries requesting syllabus sections to warm the cache  
- **Expected:** Subsequent queries have latency < 1.5 seconds (warm cache)

**Test 2: Cache Eviction and Rebuild**  
- **Input:** Simulate cache eviction and trigger index rebuild  
- **Expected:** No stale data reads; cache repopulated without errors

**Test 3: Data Integrity Check**  
- **Input:** Concurrent read/write operations on storage models  
- **Expected:** No data loss or duplication

**Test 4: Stress Test (Concurrency)**  
- **Input:** Multiple parallel requests performing mixed reads and writes  
- **Expected:** P99 latency < 3 seconds; no crashes or deadlocks

### Pass Criteria  
- P95 latency < 1.5 seconds (warm cache) and < 3 seconds (cold cache)  
- Cache hit rate ≥ 60%  
- No stale or lost data during normal concurrency  
- Index and retrieval operate correctly

### Code Review Focus  
- Cache key design and efficiency  
- Basic thread safety and atomicity in critical operations  
- Data serialization and consistency  
- Basic logging of cache metrics

### Integration Timing  
- **On commit:** Run unit tests for cache and data consistency  
- **On PR:** Run performance tests with moderate concurrency  
- **Nightly:** Run regression tests

---

## R4: Citation Display Accuracy

### Verification Method
Manual checks, component tests, integration tests, and end-to-end (E2E) tests.

### Target Files / Components
`CitationView.jsx`, `citationApi.js`, `SourceViewer` (dialog/panel), API: `/api/citations?answerId=...`

### Test Cases

**Test 1: Single Citation**
- **Input:** One citation with `startOffset`, `endOffset`.
- **Steps:** Render `CitationView`, inspect highlighted text.
- **Expected:** Highlight equals `text.slice(startOffset, endOffset)`.

**Test 2: Multiple Citations**
- **Input:** Several citations in one answer.
- **Steps:** Click each citation chip and each highlighted range.
- **Expected:** Each opens the correct source section.

**Test 3: Invalid Citation**
- **Input:** Offsets out of bounds or `start >= end`.
- **Steps:** Render with bad data.
- **Expected:** Friendly message “Source range invalid.” No crash.

**Test 4: Missing Source**
- **Input:** Source fetch returns 404.
- **Steps:** Click a citation.
- **Expected:** Message “Original file not found.” App continues to work.

**Test 5: Performance**
- **Input:** Paragraph > 1000 chars, 8+ citations.
- **Steps:** Load and interact.
- **Expected:** Highlights appear quickly (under ~0.3 s). No UI jank.

### Pass Criteria
- Exact highlight range matches backend offsets.  
- Clicking any citation opens the correct file/paragraph.  
- Clear, readable error messages for invalid or missing references.  
- No crashes; scrolling/typing remains responsive.

### Code Review Focus
- Separation of UI vs. API logic.  
- Error boundary coverage for invalid data.  
- Focus management for Source dialog (trap focus, `Esc` closes).  
- Off-by-one checks on range math; tests assert exact substring.

### Integration Timing
- **On commit:** unit + component tests.  
- **On PR:** integration tests + manual spot check.  
- **Nightly:** full E2E — upload → query → view citation → view history.

___

## **R5: Chat History Persistence**

### Verification Method  
Integration tests verifying basic persistence, ordering, and display of student query–response history.

### Target Files / Components  
`HistoryController.java`, `historyApi.js`, `HistoryView.jsx`

### Test Cases  

**Test 1: Multi-Session History Retrieval**  
- **Steps:** Submit queries across sessions, reload history  
- **Expected:** Queries and responses persist and display correctly

**Test 2: Pagination and Ordering**  
- **Steps:** Retrieve history spanning multiple pages (≥10 entries)  
- **Expected:** Approximate chronological order and pagination without major gaps

**Test 3: Refresh Persistence**  
- **Steps:** Refresh client after queries  
- **Expected:** No obvious duplicates or missing entries; UI roughly matches server data

**Test 4: Concurrent Access**  
- **Steps:** Two clients concurrently add/view history for same user  
- **Expected:** No critical data loss; minor sync delays acceptable

### Pass Criteria  
- Query–response pairs persist across sessions  
- History mostly in correct order across pages  
- Pagination functional with no critical failures  
- No major data corruption under concurrency

### Code Review Focus  
- Database transactions and atomic updates in controller  
- Sorting and timestamps handling in API and frontend  
- Pagination implementation correctness  
- Basic API-client schema compatibility

### Integration Timing  
- **On commit:** Unit tests for database and API methods  
- **On PR:** Integration tests with frontend validation  
- **Nightly:** Regression tests for persistence and UI behavior

---

## R6: UI Responsiveness & Accessibility

### Verification Method
Automated checks with **Lighthouse** and **axe-core** plus manual keyboard and screen-size sweeps.

### Target Files / Components
All user views: `ChatView`, `UploadPanel`, `CitationView`, `HistoryView`.

### Test Cases

**Test 1: Responsive Layout**
- **Steps:** Resize to 320, 375, 768, 1024, 1280 px.
- **Expected:** No horizontal scroll; content reflows; chips wrap; targets ≥ 44×44 px.

**Test 2: Keyboard Navigation**
- **Steps:** Use **Tab** / **Shift+Tab** / **Enter** / **Esc** across the app and dialogs.
- **Expected:** All controls focusable and operable; visible focus ring; dialog traps focus.

**Test 3: Contrast Check**
- **Steps:** Run axe + Lighthouse.
- **Expected:** All text/icons meet WCAG AA (≥ 4.5:1). No critical issues.

**Test 4: Reduced Motion & Zoom**
- **Steps:** Enable OS “Reduce Motion”; set browser zoom to 200%.
- **Expected:** Animations reduce/stop; layout and features still work.

### Pass Criteria
- Layout adapts well on mobile, tablet, and desktop.  
- Full keyboard support with clear focus styles.  
- Lighthouse Accessibility score ≥ 90.  
- axe-core reports **0 critical** issues.

### Code Review Focus
- Semantic HTML (`button`, `a`, `main`, `nav`).  
- Correct ARIA roles and labels where needed.  
- Logical focus order; status updates use `aria-live="polite"`.  
- Contrast and motion preferences respected.

### Integration Timing
- **On push:** axe scan.  
- **On PR:** Lighthouse run + manual keyboard pass.  
- **Nightly:** responsive matrix (mobile & desktop widths) and accessibility run.

---

## R7: End-to-End System + CI/CD

### Verification Method
End-to-end tests via Selenium/Playwright + automated CI/CD pipeline.

### CI/CD Pipeline (4 Stages, <20 min total)

1. **Build (<5 min)** — Install dependencies, compile, package
2. **Unit Tests (<3 min)** — Jest + JUnit, coverage >70%
3. **Integration Tests (<5 min)** — Run R2 Tests 1-3 with mock services
4. **E2E Tests (<10 min)** — Complete user workflow tests

### E2E Test Cases

**Test 1: Complete User Workflow**
- **Steps:** Login → Upload syllabus → Submit query → View response + citation → Check history
- **Expected:** Completes in <30s, all data displays correctly

**Test 2: Concurrent Users**
- **Steps:** 10 users perform Test 1 simultaneously
- **Expected:** All succeed, response time increase <20%, no crashes

**Test 3: Error Recovery**
- **Steps:** Simulate AI API failure, database failure, invalid file upload
- **Expected:** User-friendly errors, graceful recovery

### Pass Criteria
-  All 4 CI/CD stages functional and auto-run on every push and nightly
-  All 3 E2E tests pass consistently
-  Pipeline success rate >95% over 7 days

### Code Review Focus
- Error handling at integration points with graceful degradation
- API versions clearly specified, frontend-backend schemas match

---

## R8: Authentication and Authorization

### Verification Method
Integration tests for login flow + security tests for token validation.

### Test Cases

**Test 1: Valid Login**
- **Input:** Correct username and password
- **Expected:** HTTP 200 + valid JWT token returned

**Test 2: Invalid Credentials**
- **Input:** Wrong username or password
- **Expected:** HTTP 401 + "Invalid credentials" message

**Test 3: Token Validation**
- **Input:** API requests with valid/expired/invalid tokens
- **Expected:** Valid tokens allow access, invalid tokens return HTTP 401

### Pass Criteria
- Successful login returns HTTP 200 with valid JWT
- Failed login returns HTTP 401 with clear message
- JWT tokens expire correctly and are validated properly
- No unauthorized access to protected endpoints

### Code Review Focus
- Password hashing and validation logic
- JWT token generation and expiration handling
- Authorization checks on protected routes

### Integration Timing
- **On commit:** Unit tests for auth logic
- **On PR:** Full auth flow tests + security review
- **Daily:** Token expiration and security tests

---

## Final E2E Before Release
**Flow:** upload → ask AI → view citation → open history  
**Expected:** Pass on desktop and mobile widths with no accessibility violations and accurate citations.

---

## Summary

**Responsibilities:** Implement upload validation tests, auth flow tests, ensure proper error handling and security for backend endpoints.

**Timeline:**
- Nov 10-17: Implement R1 upload tests + validation logic
- Nov 18-23: Implement R8 auth tests + security checks
- Nov 24-28: Integration testing + security hardening

---


## 6. Rationale for Our Choices
- GitHub provides visibility, version control, and CI/CD support in one place.  
- Zoom and Google Docs allow high-quality collaboration without cost.  
- Instagram chat keeps the team responsive outside formal meetings.  
- Role-based ownership increases accountability and parallel development.  
- Verification strategy balances automation and usability testing.  

---

## 7. Risk Management

| Risk | Mitigation |
|------|-------------|
| Member absence or illness | All code pushed daily to GitHub; backup owner assigned for each component |
| API limitations | Mock AI or local model fallback |
| Integration delays | Buffer week (Nov 21 – Nov 28) reserved for testing and bug fixes |
| Merge conflicts | Use feature branches and daily pull requests |
| Requirement change or unclear scope | PM confirms project scope with TA before implementation |

**Justification:**  
By anticipating schedule slips and API issues, we ensure that our project remains feasible and on track.

---

## Summary
- Coordination: Weekly Zoom meetings and GitHub tracking  
- Communication: Zoom, Google Docs, and Instagram chat  
- Ownership: Module-based assignments with peer reviews  
- Timeline: Aligned with official milestones (Nov 7–Nov 28 core development)  
- Testing: Automated and manual verification  
- Resilience: Backup roles and fallback models ensure project completion  

This plan represents our team’s shared understanding of how we will coordinate, develop, verify, and deliver the AI Course Advisor project successfully.

