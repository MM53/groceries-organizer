DROP TABLE IF EXISTS shopping_list;
DROP TABLE IF EXISTS shopping_list_item;

CREATE TABLE shopping_list
(
    name VARCHAR NOT NULL PRIMARY KEY
);

CREATE TABLE shopping_list_item
(
    id                      VARCHAR NOT NULL PRIMARY KEY,
    shopping_list_reference VARCHAR NOT NULL,
    item_reference          VARCHAR NOT NULL,


    bought                  BOOLEAN,

    amount_value            FLOAT,
    amount_unit             VARCHAR,
    amount_unit_type        VARCHAR,

    FOREIGN KEY (shopping_list_reference) REFERENCES shopping_list (name) on delete cascade on update cascade,
    FOREIGN KEY (item_reference) REFERENCES item (id)
);
