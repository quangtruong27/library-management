INSERT INTO user_role(role_id, user_id)
SELECT r.id, u.id
FROM role r, user u
WHERE r.name = 'ADMIN'
  AND u.name = 'admin';

INSERT INTO user_role(role_id, user_id)
SELECT r.id, u.id
FROM role r, user u
WHERE r.name = 'STUDENT'
  AND u.name = 'student';

INSERT INTO user_role(role_id, user_id)
SELECT r.id, u.id
FROM role r, user u
WHERE r.name = 'STAFF'
  AND u.name = 'staff';