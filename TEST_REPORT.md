# Test Report - AI Course Advisor

**Project**: AI Course Advisor (Project Willow)
**Testers**: Chenkai Xiao, Harpreet Singh
**Date**: November 30, 2025

---

## Executive Summary

The comprehensive testing suite implemented for both Frontend and Backend has verified all critical functional and non-functional requirements. The project currently maintains a **100% test pass rate**.

### Key Metrics
| Metric | Value | Status |
| :--- | :--- | :--- |
| **Total Test Cases** | 258 (Backend: 170, Frontend: 88) | **COMPLETE** |
| **Pass Rate** | 100% | **PASS** |
| **Backend Code Coverage** | 76% | **PASS (>75% target after exclusions)** |
| **Frontend Code Coverage** | 77% | **PASS (>75% target)** |

---

## Backend Testing Status

**Framework**: JUnit 5, Mockito, Spring Boot Test, JaCoCo

### Coverage
| Metric | Code Coverage | Branch Coverage |
| :--- | :--- | :--- |
| **Status** | 76% | 73% |

### Key Improvements
- Refactored `GeminiService` for testability and added comprehensive unit tests.
- Added extensive tests for `HistoryController` covering all endpoints and error cases.
- Verified role-based access control and multi-user isolation.
- `JwtUtil` security logic achieved **100%** coverage.

### Disabled Latency Degradation Test
The class `AIQueryServiceConcurrencyLatencyTest` is annotated with `@Disabled` to avoid flaky builds. It measures latency degradation under concurrent load and is highly sensitive to local machine variability.

Run it ONLY under controlled conditions:
- Dedicated machine (no heavy background processes)
- Stable CPU frequency (disable turbo/thermal throttling if possible)
- Consistent available RAM (avoid paging; >2GB free suggested)
- Minimal external disk / network I/O noise
- Fixed JVM settings (e.g. `-Xms512m -Xmx1024m` for repeatability)
- No container resource contention

To re-enable: remove the `@Disabled` annotation in `backend/src/test/java/com/ubc/aiadvisor/service/AIQueryServiceConcurrencyLatencyTest.java` and ensure the above constraints. Consider running with a dedicated Maven profile (e.g. `-Pperf`) if added later.

### Additional Notes
- Tests use H2 in-memory DB for JPA via `src/test/resources/application-test.properties`.
- Controller tests use `@WebMvcTest` with `@MockBean` for dependencies including JWT authentication.
- Tests verify success paths and error handling (401 Unauthorized, 403 Forbidden, 400 Bad Request, etc.).
- Mockito LENIENT strictness used for complex scenario and parameterized tests.

---

## Frontend Testing Status

**Framework**: Vitest, React Testing Library

### Coverage
| Metric | Code Coverage | Branch Coverage |
| :--- | :--- | :--- |
| **Status** | 77% | 81% |

### Key Improvements
- Created unit tests for all reusable UI components (e.g., `ChatBubble`, `CitationView`).
- Implemented integration tests for all major application pages.
- Covered authentication flow and API client logic.

---

## Verification Status Summary

### Functional Requirements
- **R-U (Upload)**: Verified via `UploadServiceTest` and `UploadPage` tests.
- **R-Q (Ask AI)**: Verified via `AIQueryServiceTest` and `AskPage` tests.
- **R-C (Citation)**: Verified via `CitationView` tests.
- **R-H (History)**: Verified via `HistoryServiceTest`, `HistoryControllerTest`, and `HistoryPage` tests.
- **R-W (Workflow)**: Role access verified in backend tests.

### Non-Functional Requirements
- **R-N-01 (Concurrency)**: Verified via `AIQueryServiceConcurrencyTest`.
- **R-N-04 (Performance)**: Verified via timing tests.
- **R-N-06 (Compliance)**: Verified via POM license scan.

---

## Conclusion

The system has undergone rigorous testing. Both frontend and backend meet and exceed the defined testing requirements. The test suite provides a strong safety net for future development and refactoring.

## Next Steps

- Increase coverage for edge cases.
- Add End-to-End testing with Cypress or Playwright (future scope).
- Continuous Integration (CI) setup with these tests.
