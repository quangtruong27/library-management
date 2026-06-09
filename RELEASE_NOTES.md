# Release Notes Template (v1.0.0)

We are excited to announce the release of the Library Management System (LMS) Backend! This release introduces a comprehensive, robust, and secure Spring Boot framework to drive library automation.

## Key Highlights
- **Java 21 & Spring Boot 4.0.5 Support:** Developed with modern language features (such as pattern matching, records, and virtual thread compatibility).
- **Stateless OAuth2 JWT Security:** Clean token introspection and signature verification with strict Method-level Security.
- **Relational Integrity via Flyway:** Seamless DB schema updates and seed data configuration across development environments.
- **Dedicated Student Hub (`/api/me`):** Centralized resource endpoint returning active borrowings, reservations, and fine balances.

---

## Detailed Changes

### Authentication & Profiles
- Implemented `/api/auth/login` and `/api/auth/introspect`.
- Generated isolated `student_profile` and `staff_profile` mappings to distinguish student accounts from staff accounts.

### Catalog & Inventory
- Added cataloging support for authors, categories, publishers, books, and specific physical book copies.
- Integrated physical shelving locations (`book_location`) to coordinate inventory tracking.

### Book Circulation
- Developed transactional logic for borrowing details, returns, and reservations.
- Configured automated fine issuance for late returns.

### Incidents & Reviews
- Developed incident reports allowing users to flag book damage/losses.
- Integrated book reviews with unique constraint checking to ensure students can rate books once.

---

## Deployment Artifacts
*   Docker image: `yourregistry/lms-backend:1.0.0`
*   Database Schema Version: `V1.0.13`

## Contributors
*   Quang Truong (@quangtruong27) - Lead Backend Engineer
