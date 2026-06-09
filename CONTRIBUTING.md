# Contributing to Library Management System (LMS)

Thank you for your interest in contributing to our project! We welcome community contributions to help improve this system. Please follow the guidelines below to ensure a smooth collaboration process.

---

## 🌿 Git Workflow Strategy
We follow the **Git Flow** strategy for branching and releases:

1.  **Fork** the repository and clone it locally.
2.  Create your feature branch off the `develop` branch:
    ```bash
    git checkout develop
    git checkout -b feature/your-feature-name
    ```
3.  Implement your changes, adhering to the project's coding standards.
4.  Write comprehensive tests (JUnit/Mockito) for any new business logic.
5.  Commit your changes using semantic commit messages:
    *   `feat: add payment integration module`
    *   `fix: resolve N+1 query in book search endpoint`
    *   `docs: update API documentation references`
    *   `test: add unit tests for reservation validation`
6.  Push your branch to your remote repository:
    ```bash
    git push origin feature/your-feature-name
    ```
7.  Open a **Pull Request** pointing to the `develop` branch of the main repository.

---

## 🛠️ Development & Coding Standards

### 1. Code Style
*   Ensure your IDE uses standard Java formatting rules.
*   Maximize the use of **Lombok** (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`) to reduce boilerplate code.
*   Use **MapStruct** for DTO-Entity conversions instead of manual mapping methods.
*   Follow clean coding principles (e.g., SOLID, meaningful names, small functions).

### 2. Database Migrations
*   Any database schema changes must be written as a **Flyway migration file** under `src/main/resources/db/migration/`.
*   Ensure filenames follow the pattern `V1.0.X__description.sql`.
*   Ensure the SQL script is compatible with MySQL 8.0.

### 3. API Design Guidelines
*   Keep Controller endpoints focused only on validation and request handling. Move all business logic to Services.
*   Wrap all controller outputs in the standard `ApiResponse` generic wrapper.
*   Use annotations like `@Valid` to enforce input validation constraints.
*   Secure endpoints using Spring Security annotations:
    ```java
    @PreAuthorize("hasAuthority('BOOK_CREATE')")
    ```

### 4. Testing Requirements
*   Every new service method must have corresponding unit tests using Mockito.
*   Ensure that all existing tests pass before creating a pull request:
    ```bash
    ./gradlew test
    ```

---

## 🧪 Submission Checklist
Before submitting your pull request, please double-check that you have:
- [ ] Checked that all unit tests run successfully.
- [ ] Written clean code and removed unnecessary logs or comments.
- [ ] Added a Flyway migration script if there are schema changes.
- [ ] Verified endpoints manually or via integration testing.
- [ ] Filled out the Pull Request template comprehensively.
