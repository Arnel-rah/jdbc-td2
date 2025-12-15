
CREATE TYPE position_enum AS ENUM ('GK', 'DEF', 'MIDF', 'STR');
CREATE TYPE continent_enum AS ENUM ('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');

CREATE TABLE Team(
                     id INT PRIMARY KEY,
                     name VARCHAR(255),
                     continent continent_enum
);

CREATE TABLE Player(
                       id INT PRIMARY KEY,
                       name VARCHAR(255),
                       age INT,
                       position position_enum,
                       id_team INT NOT NULL,
                       FOREIGN KEY (id_team) REFERENCES Team(id)
);
