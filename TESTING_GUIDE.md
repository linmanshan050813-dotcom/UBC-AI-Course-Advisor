# Testing Guide - AI Course Advisor

This guide outlines how to run tests for both Frontend and Backend components of the AI Course Advisor project.

---

## Frontend Testing

### Prerequisites
- Node.js installed
- Dependencies installed (`npm install` in `frontend/` directory)

### Quick Start
```bash
cd frontend
npm test
```

### Running Tests

**Run all tests:**
```bash
npm test
```

**Run specific test file:**
```bash
npm test src/app/login/page.test.tsx
```

**Run with Coverage:**
```bash
npm run test:coverage
```

**Watch Mode (Default):**
`npm test` runs in watch mode by default. Use `q` to quit.

### Test Structure
- **Components**: `frontend/src/__tests__/components/*.test.tsx`
- **Pages**: `frontend/src/__tests__/app/*/page.test.tsx`
- **Logic/Context**: `frontend/src/__tests__/lib/*.test.ts`, `frontend/src/__tests__/context/*.test.tsx`

### Coverage
Current code coverage is 77% and branch coverage is 81%. Key components and pages are covered.

---

## Backend Testing

### Prerequisites
- Java 17+ installed
- Maven installed

### Quick Start
```bash
cd backend
mvn test
```

### Running Tests

**Run all tests:**
```bash
mvn test
```

**Run verify (includes integration tests & coverage report):**
```bash
mvn verify
```

**Run specific test class:**
```bash
mvn test -Dtest=HistoryControllerTest
```

### Coverage Report
After running `mvn verify` or `mvn jacoco:report`, open:
`backend/target/site/jacoco/index.html`

### Test Structure
- **Controllers**: `backend/src/test/java/com/ubc/aiadvisor/controller/`
- **Services**: `backend/src/test/java/com/ubc/aiadvisor/service/`
- **Repositories**: `backend/src/test/java/com/ubc/aiadvisor/repository/`

### Coverage
Current code coverage is 76% and branch coverage is 73%.
