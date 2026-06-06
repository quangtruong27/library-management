-- =========================
-- ADMIN = FULL PERMISSIONS
-- =========================
INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permission p
WHERE r.name = 'ADMIN';


-- =========================
-- STAFF = LIBRARY OPERATIONS
-- Không CRUD Role / Permission / Staff / User
-- =========================
INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permission p ON p.name IN (
                                         'STAFF_READ',

                                         'STUDENT_READ',
                                         'STUDENT_CREATE',
                                         'STUDENT_UPDATE',

                                         'BOOK_READ',
                                         'BOOK_CREATE',
                                         'BOOK_UPDATE',
                                         'BOOK_DELETE',

                                         'CATEGORY_READ',
                                         'CATEGORY_CREATE',
                                         'CATEGORY_UPDATE',
                                         'CATEGORY_DELETE',

                                         'AUTHOR_READ',
                                         'AUTHOR_CREATE',
                                         'AUTHOR_UPDATE',
                                         'AUTHOR_DELETE',

                                         'BORROW_READ',
                                         'BORROW_CREATE',
                                         'BORROW_UPDATE',

                                         'RESERVATION_READ',
                                         'RESERVATION_UPDATE',

                                         'FINE_READ',
                                         'FINE_CREATE',
                                         'FINE_UPDATE',

                                         'INCIDENT_READ',
                                         'INCIDENT_UPDATE',

                                         'REPORT_READ',

                                         'BOOK_LOCATION_READ',
                                         'BOOK_LOCATION_CREATE',
                                         'BOOK_LOCATION_UPDATE',
                                         'BOOK_LOCATION_DELETE'

    )
WHERE r.name = 'STAFF';


-- =========================
-- STUDENT = BASIC USER PERMISSIONS + ALL READ (EXCEPT USER/STAFF/ROLE)
-- =========================
INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permission p ON p.name IN (
    -- CÁC QUYỀN ĐỌC (READ)
                                         'BOOK_READ',
                                         'CATEGORY_READ',
                                         'AUTHOR_READ',
                                         'BOOK_LOCATION_READ',
                                         'BORROW_READ',
                                         'RESERVATION_READ',
                                         'FINE_READ',
                                         'REVIEW_READ',
                                         'INCIDENT_READ',

    -- CÁC QUYỀN GHI/SỬA CƠ BẢN CỦA SINH VIÊN
                                         'RESERVATION_CREATE',
                                         'RESERVATION_DELETE',
                                         'REVIEW_CREATE',
                                         'REVIEW_UPDATE',
                                         'INCIDENT_CREATE'
    )
WHERE r.name = 'STUDENT';
