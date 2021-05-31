DROP TABLE IF EXISTS recipe_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS ingredient;
DROP TABLE IF EXISTS recipe;

CREATE TABLE tag
(
    name VARCHAR NOT NULL PRIMARY KEY
);

CREATE TABLE recipe
(
    id          VARCHAR NOT NULL PRIMARY KEY,
    name        VARCHAR NOT NULL,

    description VARCHAR
);

CREATE TABLE ingredient
(
    id               VARCHAR NOT NULL PRIMARY KEY,
    recipe_reference VARCHAR NOT NULL,
    item_reference   VARCHAR NOT NULL,

    amount_value     FLOAT   NOT NULL,
    amount_unit      VARCHAR NOT NULL,
    amount_unit_type VARCHAR NOT NULL,

    FOREIGN KEY (recipe_reference) REFERENCES recipe (id) on delete cascade on update cascade
);

CREATE TABLE recipe_tag
(
    id               VARCHAR NOT NULL PRIMARY KEY,
    recipe_reference VARCHAR NOT NULL,
    tag_reference    VARCHAR NOT NULL,

    FOREIGN KEY (recipe_reference) REFERENCES recipe (id) on delete cascade on update cascade,
    FOREIGN KEY (tag_reference) REFERENCES tag (name) on delete cascade on update cascade

)
