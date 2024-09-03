import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class User {
    private int id;  // Add a field to hold the user's ID from the database
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
        loadAccounts(); // Load accounts from the database
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addAccount(String accountName, BankAccount account) {
        accounts.put(accountName, account);
        saveAccountToDatabase(accountName, account);  // Save account to the database
    }

    public BankAccount getAccount(String accountName) {
        return accounts.get(accountName);
    }

    public HashMap<String, BankAccount> getAccounts() {
        return accounts;
    }

    private void loadAccounts() {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String accountName = rs.getString("account_name").trim(); // Ensure no trailing spaces
                double balance = rs.getDouble("balance");
                BankAccount account = new BankAccount(balance);
                accounts.put(accountName, account);  // Use the trimmed account name
            }

            // Debugging: Print loaded accounts to verify
            System.out.println("Loaded accounts: " + accounts.keySet());

        } catch (SQLException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void saveAccountToDatabase(String accountName, BankAccount account) {
        String sql = "INSERT INTO accounts(user_id, account_name, balance) VALUES(?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setString(2, accountName);
            pstmt.setDouble(3, account.getBalance());
            pstmt.executeUpdate();
            System.out.println("Account saved to database.");
        } catch (SQLException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    // New method to create an account with a name
    public void createAccount(String accountName, double initialBalance) {
        BankAccount newAccount = new BankAccount(initialBalance);
        addAccount(accountName, newAccount);
        System.out.println("New account created with name: " + accountName + " and balance: " + initialBalance + "€");
    }
}
