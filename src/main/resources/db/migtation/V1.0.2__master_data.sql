-- =========================
-- USER STATUS
-- =========================
INSERT INTO user_status(id, name)
VALUES
    (1, 'ACTIVE'),
    (2, 'BLOCKED'),
    (3, 'DELETED');


-- =========================
-- STAFF POSITION
-- =========================
INSERT INTO staff_position(id, name)
VALUES
    (1, 'LIBRARIAN'),
    (2, 'MANAGER'),
    (3, 'ADMIN');


-- =========================
-- ROLE
-- =========================
INSERT INTO role(id, name, description)
VALUES
    (1, 'ADMIN', 'System administrator'),
    (2, 'STAFF', 'Library staff'),
    (3, 'STUDENT', 'Student user');


-- =========================
-- PERMISSION
-- =========================
INSERT INTO permission(id, name, description)
VALUES
    (1, 'ROLE_READ', 'View role'),
    (2, 'ROLE_CREATE', 'Create role'),
    (3, 'ROLE_UPDATE', 'Update role'),
    (4, 'ROLE_DELETE', 'Delete role'),

    (5, 'STAFF_READ', 'View staff'),
    (6, 'STAFF_CREATE', 'Create staff'),
    (7, 'STAFF_UPDATE', 'Update staff'),
    (8, 'STAFF_DELETE', 'Delete staff'),

    (9, 'STUDENT_READ', 'View student'),
    (10, 'STUDENT_CREATE', 'Create student'),
    (11, 'STUDENT_UPDATE', 'Update student'),
    (12, 'STUDENT_DELETE', 'Delete student'),

    (13, 'BOOK_READ', 'View book'),
    (14, 'BOOK_CREATE', 'Create book'),
    (15, 'BOOK_UPDATE', 'Update book'),
    (16, 'BOOK_DELETE', 'Delete book'),

    (17, 'CATEGORY_READ', 'View category'),
    (18, 'CATEGORY_CREATE', 'Create category'),
    (19, 'CATEGORY_UPDATE', 'Update category'),
    (20, 'CATEGORY_DELETE', 'Delete category'),

    (21, 'AUTHOR_READ', 'View author'),
    (22, 'AUTHOR_CREATE', 'Create author'),
    (23, 'AUTHOR_UPDATE', 'Update author'),
    (24, 'AUTHOR_DELETE', 'Delete author'),

    (25, 'BORROW_READ', 'View borrow'),
    (26, 'BORROW_CREATE', 'Create borrow'),
    (27, 'BORROW_UPDATE', 'Update borrow'),
    (28, 'BORROW_DELETE', 'Delete borrow'),

    (29, 'RESERVATION_READ', 'View reservation'),
    (30, 'RESERVATION_CREATE', 'Create reservation'),
    (31, 'RESERVATION_UPDATE', 'Update reservation'),
    (32, 'RESERVATION_DELETE', 'Delete reservation'),

    (33, 'FINE_READ', 'View fine'),
    (34, 'FINE_CREATE', 'Create fine'),
    (35, 'FINE_UPDATE', 'Update fine'),
    (36, 'FINE_DELETE', 'Delete fine'),

    (37, 'REVIEW_READ', 'View review'),
    (38, 'REVIEW_CREATE', 'Create review'),
    (39, 'REVIEW_UPDATE', 'Update review'),
    (40, 'REVIEW_DELETE', 'Delete review'),

    (41, 'INCIDENT_READ', 'View incident'),
    (42, 'INCIDENT_CREATE', 'Create incident'),
    (43, 'INCIDENT_UPDATE', 'Update incident'),
    (44, 'INCIDENT_DELETE', 'Delete incident'),

    (45, 'REPORT_READ', 'View reports'),

    (46, 'BOOK_LOCATION_READ', 'View book location'),
    (47, 'BOOK_LOCATION_CREATE', 'Create book location'),
    (48, 'BOOK_LOCATION_UPDATE', 'Update book location'),
    (49, 'BOOK_LOCATION_DELETE', 'Delete book location');
