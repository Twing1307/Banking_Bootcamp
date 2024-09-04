package com.example.Banking_Bootcamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class User {
    private static final String LOAD_ACCOUNTS_SQL = "SELECT * FROM accounts WHERE user_id = ?";
    private static final String SAVE_ACCOUNT_SQL = "INSERT INTO accounts(user_id, account_name, balance) VALUES(?, ?, ?)";

    private int id;
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
        loadAccountsFromDatabase();
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
        saveAccountToDatabase(accountName, account);
    }

    public BankAccount getAccount(String accountName) {
        return accounts.get(accountName);
    }

    public HashMap<String, BankAccount> getAccounts() {
        return accounts;
    }

    private void loadAccountsFromDatabase() {
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = createPreparedStatement(conn, LOAD_ACCOUNTS_SQL, this.id);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String accountName = rs.getString("account_name").trim();
                double balance = rs.getDouble("balance");
                BankAccount account = new BankAccount(balance);
                accounts.put(accountName, account);
            }
        } catch (SQLException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void saveAccountToDatabase(String accountName, BankAccount account) {
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = createPreparedStatement(conn, SAVE_ACCOUNT_SQL, accountName, account)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, int userId) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        return pstmt;
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, String accountName, BankAccount account) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, this.id);
        pstmt.setString(2, accountName);
        pstmt.setDouble(3, account.getBalance());
        return pstmt;
    }

    public void createAccount(String accountName, double initialBalance) {
        this.accounts.put(accountName, new BankAccount(initialBalance));
    }
}

