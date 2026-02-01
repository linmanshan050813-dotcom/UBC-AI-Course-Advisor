# AI Course Advisor — Requirements

---

## 1. Functional Requirements

### 1.1 Upload Module
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-U-01** | The system shall allow a professor to select a course document in PDF, DOCX, or TXT format and initiate an upload through the web interface. | Upload test with each file type succeeds → HTTP 200. |
| **R-U-02** | The system shall parse and store the uploaded file within 5 seconds and display a confirmation message “Course file uploaded successfully.” | Timer + UI check for message. |
| **R-U-03** | If the file type is unsupported or corrupted, the system shall display “Upload failed: file type not supported.” | Upload invalid file → error message appears. |
| **R-U-04** | Uploading a new file for the same course shall replace the existing index while preserving system stability (no crash or timeout). | Regression test on multiple uploads. |
| **R-U-05** | After upload completion, the server shall create a searchable text index from the document’s content. | Query test returns non-empty results. |

---

### 1.2 Ask AI Module
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-Q-01** | The system shall provide a text input area for students to enter natural-language questions. | UI inspection and typing test. |
| **R-Q-02** | Upon submission, the backend shall retrieve relevant text segments from the indexed document and generate a summarized answer through the AI engine. | Unit test with mock AI returns summary string. |
| **R-Q-03** | Each AI answer shall include at least one citation identifier (e.g., section number or page number). | Response JSON contains `citation` field. |
| **R-Q-04** | If no matching information is found, the system shall return “Sorry, I could not find grading details in the uploaded syllabus.” | Negative query test. |
| **R-Q-05** | The system shall return answers within 3 seconds for 95 % of queries (using mock AI mode). | Performance test logs. |
| **R-Q-06** | When a student asks a question that is unrelated to the uploaded course material (e.g., off-topic or personal questions), the system shall automatically respond with the message “Please ask questions related to the course”. | Query test with unrelated input (e.g., “What is your favorite movie?”) returns the specified message within 3 seconds. |


---

### 1.3 Citation Viewer
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-C-01** | When a user clicks “View Source,” the system shall display the exact text segment used to generate the answer. | UI popup contains matching snippet. |
| **R-C-02** | The displayed segment shall highlight the matched lines visually (e.g., yellow background). | CSS inspection or screenshot comparison. |
| **R-C-03** | If the original file is missing, the system shall show “Original file not found.” | Remove file → check error handling. |

---

### 1.4 History Module
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-H-01** | The system shall store the 10 most recent student queries and their AI responses per course. | Database entry count ≤ 10. |
| **R-H-02** | Queries and answers shall be displayed in reverse chronological order. | Manual UI check. |
| **R-H-03** | If no previous queries exist, the system shall display “Start by asking your first question!”. | Fresh account test. |

---

### 1.5 Professor and Student Workflow
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-W-01** | Professors shall be able to log in and manage their own uploaded files. | Login + Upload test per user. |
| **R-W-02** | Students shall be able to access the chat interface without administrator privileges. | User role check. |
| **R-W-03** | Professors shall be able to delete or replace a syllabus at any time. | API DELETE/PUT call returns 200. |

---

## 2. Non-Functional Requirements
| ID | Requirement | Verification |
|----|--------------|--------------|
| **R-N-01** | The system shall support ≥ 20 concurrent requests without degradation beyond 10 % response delay. | Load test with 20 threads. |
| **R-N-02** | System uptime shall be ≥ 99 % during testing phase. | Monitoring logs. |
| **R-N-03** | User data and course files shall be stored securely with access control by role (professor/student). | Security audit of routes. |
| **R-N-04** | Average AI response time shall not exceed 3 seconds using mock mode. | Timed E2E test. |
| **R-N-05** | The frontend UI shall be usable on both desktop and mobile browsers (Chrome, Edge, Safari). | Responsive layout inspection. |
| **R-N-06** | System components shall use only open-source or free libraries. | Dependency review. |
| **R-N-07** | All critical errors shall be logged with timestamp and stack trace. | Log inspection. |

---

## 3. User Scenarios
| Scenario ID | Description | Expected Outcome |
|--------------|-------------|------------------|
| **S-01** | Professor uploads a syllabus → student asks “Can I submit Lab 3 late?” | AI responds with policy text + citation. |
| **S-02** | Student asks a question not in syllabus. | System returns “not found” message in under 3 s. |
| **S-03** | Professor replaces old syllabus. | New index overwrites previous data; old answers invalidate. |
| **S-04** | Multiple students ask questions concurrently. | All requests processed without failure or blocking. |

---

## 4. Testing and Verification Mapping
| Requirement Group | Linked Test Type | Example |
|--------------------|-----------------|----------|
| Upload & Parsing | Unit Test | Mock file upload + assert status 200. |
| Ask AI | Integration Test | Client POST → AI engine → returns JSON with citation. |
| Cite Source & History | E2E Test | Ask question → View Source → History entry visible. |
| Non-Functional | Load + Performance Test | 20 threads query latency < 3 s (95 %). |

---

## 5. Acceptance Criteria Summary
- All functional requirements **R-U-01 → R-H-03** verified through unit and integration tests.  
- All non-functional requirements **R-N-01 → R-N-07** verified through load and security tests.  
- System passes ≥ 95 % of automated test cases before final submission.  

---
