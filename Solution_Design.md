*Making UBC course information accessible and intelligent*  
**Course:** CPEN 221 — Software Construction  

---

## Problem

University students constantly face the challenge of sifting through long course documents to find simple information, whether about course logistics or curricular content. For instance, a student wanting to know “Can I submit Lab 3 two days late?” often has to search through ten or more pages of policy documents. Professors, in turn, spend valuable time each term repeating the same clarifications via email, office hours, or the discussion board. 

While all of this information technically exists, it is often buried in unstructured text that is can be difficult to access efficiently and may be easily missed. Existing tools, such as keyword searching in PDFs, rely on instructors using the same terminology as students in order for students to easily find answers. 

This lack of structure can cause unnecessary confusion, lost productivity, and frustration for both students and instructors. In the context of large university courses like those at UBC, how might this network of course information become a place that not only prevents misunderstanding, but also immediately directs students to accurate, instructor-approved material?

---

## Solution

### Overview
We propose **AI Course Advisor**, an interactive web application that allows professors to upload course documents and enables students to query them through natural-language questions. The system instantly extracts precise answers from the uploaded material and cites the original section where it found answers for transparency. Our MVP would deal with course syllabi in particular.

---

### Core Functionality

| Module | User Action | System Response | Error / Edge Cases |
|---------|--------------|----------------|--------------------|
|**Upload** | Professor selects PDF/DOCX/TXT syllabus and clicks “Upload.” | File is parsed and stored; confirmation message “Course file uploaded successfully.” | If unsupported format: “Upload failed: file type not supported.” |

> MVP Note (R1): The upload controller currently enforces PDF-only with a 5MB limit to align with verification. DOCX/TXT acceptance and richer parsing are planned for a later milestone.
|**Ask AI** | Student types a question (ex., “What is the weight of the final exam?”) | AI searches parsed text, extracts relevant paragraph, and returns a concise answer with citation. | If no relevant info: “Sorry, I could not find grading details in the uploaded syllabus.” |
|**Cite Source** | Student clicks “View Source.” | System displays the original text segment and highlights matched lines. | If source file missing: “Original file not found.” |
|**History** | Student opens “Recent Questions.” | Shows last 10 queries and their answers. | If no history: “Start by asking your first question!” |

---

### User Interaction Flow

#### Professor Workflow
1. Login.
2. Upload syllabus file.  
3. System parses document and creates a private course knowledge base.  
4. Professor can replace or delete file at any time.

#### Student Workflow
1. Open course page and see chat-style interface.  
2. Type a question (ex. “What’s the policy for late labs?”).  
3. Receive instant AI answer with citation (ex. “Two late days allowed – see Section 2.3”).  
4. Click “View Source” to read the original paragraph.  
5. Previous queries appear in “History.”  

---

### Interface Mockups

1. **Home Screen**
* Two buttons: “Professor Login / Upload Files” and “Ask the AI.”
<img width="500" height="475" alt="image" src="https://github.com/user-attachments/assets/124a311c-d959-4279-82c6-bd800ded930d" />

2. **Chat Screen**
* Left: student messages; Right: AI responses with small gray citation box (ex. “Section 3.2”).
<img width="430" height="660" alt="image" src="https://github.com/user-attachments/assets/f7477330-8730-4872-afe9-b9c8cef1a785" />

3. **Upload Panel**
* Drag-and-drop area with progress bar and success/error indicator.
<img width="636" height="548" alt="image" src="https://github.com/user-attachments/assets/1f83dff8-f101-43b5-81ff-3a2d6cd1e624" />
<img width="638" height="633" alt="image" src="https://github.com/user-attachments/assets/9fdf3bf9-e2fe-49ba-b2cc-60a6c009aab4" />
<img width="629" height="618" alt="image" src="https://github.com/user-attachments/assets/d7ef3a0d-ccb9-4e83-9252-326004d3f4cf" />

4. **Source Viewer**
* Popup highlighting the exact syllabus text that generated the answer.  
<img width="420" height="200" alt="image" src="https://github.com/user-attachments/assets/cf9ea63f-cf2b-4d44-a6d5-1770f090d68c" />

---

### System Architecture
```
Frontend (React or JavaFX)
  ↓ REST API
Backend (Flask / Spring Boot)
  ↓
AI Engine (OpenAI / Hugging Face)
  ↓
Cache & Storage (SQLite / Local FS)
```

- **Client** — handles uploads and chat interface.  
- **Server** — manages file storage and query requests.  
- **AI Engine** — extracts answers and generates summaries with citations.  
- **Cache** — stores parsed course data for faster responses.  

---

### Testing Plan

| Test Level | Purpose | Example |
|-------------|----------|---------|
| **Unit Tests** | Ensure file upload, parsing, and answer modules work individually. | Upload PDF → returns success status code. |
| **Integration Tests** | Verify front-back communication. | Query flow: ask → AI → citation. |
| **E2E Tests** | Validate full user experience. | Simulate student session asking 5 questions. |
| **Mock AI** | Ensure deterministic outputs for testing. | Pre-defined syllabus QA pairs. |

---

### Design Rationale

- Interactive and intuitive (fulfills user input requirement).  
- Narrow scope yet meaningful (problem clearly solved).  
- Feasible within 4 weeks using free tools.  

---

### Goals
* Deliver a real, functional AI assistant that makes UBC course information instantly accessible.
* Create a scalable assistant for potential future integration with Canvas or an offline mode.
* By reducing redundant Q&A between students and professors, **AI Course Advisor** enhances efficiency and improves learning experience campus-wide.
