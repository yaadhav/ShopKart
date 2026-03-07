CREATE TABLE feedetails (
    feedetailsid BIGSERIAL PRIMARY KEY,
    deliveryfee NUMERIC(10,2) NOT NULL,
    platformfee NUMERIC(10,2) NOT NULL,

    updatedby BIGINT NOT NULL,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (updatedby) REFERENCES users(userid)
);

ALTER SEQUENCE feedetails_feedetailsid_seq RESTART WITH 10000000001;