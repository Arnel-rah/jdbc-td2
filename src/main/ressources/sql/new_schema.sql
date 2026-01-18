CREATE TYPE dish_type AS ENUM ('STARTER', 'MAIN', 'DESSERT'); -- 
CREATE TYPE ingredient_category AS ENUM ('VEGETABLE', 'MEAT', 'FRUIT', 'DAIRY', 'OTHER'); 
CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

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