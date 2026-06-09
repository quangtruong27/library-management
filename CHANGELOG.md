# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-06-09
### Added
- Created foundational project structure utilizing Java 21 and Spring Boot 4.0.5.
- Configured Spring Security with stateless JWT validation (using HMAC-SHA256 signer key).
- Created a robust Role-Based Access Control (RBAC) database schema mapping users, roles, and permissions.
- Integrated Flyway to manage database migrations, including initial database schemas, master static lists, and sample mock data.
- Built Core Modules: User authentication, student profiles, books, book locations, and book copies.
- Implemented Circulation Logic: Book reservation workflows, book borrowing, returns, fine generations, and incident reports.
- Added `/api/me` secure endpoint suite for personal profile metrics and statistics.
- Added comprehensive unit testing suite using JUnit 5 and Mockito.
- Added Dockerfile and deployment guides.
- Published professional project assets (Readme, PR templates, Issue templates, Contributing guides, Changelog, and Release Notes templates).

### Changed
- Configured application to validate database schemas via Spring Data JPA validator.
- Replaced manual mappings with MapStruct converters to improve performance.

### Fixed
- Resolved potential N+1 query issue on book catalogs by using selective lazy loading fetch mappings.
