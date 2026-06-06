

-- Staff 1
INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN('c2c6d1b2-1111-4444-8888-aaaaaaaaaaaa'),
           'Nguyen Van A',
           '123456',
           'a@gmail.com',
           1
       );

INSERT INTO staff_profile (
    id, employee_code, phone, gender, dob,
    position_id, address, avatar_url
)
VALUES (
           UUID_TO_BIN('c2c6d1b2-1111-4444-8888-aaaaaaaaaaaa'),
           'EMP001',
           '0901234567',
           'MALE',
           '1999-01-01',
           1,
           'Ha Noi',
           'https://example.com/avatar1.png'
       );


-- Staff 2
INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN('b1111111-2222-3333-4444-555555555555'),
           'Tran Thi B',
           '123456',
           'b@gmail.com',
           1
       );

INSERT INTO staff_profile (
    id, employee_code, phone, gender, dob,
    position_id, address, avatar_url
)
VALUES (
           UUID_TO_BIN('b1111111-2222-3333-4444-555555555555'),
           'EMP002',
           '0911111111',
           'FEMALE',
           '1998-05-10',
           1,
           'Ho Chi Minh',
           'https://example.com/avatar2.png'
       );


-- Staff 3
INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN('c2222222-3333-4444-5555-666666666666'),
           'Le Van C',
           '123456',
           'c@gmail.com',
           1
       );

INSERT INTO staff_profile (
    id, employee_code, phone, gender, dob,
    position_id, address, avatar_url
)
VALUES (
           UUID_TO_BIN('c2222222-3333-4444-5555-666666666666'),
           'EMP003',
           '0922222222',
           'MALE',
           '2000-12-20',
           2,
           'Da Nang',
           'https://example.com/avatar3.png'
       );