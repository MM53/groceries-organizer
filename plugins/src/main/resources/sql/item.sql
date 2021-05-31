DROP TABLE IF EXISTS item_name;
DROP TABLE IF EXISTS item;

CREATE TABLE item (
                      id VARCHAR PRIMARY KEY,
                      unit_type VARCHAR NOT NULL
);

CREATE TABLE item_name
(
    name           VARCHAR PRIMARY KEY,
    item_reference VARCHAR NOT NULL,

    foreign key (item_reference) references item (id) on delete cascade on update cascade
);
