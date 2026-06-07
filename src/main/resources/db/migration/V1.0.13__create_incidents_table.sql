CREATE TABLE incident
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    title           VARCHAR(255)          NOT NULL,
    description     TEXT                  NOT NULL,
    priority        VARCHAR(255)          NOT NULL,
    status          VARCHAR(255)          NOT NULL,
    reporter_id     BINARY(16)            NOT NULL,
    assignee_id     BINARY(16)            NULL,
    resolution_note VARCHAR(1000)         NULL,
    created_date    datetime              NOT NULL,
    updated_date    datetime              NOT NULL,
    CONSTRAINT pk_incident PRIMARY KEY (id),
    CONSTRAINT fk_incident_on_reporter FOREIGN KEY (reporter_id) REFERENCES user (id),
    CONSTRAINT fk_incident_on_assignee FOREIGN KEY (assignee_id) REFERENCES user (id)
);

CREATE TABLE incident_comment
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    incident_id  BIGINT                NOT NULL,
    user_id      BINARY(16)            NOT NULL,
    content      TEXT                  NOT NULL,
    created_date datetime              NOT NULL,
    CONSTRAINT pk_incident_comment PRIMARY KEY (id),
    CONSTRAINT fk_incident_comment_on_incident FOREIGN KEY (incident_id) REFERENCES incident (id) ON DELETE CASCADE,
    CONSTRAINT fk_incident_comment_on_user FOREIGN KEY (user_id) REFERENCES user (id)
);
