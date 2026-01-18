CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type VARCHAR(50) NOT NULL,
    selling_price DOUBLE PRECISION
);

CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    category VARCHAR(50) NOT NULL
);

CREATE TABLE dish_ingredient (
    dish_id INT REFERENCES dish(id),
    ingredient_id INT REFERENCES ingredient(id),
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(10) NOT NULL,
    PRIMARY KEY (dish_id, ingredient_id)
);
