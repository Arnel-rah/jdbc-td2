package org.develop.classe;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final String url;
    private final String user;
    private final String password;

    public DBConnection() {
        Dotenv dotenv = Dotenv.load();
        url = dotenv.get("DB_URL");
        user = dotenv.get("DB_USER");
        password = dotenv.get("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new RuntimeException(
                    "Variables d'environnement manquantes dans le fichier .env : " + "DB_URL, DB_USER ou DB_PASSWORD"
            );
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Impossible d'établir la connexion à la base de données",
                    e
            );
        }
    }
}