CREATE TABLE paymentintent (
    paymentintentid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    orderid BIGINT NOT NULL,
    totalamount NUMERIC(12,2) NOT NULL,
    paymentstatus INTEGER NOT NULL,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (orderid) REFERENCES orders(orderid)
);

ALTER SEQUENCE paymentintent_paymentintentid_seq RESTART WITH 10000000001;

CREATE INDEX idx_paymentintent_userid ON paymentintent(userid);
CREATE INDEX idx_paymentintent_orderid ON paymentintent(orderid);


CREATE TABLE payment (
    paymentid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    orderid BIGINT NOT NULL,
    paymentintentid BIGINT NOT NULL,
    paymentmethod INTEGER NOT NULL,
    paymentmode INTEGER NOT NULL,
    totalamount NUMERIC(12,2) NOT NULL,
    referenceid VARCHAR(255),

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (orderid) REFERENCES orders(orderid),
    FOREIGN KEY (paymentintentid) REFERENCES paymentintent(paymentintentid)
);

ALTER SEQUENCE payment_paymentid_seq RESTART WITH 10000000001;

CREATE INDEX idx_payment_userid ON payment(userid);
CREATE INDEX idx_payment_orderid ON payment(orderid);
CREATE INDEX idx_payment_paymentintentid ON payment(paymentintentid);

ALTER TABLE orders ADD CONSTRAINT fk_orders_paymentid
    FOREIGN KEY (paymentid) REFERENCES payment(paymentid);