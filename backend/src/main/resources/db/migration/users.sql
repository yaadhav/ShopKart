CREATE TABLE users (
    userid BIGSERIAL PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,

    role INTEGER NOT NULL DEFAULT 1,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL
);

ALTER SEQUENCE users_userid_seq RESTART WITH 10000000001;

CREATE UNIQUE INDEX idx_users_email ON users(email);
