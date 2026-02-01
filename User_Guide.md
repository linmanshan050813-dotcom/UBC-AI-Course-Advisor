Guid video: https://youtu.be/87_czrMv3BQ
# A - Installation & Setup:

---

# 1. Install Docker Desktop

Project Willow uses Docker to run all backend and frontend services.
**You do NOT need to install Java, Node.js, npm, Maven, or anything else.**
Docker handles everything.

### Download Docker Desktop

Windows / macOS download page: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

### After installation

1. Open **Docker**
2. Make sure the bottom-left corner shows **Engine Running**:

   <img width="235" alt="image" src="https://github.com/user-attachments/assets/45c33cd1-e6f7-4107-bc45-e252b51fb1e5" />



(If Docker is not running, the project will not start.)

---

# 2. First-Time Environment Setup (Do This Only Once)

These steps prepare your machine to run Project Willow smoothly.

---

## (1) Verify Docker Installation

Open **PowerShell as Administrator** (Windows) or **Terminal** (macOS/Linux) and type:

```bash
docker version
docker compose version
```

If both commands return version numbers, your installation is good.

If you see errors → Docker Desktop is not running.

---

## (2) Allow PowerShell to Run Scripts (Windows Only)

This is required for the project's verification script.

In **PowerShell**, run:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

Press **Y** to confirm.

If you’re on macOS/Linux → skip this step.

---

## (3) Pre-Build Docker Images

Building the Docker images ahead of time makes the startup much faster.

### Step 1 — Navigate to the project folder

Replace the path with the directory where this project is located.
Example (Windows):

```bash
cd "C:\Users\hp\Desktop\cpen 221\project-willow-main"
```

### Step 2 — Build the Docker images

```bash
docker compose build
```

This may take several minutes the first time — that’s normal.

---

## (4) Create and Configure the `.env` File

The project requires a `.env` file inside the **project root directory**
(the same folder as `docker-compose.yml`).

### Step 1 — If `.env` does not exist, create it:

**Windows:**

```powershell
New-Item -Name ".env" -ItemType "file"
```

**macOS / Linux:**

```bash
touch .env
```

### Step 2 — Add the required environment variables

Paste the following into your `.env` file:

```
SUPABASE_DB_URL=YOUR_DB_URL
SUPABASE_DB_USER=YOUR_DB_USER
SUPABASE_DB_PASSWORD=YOUR_DB_PASSWORD
SUPABASE_JWT_SECRET=YOUR_JWT_SECRET
SUPABASE_STORAGE_BUCKET=YOUR_STORAGE_BUCKET
SUPABASE_SERVICE_KEY=YOUR_SERVICE_KEY

VITE_SUPABASE_URL=YOUR_SUPABASE_URL
VITE_SUPABASE_ANON_KEY=YOUR_SUPABASE_ANON_KEY

GEMINI_API_KEY=YOUR_API_KEY
```

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

---

# 3. Navigate to the Project Root
Before starting the services, ensure your directory contains:

```
backend/
frontend/
docker-compose.yml
verify-docker.ps1
```

Navigate there:

```bash
cd "path/to/project-willow-main"
```

---

# 4. Start the Full Application (Backend + Frontend)

Start all containers:

```bash
docker compose up
```

If successful, you will see:

```
✓ ai-advisor-backend     Running
✓ ai-advisor-frontend    Running
```

### Run in background mode:

```bash
docker compose up -d
```

This allows you to close the terminal while the app keeps running.

---

# 5. Access the Frontend Website

Open your browser and visit: [http://localhost:3000](http://localhost:3000)

You should now see the **Web Interface**.

If the page doesn’t load, check:

* Did Docker Desktop start successfully?
* Did you run `docker compose up` from the project root?

---

# 6. Check the Backend Health Endpoint

Visit: [http://localhost:8080/health](http://localhost:8080/health)

Expected output:

```json
{"phase":4,"status":"ok"}
```

This confirms the Spring Boot backend is running correctly.

---

# 7. Stop All Services

To stop everything:

```bash
docker compose down
```

This fully shuts down the containers and releases system resources.

---

# B - Product Guide

---

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
