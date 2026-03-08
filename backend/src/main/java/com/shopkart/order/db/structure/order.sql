CREATE TABLE orders (
    orderid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    paymentid BIGINT,

    orderamount NUMERIC(12,2) NOT NULL,
    ordersavings NUMERIC(12,2) NOT NULL DEFAULT 0,
    conveniencefee NUMERIC(12,2) NOT NULL DEFAULT 0,
    ordertotal NUMERIC(12,2) NOT NULL,

    paymentmode INTEGER NOT NULL,
    paymentstatus INTEGER NOT NULL,
    orderstatus INTEGER NOT NULL,

    initiatedtime BIGINT NOT NULL,
    deliveredtime BIGINT,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (userid) REFERENCES users(userid)
);

ALTER SEQUENCE orders_orderid_seq RESTART WITH 10000000001;

CREATE INDEX idx_orders_userid ON orders(userid);
CREATE INDEX idx_orders_paymentid ON orders(paymentid);


CREATE TABLE ordermapping (
    ordermappingid BIGSERIAL PRIMARY KEY,
    orderid BIGINT NOT NULL,
    userid BIGINT NOT NULL,
    productid BIGINT NOT NULL,

    imageurl VARCHAR(512) NOT NULL,
    productname VARCHAR(255) NOT NULL,
    size INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    sellingprice NUMERIC(12,2) NOT NULL,
    originalprice NUMERIC(12,2) NOT NULL,
    savings NUMERIC(12,2) NOT NULL DEFAULT 0,

    createdtime BIGINT NOT NULL,

    FOREIGN KEY (orderid) REFERENCES orders(orderid),
    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (productid) REFERENCES products(productid)
);

ALTER SEQUENCE ordermapping_ordermappingid_seq RESTART WITH 10000000001;

CREATE INDEX idx_ordermapping_orderid ON ordermapping(orderid);
CREATE INDEX idx_ordermapping_userid ON ordermapping(userid);


CREATE TABLE orderaddress (
    orderaddressid BIGSERIAL PRIMARY KEY,
    orderid BIGINT NOT NULL UNIQUE,

    name VARCHAR(100) NOT NULL,
    contactnumber VARCHAR(15) NOT NULL,
    firstline VARCHAR(255) NOT NULL,
    secondline VARCHAR(255) NOT NULL,
    landmark VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,

    createdtime BIGINT NOT NULL,

    FOREIGN KEY (orderid) REFERENCES orders(orderid)
);

ALTER SEQUENCE orderaddress_orderaddressid_seq RESTART WITH 10000000001;

CREATE INDEX idx_orderaddress_orderid ON orderaddress(orderid);