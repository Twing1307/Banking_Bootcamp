import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:banking_system.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        // SQL to create the users table
        String userTable = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    id integer PRIMARY KEY,\n"
                + "    username text NOT NULL UNIQUE,\n"
                + "    password text NOT NULL\n"
                + ");";

        String accountTable = "CREATE TABLE IF NOT EXISTS accounts (\n"
                + "    account_id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "    user_id integer NOT NULL,\n"
                + "    balance real NOT NULL,\n"
                + "    FOREIGN KEY(user_id) REFERENCES users(id)\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(accountTable);
            System.out.println("Tables created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
