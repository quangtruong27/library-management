CREATE TABLE book_return
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    return_date        datetime              NOT NULL,
    borrow_detail_id   BIGINT                NOT NULL,
    staff_profile_id   BINARY(16)            NOT NULL,
    status             VARCHAR(255)          NOT NULL,
    book_condition     VARCHAR(255)          NOT NULL,
    fine_amount        DECIMAL(10, 2)        NULL,
    overdue_days       INT                   NULL,
    overdue_fine       DECIMAL(10, 2)        NULL,
    damage_fine        DECIMAL(10, 2)        NULL,
    note               VARCHAR(255)          NULL,
    CONSTRAINT pk_book_return PRIMARY KEY (id),
    CONSTRAINT fk_book_return_on_borrow_detail FOREIGN KEY (borrow_detail_id) REFERENCES borrow_detail (id),
    CONSTRAINT fk_book_return_on_staff FOREIGN KEY (staff_profile_id) REFERENCES staff_profile (id)
);
