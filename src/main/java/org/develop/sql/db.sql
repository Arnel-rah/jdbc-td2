-- create user mini_football_db_manager
CREATE USER mini_football_db_manager WITH PASSWORD '123456';

--  grant privileges on the database
GRANT ALL PRIVILEGES ON DATABASE mini_football_db TO mini_football_db_manager;

--  allow user to create tables
GRANT CREATE ON DATABASE mini_football_db TO mini_football_db_manager;
--  give CRUD permissions on all future tables
ALTER DEFAULT PRIVILEGES
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO mini_football_db_manager;