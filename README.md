# AI Course Advisor

## Team Mission
* *Optimizing the efficiency of education for students and professors.*

## Team Name
* UBC Tutoring Agents

## Team Logo
![Team Logo](./images/team-logo.png)

## Project Overview
AI Course Advisor helps students quickly find answers to their course-related questions by analyzing uploaded course documents. Simply upload your syllabus, and ask questions in natural language. Our AI will extract precise answers and provide citations to the original source material.

---

## Architecture
- **Backend:** Spring Boot (Java 17)
- **Frontend:** React (Vite + TypeScript)
- **Database:** PostgreSQL (via Supabase)
- **AI Engine:** Google Gemini 2.5 Pro (via REST API)
- **Authentication:** Supabase Auth

# B - Product Guide and Example using

---
# Product demo link: [https://www.youtube.com/watch?v=87_czrMv3BQ&feature=youtu.be]

# 1. Sign Up and Login
Once you have opened: [http://localhost:3000](http://localhost:3000) in step 5, you should see our interface:
<img width="800" alt="image" src="https://github.com/user-attachments/assets/b05ba1c4-0ede-4fb4-8125-bb2399a5f7b1" />

1. At the top right corner, click **Sign In**.
2. If you don't have an account, click **Sign Up**.
3. Once you have entered your email and password, you should receive a verification email such as:

     <img width="320" alt="image" src="https://github.com/user-attachments/assets/458f4aae-6459-4a6d-aa64-94ff54ba2e5a" />

4. Then click **Confirm your mail**. It will bring you to our website, and your status will be logged in.

---

# 2. Upload Syllabus

Scroll to the bottom of the home page and click **Upload Syllabus**.

<img width="777" alt="image" src="https://github.com/user-attachments/assets/4d240a00-52e7-46d7-bbc8-fa1ba291124b" />

1. Click **Choose File**.
2. Select your syllabus file (PDF, DOCX, or TXT).
3. After the upload is successful, you may:
   - Upload another file, or  
   - Ask questions about the uploaded syllabus by clicking **Ask** in the top-right menu.

Once uploaded, you will be able to ask questions related to this syllabus.

---

# 3. Asking Questions

To begin asking questions:

1. Go to the **Upload** section.
2. Select the syllabus you want to ask about.
3. Start chatting with the assistant.

**Example (MATH220: Mathematical Proof):**

<img width="777" alt="image" src="https://github.com/user-attachments/assets/8dcf4086-eaf9-41d8-b6aa-768a786b077d" />

The selected course name will appear at the top of the chat box.

---

## Viewing Answer Sources

Every AI response includes a list of sources.  
To view the reference materials used to generate the answer:

- Click the source labels at the bottom of the reply.

<img width="777" alt="image" src="https://github.com/user-attachments/assets/c604e21c-73e0-48bb-ae54-f2bbd4e6d5d0" />

---

## Viewing Chat History

To see previous conversations:

- Open the **History** section located in the top-right corner of the page.


## Environment Setup (Prerequisites & Configuration)

**You do NOT need to install Java, Node.js, npm, Maven, or anything else. Docker handles everything.**

### Prerequisites
- Docker & Docker Compose
- Supabase Account (for Database & Auth)
- Google Cloud Account (for Gemini API Key)

### 1. Install Docker Desktop

Download and install Docker Desktop for Windows or macOS: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

### 2. Verify Docker Installation

Open PowerShell or Terminal and ensure Docker is running by executing:

```bash
docker version
docker compose version
```

### 3. Allow PowerShell to Run Scripts (Windows Only)

In PowerShell, run the following command and press **Y** to confirm:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

### 4. Create and Configure the `.env` File

Create a `.env` file in the **project root directory** (the same folder as `docker-compose.yml`) and add **all** the required secrets (see also the `.env.example` given in the repo):

```properties
# --- Backend Configuration ---
# Supabase Database Connection (Use Connection Pooling URL for IPv4 support)
SUPABASE_DB_URL=jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:5432/postgres
SUPABASE_DB_USER=postgres.[YOUR-PROJECT-REF]
SUPABASE_DB_PASSWORD=[YOUR-DB-PASSWORD]
SUPABASE_JWT_SECRET=[YOUR-SUPABASE-JWT-SECRET]

# --- Frontend Configuration ---
# These are needed at build time for the frontend
VITE_SUPABASE_URL=[YOUR-SUPABASE-PROJECT-URL]
VITE_SUPABASE_ANON_KEY=[YOUR-SUPABASE-ANON-KEY]

# Gemini API
GEMINI_API_KEY=[YOUR-GEMINI-API-KEY]

# Optional: Specify model version (default: gemini-2.5-flash)
GEMINI_MODEL=[YOUR_MODEL_VERSION]
```

Should it be necessary, the specific values of these `.env` variables used by our team can be shared privately.

**UPDATE: The specific values of these `.env` variables used by our team are pasted below in order to facilitate the evaluation process. They will be removed later on.**

```properties
# --- Backend Configuration ---
# Supabase Database Connection
SUPABASE_DB_URL=jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:5432/postgres
SUPABASE_DB_USER=postgres.lmfnpreuxnexksmzzmnl
SUPABASE_DB_PASSWORD=CPEN221Project
SUPABASE_JWT_SECRET=GP8kzyvsAev3hOj1/dFahoErmaTMZCsvUNLCEiucEqMA+kKZtqES6fINesyJZ5lqK68A8CGZxgQsbdUckjyxfQ==
SUPABASE_STORAGE_BUCKET="course-syllabi"
SUPABASE_SERVICE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtZm5wcmV1eG5leGtzbXp6bW5sIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2NDE5NjMyMCwiZXhwIjoyMDc5NzcyMzIwfQ.tF9G8nW0CaI-6uSCXcU5qFraOW14K87ybDmzGgjWhw0
# Gemini API
GEMINI_API_KEY=AIzaSyDevZmU_7-PiQrwQ5HksedmuNQv0MMgan0

# --- Frontend Configuration ---
# These are needed at build time for the frontend
VITE_SUPABASE_URL=https://lmfnpreuxnexksmzzmnl.supabase.co
VITE_SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtZm5wcmV1eG5leGtzbXp6bW5sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQxOTYzMjAsImV4cCI6MjA3OTc3MjMyMH0.rJ9jpHF_D4Had6SNlxI9nuW2DPD36UCvMggxjysWPX8
```

-----

## Running the Application

### 1. Pre-Build Docker Images

Navigate to the project root directory and run the build command once to speed up subsequent starts:

```bash
docker compose build
```

### 2. Start the Services

Start both the backend and frontend services:

```bash
docker compose up
```

To run in the background: `docker compose up -d`

### 3. Access the Application

1.  **Frontend Website:** Open your browser and visit: **http://localhost:3000**
2.  **Backend Health Check:** Visit: **http://localhost:8080/health** (Expected output: `{"status": "UP"}`)

### 4. Stop All Services

To stop and remove all containers:

```bash
docker compose down
```

## Features
- **Authentication:** Sign up and log in securely.
- **PDF Upload:** Upload course syllabi (stored in your private history).
- **Contextual Q&A:** Select a document and ask questions. The AI answers based *only* on the content of the single, selected document.
- **Citations:** Answers include direct quotes, page numbers, and location (Top/Middle/Bottom).
- **History:** View past questions and answers with persistent citations.
