CREATE TABLE products (
    productid BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
    tagline VARCHAR(100),

    sellingprice NUMERIC(12,2) NOT NULL,
    originalprice NUMERIC(12,2) NOT NULL,
    discountpercentage INTEGER DEFAULT 0 CHECK (discountpercentage >= 0 AND discountpercentage <= 100),

    rating NUMERIC(2,1) DEFAULT 0 CHECK (rating >= 0.0 AND rating <= 5.0),
    ratingcount INTEGER NOT NULL DEFAULT 0,

    brand INTEGER NOT NULL,
    fashionstyle INTEGER NOT NULL,
    category INTEGER NOT NULL,
    occasion INTEGER NOT NULL,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL
);

ALTER SEQUENCE products_productid_seq RESTART WITH 10000000001;

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_fashion_style ON products(fashionstyle);
CREATE INDEX idx_products_occasion ON products(occasion);


CREATE TABLE productimages (
    productimages BIGSERIAL PRIMARY KEY,
    productid BIGINT NOT NULL,

    imageurl VARCHAR(512) NOT NULL,
    isthumbnail BOOLEAN DEFAULT FALSE,
    displayorder INTEGER NOT NULL,

    createdtime BIGINT NOT NULL,

    FOREIGN KEY (productid) REFERENCES products(productid) ON DELETE CASCADE
);

ALTER SEQUENCE productimages_productimagesid_seq RESTART WITH 10000000001;

CREATE INDEX idx_productimages_productid ON productimages(productid);


CREATE TABLE productdetails (
    productid BIGINT PRIMARY KEY,
    description TEXT,
    color VARCHAR(50),
    material VARCHAR(100),
    length VARCHAR(50),
    sleeve VARCHAR(50),
    transparency VARCHAR(50),
    careinstructions TEXT,

    rating1star INTEGER DEFAULT 0,
    rating2star INTEGER DEFAULT 0,
    rating3star INTEGER DEFAULT 0,
    rating4star INTEGER DEFAULT 0,
    rating5star INTEGER DEFAULT 0,

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    FOREIGN KEY (productid) REFERENCES products(productid) ON DELETE CASCADE
);


CREATE TABLE productstock (
    productstock BIGSERIAL PRIMARY KEY,
    productid BIGINT NOT NULL,

    size INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL,

    UNIQUE (productid, size),
    FOREIGN KEY (productid) REFERENCES products(productid) ON DELETE CASCADE
);

ALTER SEQUENCE productstock_productstockid_seq RESTART WITH 10000000001;

CREATE INDEX idx_productstock_productid ON productstock(productid);
