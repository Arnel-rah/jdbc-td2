package org.develop.classe;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    Dotenv dotenv = Dotenv.load();
    private final String URL = dotenv.get("DB_URL");
    private final String USER = dotenv.get("DB_USER");
    private final String PASSWORD = dotenv.get("DB_PASSWORD");
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }
}

