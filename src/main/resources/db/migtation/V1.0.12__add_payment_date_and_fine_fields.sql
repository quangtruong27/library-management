ALTER TABLE payment ADD COLUMN payment_date datetime NULL;
ALTER TABLE payment ADD COLUMN amount DECIMAL(10, 2) NULL;
ALTER TABLE fine ADD COLUMN created_date datetime NULL;
ALTER TABLE fine ADD COLUMN due_date datetime NULL;
ALTER TABLE fine ADD COLUMN note VARCHAR(255) NULL;
