CREATE TABLE products (
    productid BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
    description VARCHAR(50),

    sellingprice NUMERIC(12,2) NOT NULL,
    originalprice NUMERIC(12,2) NOT NULL,
    discountpercentage INTEGER DEFAULT 0 CHECK (discountpercentage >= 0 AND discountpercentage <= 100),

    rating NUMERIC(2,1) DEFAULT 0 CHECK (rating >= 0.0 AND rating <= 5.0),
    ratingcount INTEGER NOT NULL DEFAULT 0,

    brand INTEGER NOT NULL,
    fashionstyle INTEGER NOT NULL,
    category INTEGER NOT NULL,
    occasion INTEGER NOT NULL,
    size INTEGER NOT NULL,

    image VARCHAR(512),

    createdtime BIGINT NOT NULL,
    updatedtime BIGINT NOT NULL
);

ALTER SEQUENCE products_productid_seq RESTART WITH 10000000001;

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_fashion_style ON products(fashionstyle);
CREATE INDEX idx_products_occasion ON products(occasion);