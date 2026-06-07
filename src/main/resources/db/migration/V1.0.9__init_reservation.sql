-- Tạo bảng reservation_status
CREATE TABLE reservation_status
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NULL,
    CONSTRAINT pk_reservation_status PRIMARY KEY (id)
);

ALTER TABLE reservation_status
    ADD CONSTRAINT uc_reservation_status_name UNIQUE (name);

-- Insert dữ liệu mẫu
INSERT INTO reservation_status (name, description) VALUES
    ('PENDING',   'Đang giữ chỗ, chờ sinh viên đến lấy'),
    ('READY',     'Sách đã sẵn sàng, sinh viên có thể đến nhận'),
    ('EXPIRED',   'Đã hết hạn giữ chỗ'),
    ('CANCELLED', 'Sinh viên đã hủy đặt trước');

-- Đổi cột status VARCHAR sang status_id BIGINT FK trong bảng reservation
ALTER TABLE reservation DROP COLUMN status;

ALTER TABLE reservation ADD COLUMN status_id BIGINT NULL;

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_STATUS
        FOREIGN KEY (status_id) REFERENCES reservation_status (id);
