CREATE TABLE item_alternative_name
(
    name           VARCHAR PRIMARY KEY,
    alternative_for VARCHAR NOT NULL,

    foreign key (alternative_for) references item (primary_name)
);