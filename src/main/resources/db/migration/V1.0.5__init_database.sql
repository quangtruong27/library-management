INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN(UUID()),
           'admin',
           '$2a$12$Y.pQsMoZzT9SQ89n8LRCUuguCFwtjv0GMriilePFYztPnlWSmczZS',
           'admin@gmail.com',
           1
       );

INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN(UUID()),
           'student',
           '$2a$12$xTf0XjYsPfxXtplxx7kNsuSMNDYUPmvxAGkJW3AAlvb6aNJ835/Oe',
           'student@gmail.com',
           1
       );

INSERT INTO user (id, name, password, email, status_id)
VALUES (
           UUID_TO_BIN(UUID()),
           'staff',
           '$2a$12$YeZ0Q.HsXHGPg8ZuZM6uleiaojFEYjuVev0GJCtx6JXXLG34erfXy',
           'staff	@gmail.com',
           1
       );