
CREATE USER mini_football_db_manager WITH PASSWORD '123456';

GRANT ALL PRIVILEGES ON DATABASE mini_football_db TO mini_football_db_manager;

GRANT CREATE ON DATABASE mini_football_db TO mini_football_db_manager;

ALTER DEFAULT PRIVILEGES
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO mini_football_db_manager;