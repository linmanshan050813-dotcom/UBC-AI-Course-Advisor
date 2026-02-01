# Description of All Components

## UploadModel
- Handles syllabus uploads and stores metadata (file name, course ID, timestamp).  
- Resides on the **server**.  
- Communicates only with the **UploadAPI**.  
  - The **UploadAPI** can ask the **UploadModel** to store or replace a syllabus file.  
  - The **UploadAPI** can ask the **UploadModel** for upload status (success, failure, or invalid type).  

---

## FileParserModel
- Extracts and cleans text from uploaded PDF, DOCX, or TXT files.  
- Resides on the **server**.  
- Communicates only with the **UploadModel**.  
  - The **UploadModel** calls the **FileParserModel** to parse files and return clean text.  
  - Reports parsing errors or unsupported formats.  

---

## IndexModel
- Stores searchable text indexes created from parsed syllabi.  
- Resides on the **server**.  
- Communicates with:  
  - **UploadModel** to save parsed text.  
  - **AIQueryModel** to retrieve relevant text segments for queries.  

---

## AIQueryModel
- Manages query processing logic and interacts with the AI engine.  
- Resides on the **server**.  
- Communicates with:  
  - **IndexModel** to search syllabus content.  
  - **AIEngine** to generate summarized answers and citations.  
  - **AIQueryAPI** to return final responses to the client.  

---

## AIEngine
- Generates summarized answers using pretrained AI models.  
- Resides on the **server**.  
- Communicates only with **AIQueryModel**.  
  - The **AIQueryModel** sends a query context and receives summarized results with citations.  

---

## CitationModel
- Stores and retrieves syllabus text segments used for citations.  
- Resides on the **server**.  
- Communicates only with the **CitationAPI**.  
  - The **CitationAPI** can ask the **CitationModel** to fetch or verify source paragraphs.  

---

## HistoryModel
- Stores the 10 most recent student queries and AI responses per course.  
- Resides on both **client** (for cache) and **server** (for persistence).  
- Communicates with the **HistoryAPI**.  
  - The **HistoryAPI** can save or retrieve query–response logs.  

---

## AuthModel
- Manages login credentials and user roles (professor or student).  
- Resides on the **server**.  
- Communicates only with the **AuthAPI**.  
  - The **AuthAPI** verifies login credentials and returns user roles.  

---

## UploadAPI
- Provides access to syllabus upload and validation.  
- Resides on the **server**.  
- Communicates with:  
  - **UploadModel**, **FileParserModel**, and **IndexModel** for processing and storage.  
- Used by the **UploadView** on the client to send upload requests.  

---

## AIQueryAPI
- Handles student questions and returns summarized answers.  
- Resides on the **server**.  
- Communicates with:  
  - **AIQueryModel** for AI responses.  
  - **IndexModel** for relevant syllabus segments.  
  - **HistoryModel** for query history.  
- Used by **ChatView** on the client.  

---

## CitationAPI
- Returns original syllabus text corresponding to a citation ID.  
- Resides on the **server**.  
- Communicates with **CitationModel**.  
- Used by **CitationView** on the client.  

---

## HistoryAPI
- Manages student query and response history.  
- Resides on the **server**.  
- Communicates with **HistoryModel**.  
- Used by **HistoryView** on the client.  

---

## AuthAPI
- Manages login and authentication.  
- Resides on the **server**.  
- Communicates with **AuthModel** for verification.  
- Used by the client’s login interface.  

---

## Views
### UploadView
- Allows professors to select and upload syllabus files.  
- Sends file data to **UploadAPI**.  
- Displays upload progress and status.  

### ChatView
- Displays student and AI messages in a chat layout.  
- Sends queries to **AIQueryAPI** and shows returned responses and citations.  

### CitationView
- Shows the syllabus section that generated the AI answer.  
- Retrieves text from **CitationAPI**.  

### HistoryView
- Displays the last 10 questions and answers per course.  
- Retrieves data from **HistoryAPI**.  

All views reside on the **client** and communicate with the server through **APIs**.
