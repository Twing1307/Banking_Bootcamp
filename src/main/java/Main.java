import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static User loggedInUser = null;

    public static void main(String[] args) {
        // Create the database and tables if they don't exist
        Database.createNewDatabase();
        Database.createTables();

        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            if (loggedInUser == null) {
                System.out.println("\nBanking System:");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Choose an option:");

                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        registerUser(scanner);
                        break;
                    case 2:
                        loginUser(scanner);
                        break;
                    case 3:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } else {
                System.out.println("\nWelcome, " + loggedInUser.getUsername());
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Transfer");
                System.out.println("4. Print Balance");
                System.out.println("5. Print Transaction History");
                System.out.println("6. Generate Report (.txt file)");
                System.out.println("7. Logout");
                System.out.println("Choose an option:");

                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        performDeposit(scanner);
                        break;
                    case 2:
                        performWithdrawal(scanner);
                        break;
                    case 3:
                        performTransfer(scanner);
                        break;
                    case 4:
                        printBalance(scanner);
                        break;
                    case 5:
                        printTransactionHistory(scanner);
                        break;
                    case 6:
                        try {
                            BankAccount.generateReport(loggedInUser.getAccounts());
                        } catch (IOException e) {
                            System.out.println("Error generating report: " + e.getMessage());
                        }
                        break;
                    case 7:
                        logoutUser();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        }
        scanner.close();
    }

    private static void registerUser(Scanner scanner) {
        System.out.println("Enter username: ");
        String username = scanner.next();
        System.out.println("Enter password: ");
        String password = scanner.next();

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User registered successfully. Creating a new bank account...");

            // Automatically log in the new user
            loggedInUser = new User(getUserId(username), username, password);

            // Prompt to create a new account
            createAccount(scanner);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void loginUser(Scanner scanner) {
        System.out.println("Enter username: ");
        String username = scanner.next();
        System.out.println("Enter password: ");
        String password = scanner.next();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                loggedInUser = new User(userId, username, password);
                System.out.println("Login successful.");
            } else {
                System.out.println("Incorrect username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void logoutUser() {
        loggedInUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void performDeposit(Scanner scanner) {
        System.out.println("Enter account name for deposit: ");
        String accountName = scanner.next();
        System.out.println("Enter deposit amount: ");
        double depositAmount = scanner.nextDouble();
        confirmWithPassword(scanner);

        if (loggedInUser.getAccounts().containsKey(accountName)) {
            BankAccount account = loggedInUser.getAccount(accountName);
            account.deposit(depositAmount);
            updateAccountBalanceInDatabase(accountName, account.getBalance());
        } else {
            System.out.println("Account name not found.");
        }
    }

    private static void performWithdrawal(Scanner scanner) {
        System.out.println("Enter account name for withdrawal: ");
        String accountName = scanner.next();
        System.out.println("Enter withdrawal amount: ");
        double withdrawAmount = scanner.nextDouble();
        confirmWithPassword(scanner);

        if (loggedInUser.getAccounts().containsKey(accountName)) {
            BankAccount account = loggedInUser.getAccount(accountName);
            account.withdraw(withdrawAmount);
            updateAccountBalanceInDatabase(accountName, account.getBalance());
        } else {
            System.out.println("Account name not found.");
        }
    }

    private static void performTransfer(Scanner scanner) {
        System.out.println("Enter account name to transfer from: ");
        String fromAccountName = scanner.next();
        System.out.println("Enter recipient's username: ");
        String recipientUsername = scanner.next();
        System.out.println("Enter recipient's account name: ");
        String toAccountName = scanner.next();
        System.out.println("Enter transfer amount: ");
        double transferAmount = scanner.nextDouble();
        confirmWithPassword(scanner);

        // Check if the logged-in user has the "from" account
        if (!loggedInUser.getAccounts().containsKey(fromAccountName)) {
            System.out.println("Source account not found.");
            return;
        }

        BankAccount fromAccount = loggedInUser.getAccount(fromAccountName);

        // Fetch recipient's account from the database
        BankAccount toAccount = fetchAccountFromDatabase(recipientUsername, toAccountName);

        // Check if the recipient account was found
        if (toAccount == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        // Proceed with the transfer
        fromAccount.transfer(toAccount, transferAmount);
        updateAccountBalanceInDatabase(fromAccountName, fromAccount.getBalance());
        updateAccountBalanceInDatabase(recipientUsername, toAccountName, toAccount.getBalance());
        System.out.println("Transfer completed successfully.");
    }

    // Helper method to fetch an account from the database by username and account name
    private static BankAccount fetchAccountFromDatabase(String username, String accountName) {
        String sql = "SELECT a.balance FROM accounts a JOIN users u ON a.user_id = u.id WHERE u.username = ? AND a.account_name = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, accountName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                return new BankAccount(balance);
            } else {
                return null; // Account not found
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account: " + e.getMessage());
            return null;
        }
    }

    // Update database with specific account balance change for cross-user accounts
    private static void updateAccountBalanceInDatabase(String username, String accountName, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_name = ? AND user_id = (SELECT id FROM users WHERE username = ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountName);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
            System.out.println("Account balance updated in database.");
        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }


    private static void printBalance(Scanner scanner) {
        System.out.println("Enter account name to print balance: ");
        String accountName = scanner.next();
        confirmWithPassword(scanner);
        if (loggedInUser.getAccounts().containsKey(accountName)) {
            loggedInUser.getAccount(accountName).printBalance();
        } else {
            System.out.println("Account name not found.");
        }
    }

    private static void printTransactionHistory(Scanner scanner) {
        System.out.println("Enter account name to print transaction history: ");
        String accountName = scanner.next();
        confirmWithPassword(scanner);
        if (loggedInUser.getAccounts().containsKey(accountName)) {
            loggedInUser.getAccount(accountName).printTransactionHistory();
        } else {
            System.out.println("Account name not found.");
        }
    }

    private static void updateAccountBalanceInDatabase(String accountName, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_name = ? AND user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountName);
            pstmt.setInt(3, loggedInUser.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    // New method to create a bank account for the user
    private static void createAccount(Scanner scanner) {
        System.out.println("Enter a name for the new account: ");
        String accountName = scanner.next();
        System.out.println("Enter initial balance for the new account: ");
        double initialBalance = scanner.nextDouble();
        loggedInUser.createAccount(accountName, initialBalance);  // Calls the method in User class to create a new account
    }

    // Helper to confirm actions with a password
    private static void confirmWithPassword(Scanner scanner) {
        System.out.println("Please enter your password for confirmation: ");
        String password = scanner.next();
        if (!loggedInUser.checkPassword(password)) {
            System.out.println("Incorrect password. Action cancelled.");
            System.exit(0);  // Exit if the password confirmation fails
        }
    }

    // Helper to retrieve user ID by username
    private static int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("User ID not found.");
            }
        }
    }
}
