# Backend Testing Strategy & Coverage Philosophy

## **Coverage Target: 75%+ (With Exclusions)**

Our backend testing strategy targets **75%+ code coverage** on meaningful code. To achieve this without writing low-value tests, we explicitly **exclude** boilerplate code (DTOs, Models, Config) from our coverage metrics.

This approach prioritizes **behavior over metrics** while maintaining high professional standards for the code that actually contains logic.

## **Why We Exclude, Not Lower Standards**

### **1. The "Noise" Problem**
Including DTOs, POJOs, and configuration files in coverage reports artificially lowers the score. 
- A project with 100% test coverage on business logic but 0% on DTOs might show an overall 60% coverage.
- This obscures the fact that the **critical logic is well-tested**.

**Solution:** We configure JaCoCo to ignore:
- ❌ `com.ubc.aiadvisor.dto.*` (Data Transfer Objects)
- ❌ `com.ubc.aiadvisor.model.*` (Simple Entities/POJOs)
- ❌ `com.ubc.aiadvisor.config.*` (Framework Configuration)
- ❌ `*Application.java` (Spring Boot Entry Point)

This leaves us with a pure metric: **How well is our Logic tested?**

### **2. Testing What Matters**
With noise excluded, we demand high coverage on:
- ✅ **Business logic** - Core service methods, data transformations, validation rules
- ✅ **API contracts** - Controller endpoints, request/response handling, status codes
- ✅ **Security** - Authentication, authorization, token validation
- ✅ **Error scenarios** - Exception handling, edge cases, fallback behaviors

### **3. Quality Over Quantity**
Coverage percentage measures **lines executed**, not **bugs caught**. 

```java
// ❌ High coverage, low value
@Test
void testGetAnswer() {
    QueryResponse response = new QueryResponse("answer", null);
    response.getAnswer(); // No assertion - useless test
}

// ✅ High value test
@Test
void testProcessQueryHandlesAIFailureGracefully() {
    when(aiEngine.generateAnswer(any(), any())).thenThrow(new RuntimeException());
    QueryResponse response = service.processQuery("question", "user123", null);
    assertTrue(response.getAnswer().contains("error"));
}
```

### **4. Industry Standards & Martin Fowler**
According to the Google Testing Blog, code coverage of 60% is generally seen as “acceptable”, 75% as “commendable” and 90% as “exemplary.” Because our product is a 5-week MVP, we have decided to aim for **75%+** code coverage. We align with this by ensuring our **Services** and **Controllers** meet this bar, rather than letting the average be dragged down by boilerplate. Thus, **DTOs/Models** and **Config** have been excluded from our JaCoCo reports.

## **What We Test**

1. **All API Endpoints** - Success and failure scenarios
2. **Authentication** - JWT token validation, unauthorized access
3. **Business Logic** - AI query processing, file uploads, history management
4. **Error Handling** - Invalid inputs, service failures, fallback responses
5. **Data Access** - Repository operations with H2 in-memory database

## **What We Don't Test**

1. **Auto-generated code** - JPA getters/setters, Lombok annotations
2. **Spring Framework internals** - Dependency injection, bean wiring
3. **Configuration classes** - Simple bean definitions
4. **Third-party libraries** - Supabase SDK, Google Gemini API (we test our wrappers)

## **Testing Approach**

### **Unit Tests**
- Mock external dependencies (AI services, storage, databases)
- Fast execution (< 5 seconds for all tests)
- Isolated - no external services required

### **Integration Tests**
- H2 in-memory database for JPA testing
- MockMvc for controller integration
- Test complete request/response flows

### **What We Skip**
- End-to-end tests with real Supabase (too slow, requires credentials)
- Performance/load testing (separate test suite)
- UI testing (handled by frontend team)

## **Test Quality Metrics**

Beyond coverage percentage, we measure:
- ✅ All critical user flows tested
- ✅ All error scenarios have test cases
- ✅ No flaky tests (inconsistent pass/fail)
- ✅ Test execution time < 30 seconds
- ✅ Clear test names describing behavior

## **References**

- Google Testing Blog: [Code Coverage Best Practices](https://testing.googleblog.com/2020/08/code-coverage-best-practices.html)
- Stack Overflow Developer Survey: 60-80% is typical for professional projects

---

**Last Updated**: November 30, 2025  
**Tester**: Harpreet Singh
