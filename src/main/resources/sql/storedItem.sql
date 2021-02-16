DROP TABLE IF EXISTS item_location;
DROP TABLE IF EXISTS stored_item;
DROP TABLE IF EXISTS minimum_amount;

CREATE TABLE minimum_amount
(
    id               VARCHAR NOT NULL PRIMARY KEY,

    amount_value     FLOAT   NOT NULL,
    amount_unit      VARCHAR NOT NULL,
    amount_unit_type VARCHAR NOT NULL
);

CREATE TABLE stored_item
(
    id                       VARCHAR NOT NULL PRIMARY KEY,
    item_reference           VARCHAR NOT NULL,
    minimum_amount_reference VARCHAR,

    FOREIGN KEY (item_reference) REFERENCES item (id),
    FOREIGN KEY (minimum_amount_reference) REFERENCES minimum_amount (id)
);

CREATE TABLE item_location
(
    id                    VARCHAR NOT NULL PRIMARY KEY,
    stored_item_reference VARCHAR NOT NULL,

    location_room         VARCHAR,
    location_place        VARCHAR,
    location_shelf        VARCHAR,

    amount_value          FLOAT   NOT NULL,
    amount_unit           VARCHAR NOT NULL,
    amount_unit_type      VARCHAR NOT NULL,

    FOREIGN KEY (stored_item_reference) REFERENCES stored_item (id)
);
