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

CREATE TABLE userdetails (
    userid BIGINT PRIMARY KEY,

    phonenumber VARCHAR(15),
    gender SMALLINT,
    dateofbirth DATE,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);


CREATE TABLE address (
    addressid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,
    contactnumber VARCHAR(15) NOT NULL,
    firstline VARCHAR(255) NOT NULL,
    secondline VARCHAR(255) NOT NULL,
    landmark VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    isdefault BOOLEAN NOT NULL DEFAULT FALSE,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);

ALTER SEQUENCE address_addressid_seq RESTART WITH 10000000001;

CREATE INDEX idx_address_userid ON address(userid);