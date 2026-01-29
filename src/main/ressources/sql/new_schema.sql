CREATE TYPE dish_type AS ENUM ('STARTER', 'MAIN', 'DESSERT'); -- 
CREATE TYPE ingredient_category AS ENUM ('VEGETABLE', 'MEAT', 'FRUIT', 'DAIRY', 'OTHER'); 
CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');
CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE SEQUENCE order_ref_seq START WITH 1;
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type NOT NULL,
    selling_price NUMERIC(10, 2)
);


CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL, 
    category ingredient_category NOT NULL 
);

CREATE TABLE DishIngredient (
    id SERIAL PRIMARY KEY, 
    id_dish INT REFERENCES dish(id),
    id_ingredient INT REFERENCES ingredient(id),
    quantity_required NUMERIC(10, 2) NOT NULL,
    unit unit_type NOT NULL 
);

CREATE TABLE IF NOT EXISTS stock_movement (
    id SERIAL PRIMARY KEY,
    id_ingredient INT REFERENCES ingredient(id),
    quantity NUMERIC(10, 2) NOT NULL,
    "type" movement_type NOT NULL,
    unit unit_type NOT NULL,
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "order"(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255),
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dish_order(
    id SERIAL PRIMARY KEY,
    id_order INT REFERENCES "order"(id),
    id_dish INT REFERENCES dish(id),
    quantity NUMERIC(10, 1) NOT NULL
);


DROP TABLE stock_movement;

INSERT INTO stock_movement (id_ingredient, quantity, "type", unit, creation_datetime) VALUES

(1,0.2,'OUT','KG','2024-01-06 12:00'),
(2,4.0, 'IN', 'KG', '2024-01-05 08:00'),
(2,0.15, 'OUT', 'KG', '2024-01-06 12:00'),
(3,10.0, 'IN', 'KG', '2024-01-04 09:00'),
(3,1.0,'OUT','KG', '2024-01-06 13:00'),
(4,3.0, 'IN', 'KG', '2024-01-05 10:00'),
(4,0.3, 'OUT', 'KG', '2024-01-06 14:00'),
(5,2.5, 'IN', 'KG', '2024-01-05 10:00'),
(5,0.2, 'OUT', 'KG', '2024-01-06 14:00')
;






