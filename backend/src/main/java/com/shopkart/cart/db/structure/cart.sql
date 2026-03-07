CREATE TABLE cart (
    cartid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    productid BIGINT NOT NULL,
    size INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    UNIQUE (userid, productid, size),
    FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    FOREIGN KEY (productid) REFERENCES products(productid) ON DELETE CASCADE
);

ALTER SEQUENCE cart_cartid_seq RESTART WITH 10000000001;

CREATE INDEX idx_cart_userid ON cart(userid);


CREATE TABLE wishlist (
    wishlistid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    productid BIGINT NOT NULL,

    createdtime BIGINT NOT NULL,

    UNIQUE (userid, productid),
    FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    FOREIGN KEY (productid) REFERENCES products(productid) ON DELETE CASCADE
);

ALTER SEQUENCE wishlist_wishlistid_seq RESTART WITH 10000000001;

CREATE INDEX idx_wishlist_userid ON wishlist(userid);