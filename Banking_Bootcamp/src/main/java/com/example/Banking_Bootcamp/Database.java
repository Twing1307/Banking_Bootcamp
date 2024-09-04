package com.example.Banking_Bootcamp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:banking_system.db";
    private static final String ERROR_MESSAGE = "Database error: ";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(ERROR_MESSAGE + e.getMessage());
            return null;
        }
    }

    public static void createNewDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(ERROR_MESSAGE + e.getMessage());
        }
    }

    private static void executeStatement(String sql) {
        try (Connection conn = connect()) {
            if (conn == null) {
                System.out.println("Connection failed. Cannot execute statement.");
                return;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_MESSAGE + e.getMessage());
        }
    }

    public static void createTables() {
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    id integer PRIMARY KEY,\n"
                + "    username text NOT NULL UNIQUE,\n"
                + "    password text NOT NULL\n"
                + ");";
        String createAccountTableSQL = "CREATE TABLE IF NOT EXISTS accounts (\n"
                + "    account_id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "    user_id integer NOT NULL,\n"
                + "    account_name text NOT NULL,\n"
                + "    balance real NOT NULL,\n"
                + "    FOREIGN KEY(user_id) REFERENCES users(id)\n"
                + ");";
        executeStatement(createUserTableSQL);
        executeStatement(createAccountTableSQL);
        System.out.println("Tables created.");
    }
}