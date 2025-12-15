package org.develop.classe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final String URL = "jdbc:postgresql://localhost:5432/mini_football_db";
    private final String USER = "mini_football_db_manager";
    private final String PASSWORD = "123456";
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }
}

