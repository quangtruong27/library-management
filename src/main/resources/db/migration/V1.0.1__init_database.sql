CREATE TABLE author
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    name      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_author PRIMARY KEY (id)
);

CREATE TABLE book
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description`  TEXT         NULL,
    price          DECIMAL      NULL,
    publisher_year INT          NULL,
    category_id    BIGINT       NULL,
    publisher_id   BIGINT       NULL,
    CONSTRAINT pk_book PRIMARY KEY (id)
);

CREATE TABLE book_author
(
    author_id BIGINT NOT NULL,
    book_id   BIGINT NOT NULL,
    CONSTRAINT pk_book_author PRIMARY KEY (author_id, book_id)
);

CREATE TABLE book_copy
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    qr_code         VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NULL,
    book_id         BIGINT       NULL,
    booklocation_id BIGINT       NULL,
    CONSTRAINT pk_bookcopy PRIMARY KEY (id)
);

CREATE TABLE book_location
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_booklocation PRIMARY KEY (id)
);

CREATE TABLE borrow
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    borrow_date        datetime     NULL,
    due_date           datetime     NULL,
    status             VARCHAR(255) NULL,
    student_profile_id BINARY(16)   NULL,
    staff_profile_id   BINARY(16)   NULL,
    CONSTRAINT pk_borrow PRIMARY KEY (id)
);

CREATE TABLE borrow_detail
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    return_date  datetime     NULL,
    note         VARCHAR(255) NULL,
    borrow_id    BIGINT       NULL,
    book_copy_id BIGINT       NULL,
    CONSTRAINT pk_borrowdetail PRIMARY KEY (id)
);

CREATE TABLE category
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE clazz
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255) NOT NULL,
    course_year VARCHAR(255) NULL,
    faculty_id  BIGINT       NULL,
    CONSTRAINT pk_clazz PRIMARY KEY (id)
);

CREATE TABLE faculty
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_faculty PRIMARY KEY (id)
);

CREATE TABLE fine
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    fine_type          VARCHAR(255) NULL,
    reference_price    DECIMAL      NULL,
    status             VARCHAR(255) NULL,
    student_profile_id BINARY(16)   NULL,
    borrow_detail_id   BIGINT       NULL,
    CONSTRAINT pk_fine PRIMARY KEY (id)
);

CREATE TABLE notification
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    title   VARCHAR(255) NOT NULL,
    content TEXT         NULL,
    status  VARCHAR(255) NULL,
    user_id BINARY(16)   NULL,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

CREATE TABLE payment
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    payment_method     VARCHAR(255) NULL,
    student_profile_id BINARY(16)   NULL,
    staff_profile_id   BINARY(16)   NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

CREATE TABLE payment_detail
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    amount_allocated DECIMAL    NULL,
    payment_id       BIGINT       NULL,
    fine_id          BIGINT       NULL,
    CONSTRAINT pk_paymentdetail PRIMARY KEY (id)
);

CREATE TABLE permission
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_permission PRIMARY KEY (id)
);

CREATE TABLE publisher
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    name    VARCHAR(255) NULL,
    email   VARCHAR(255) NULL,
    contact VARCHAR(255) NULL,
    CONSTRAINT pk_publisher PRIMARY KEY (id)
);

CREATE TABLE reservation
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    reservation_date   datetime     NULL,
    expired_date       datetime     NULL,
    status             VARCHAR(255) NULL,
    student_profile_id BINARY(16)   NULL,
    book_copy_id       BIGINT       NULL,
    CONSTRAINT pk_reservation PRIMARY KEY (id)
);

CREATE TABLE `role`
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE role_permission
(
    permission_id BIGINT NOT NULL,
    role_id       BIGINT NOT NULL,
    CONSTRAINT pk_role_permission PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE staff_profile
(
    id       BINARY(16)   NOT NULL,
    employee_code VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) NULL,
    gender        VARCHAR(255) NULL,
    dob           date         NULL,
    position_id   BIGINT       NULL,
    address       VARCHAR(255) NULL,
    avatar_url    VARCHAR(255) NULL,
    CONSTRAINT pk_staffprofile PRIMARY KEY (id)
);

CREATE TABLE staff_position
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_staffposition PRIMARY KEY (id)
);

ALTER TABLE staff_position
    ADD CONSTRAINT uc_staffposition_name UNIQUE (name);

CREATE TABLE student_profile
(
    id         BINARY(16)   NOT NULL,
    student_code    VARCHAR(255) NOT NULL,
    phone           VARCHAR(255) NULL,
    gender          VARCHAR(255) NULL,
    dob             date         NULL,
    address         VARCHAR(255) NULL,
    enrollment_year INT          NULL,
    avatar_url      VARCHAR(255) NULL,
    clazz_id        BIGINT       NULL,
    CONSTRAINT pk_studentprofile PRIMARY KEY (id)
);

CREATE TABLE user
(
    id             BINARY(16)   NOT NULL,
    name           VARCHAR(255) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    status_id      BIGINT       NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_status
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_userstatus PRIMARY KEY (id)
);

ALTER TABLE user_status
    ADD CONSTRAINT uc_userstatus_name UNIQUE (name);

CREATE TABLE user_role
(
    role_id BIGINT NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (role_id, user_id)
);

CREATE TABLE review
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    rating             INT          NOT NULL,   -- Điểm đánh giá: 1 → 5 sao
    comment            TEXT         NULL,       -- Nội dung nhận xét
    created_at         DATETIME     NULL,
    updated_at         DATETIME     NULL,
    book_id            BIGINT       NULL,
    student_profile_id BINARY(16)   NULL,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_BOOK
        FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_STUDENT
        FOREIGN KEY (student_profile_id) REFERENCES student_profile (id);

-- Mỗi sinh viên chỉ được đánh giá 1 lần cho mỗi cuốn sách
ALTER TABLE review
    ADD CONSTRAINT uc_review_book_student UNIQUE (book_id, student_profile_id);

ALTER TABLE staff_profile
    ADD CONSTRAINT FK_STAFFPROFILE_ON_POSITION
        FOREIGN KEY (position_id) REFERENCES staff_position (id);

ALTER TABLE user
    ADD CONSTRAINT FK_USER_ON_STATUS
        FOREIGN KEY (status_id)
            REFERENCES user_status(id);

ALTER TABLE book_copy
    ADD CONSTRAINT uc_bookcopy_qrcode UNIQUE (qr_code);

ALTER TABLE clazz
    ADD CONSTRAINT uc_clazz_name UNIQUE (name);

ALTER TABLE faculty
    ADD CONSTRAINT uc_faculty_name UNIQUE (name);

ALTER TABLE permission
    ADD CONSTRAINT uc_permission_name UNIQUE (name);

ALTER TABLE `role`
    ADD CONSTRAINT uc_role_name UNIQUE (name);

ALTER TABLE staff_profile
    ADD CONSTRAINT uc_staffprofile_employee_code UNIQUE (employee_code);

ALTER TABLE student_profile
    ADD CONSTRAINT uc_studentprofile_student_code UNIQUE (student_code);

ALTER TABLE user
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE book_copy
    ADD CONSTRAINT FK_BOOKCOPY_ON_BOOK FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE book_copy
    ADD CONSTRAINT FK_BOOKCOPY_ON_BOOKLOCATION FOREIGN KEY (booklocation_id) REFERENCES book_location (id);

ALTER TABLE book
    ADD CONSTRAINT FK_BOOK_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE book
    ADD CONSTRAINT FK_BOOK_ON_PUBLISHER FOREIGN KEY (publisher_id) REFERENCES publisher (id);

ALTER TABLE borrow_detail
    ADD CONSTRAINT FK_BORROWDETAIL_ON_BOOK_COPY FOREIGN KEY (book_copy_id) REFERENCES book_copy (id);

ALTER TABLE borrow_detail
    ADD CONSTRAINT FK_BORROWDETAIL_ON_BORROW FOREIGN KEY (borrow_id) REFERENCES borrow (id);

ALTER TABLE borrow
    ADD CONSTRAINT FK_BORROW_ON_STAFF_PROFILE FOREIGN KEY (staff_profile_id) REFERENCES staff_profile (id);

ALTER TABLE borrow
    ADD CONSTRAINT FK_BORROW_ON_STUDENT_PROFILE FOREIGN KEY (student_profile_id) REFERENCES student_profile (id);

ALTER TABLE clazz
    ADD CONSTRAINT FK_CLAZZ_ON_FACULTY FOREIGN KEY (faculty_id) REFERENCES faculty (id);

ALTER TABLE fine
    ADD CONSTRAINT FK_FINE_ON_BORROW_DETAIL FOREIGN KEY (borrow_detail_id) REFERENCES borrow_detail (id);

ALTER TABLE fine
    ADD CONSTRAINT FK_FINE_ON_STUDENT_PROFILE FOREIGN KEY (student_profile_id) REFERENCES student_profile (id);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE payment_detail
    ADD CONSTRAINT FK_PAYMENTDETAIL_ON_FINE FOREIGN KEY (fine_id) REFERENCES fine (id);

ALTER TABLE payment_detail
    ADD CONSTRAINT FK_PAYMENTDETAIL_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payment (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_STAFF_PROFILE FOREIGN KEY (staff_profile_id) REFERENCES staff_profile (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_STUDENT_PROFILE FOREIGN KEY (student_profile_id) REFERENCES student_profile (id);

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_BOOK_COPY FOREIGN KEY (book_copy_id) REFERENCES book_copy (id);

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_STUDENT_PROFILE FOREIGN KEY (student_profile_id) REFERENCES student_profile (id);

ALTER TABLE staff_profile
    ADD CONSTRAINT FK_STAFFPROFILE_ON_USER FOREIGN KEY (id) REFERENCES user (id);

ALTER TABLE student_profile
    ADD CONSTRAINT FK_STUDENTPROFILE_ON_CLAZZ FOREIGN KEY (clazz_id) REFERENCES clazz (id);

ALTER TABLE student_profile
    ADD CONSTRAINT FK_STUDENTPROFILE_ON_USER FOREIGN KEY (id) REFERENCES user (id);

ALTER TABLE book_author
    ADD CONSTRAINT fk_booaut_on_author FOREIGN KEY (author_id) REFERENCES author (id);

ALTER TABLE book_author
    ADD CONSTRAINT fk_booaut_on_book FOREIGN KEY (book_id) REFERENCES book (id);

ALTER TABLE role_permission
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permission (id);

ALTER TABLE role_permission
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_user FOREIGN KEY (user_id) REFERENCES user (id);
