import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {

    public Connection getConnection() {
        Dotenv dotenv = Dotenv.load();
        try {
            String jdbcURl = dotenv.get("JDBC_URl"); //
            String user = dotenv.get("USER"); //mini_dish_db_manager
            String password = dotenv.get("PASSWORD"); //123456
            return DriverManager.getConnection(jdbcURl, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
