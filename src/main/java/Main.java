import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static User loggedInUser = null;
    private static final String LOGIN_PROMPT = "Enter username: ";
    private static final String PASSWORD_PROMPT = "Enter password: ";
    private static final String INVALID_OPTION = "Invalid option. Please try again.";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Database.createNewDatabase();
            Database.createTables();
            boolean running = true;
            while (running) {
                if (loggedInUser == null) {
                    handleUserNotLoggedIn(scanner);
                } else {
                    handleUserLoggedIn(scanner);
                }
            }
        }
    }

    private static void handleUserNotLoggedIn(Scanner scanner) {
        System.out.println("\nBanking System:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> registerUser(scanner);
            case 2 -> loginUser(scanner);
            case 3 -> {
                System.out.println("Exiting...");
                System.exit(0);
            }
            default -> System.out.println(INVALID_OPTION);
        }
    }

    private static void handleUserLoggedIn(Scanner scanner) {
        System.out.println("\nWelcome, " + loggedInUser.getUsername());
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Transfer");
        System.out.println("4. Print Balance");
        System.out.println("5. Print Transaction History");
        System.out.println("6. Generate Report (.txt file)");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> performDeposit(scanner);
            case 2 -> performWithdrawal(scanner);
            case 3 -> performTransfer(scanner);
            case 4 -> printBalance(scanner);
            case 5 -> printTransactionHistory(scanner);
            case 6 -> generateReport();
            case 7 -> logoutUser();
            default -> System.out.println(INVALID_OPTION);
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print(LOGIN_PROMPT);
        String username = scanner.next();
        System.out.print(PASSWORD_PROMPT);
        String password = scanner.next();

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");

            loggedInUser = new User(getUserId(username), username, password);

            createUniqueBankAccount(conn, scanner);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createUniqueBankAccount(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter initial balance for the new account: ");
        double initialBalance = scanner.nextDouble();

        String countSQL = "SELECT COUNT(*) AS account_count FROM accounts WHERE user_id = ?";
        int accountNumber = 1;

        try (PreparedStatement countStmt = conn.prepareStatement(countSQL)) {
            countStmt.setInt(1, loggedInUser.getId());
            ResultSet rs = countStmt.executeQuery();
            if (rs.next()) {
                accountNumber = rs.getInt("account_count") + 1;
            }
        }

        String accountName = loggedInUser.getUsername();
        String insertAccountSQL = "INSERT INTO accounts(user_id, account_name, balance) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertAccountSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, loggedInUser.getId());
            pstmt.setString(2, accountName);
            pstmt.setDouble(3, initialBalance);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            loggedInUser.createAccount(accountName, initialBalance);
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void loginUser(Scanner scanner) {
        System.out.print(LOGIN_PROMPT);
        String username = scanner.next();
        System.out.print(PASSWORD_PROMPT);
        String password = scanner.next();
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUser = new User(rs.getInt("id"), username, password);
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
        System.out.print("Enter account name for deposit: ");
        String accountName = scanner.next();
        System.out.print("Enter deposit amount: ");
        double depositAmount = scanner.nextDouble();
        confirmWithPassword(scanner);
        if (loggedInUser.getAccounts().containsKey(accountName)) {
            BankAccount account = loggedInUser.getAccount(accountName);
            account.deposit(depositAmount);
            updateAccountBalanceInDatabase(accountName, account.getBalance());
        } else {
            System.out.println("Account name not found.");
            System.out.println("Available accounts: " + loggedInUser.getAccounts().keySet());
        }
    }

    private static void generateReport() {
        try {
            BankAccount.generateReport(loggedInUser.getAccounts());
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
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
            System.out.println("Available accounts: " + loggedInUser.getAccounts().keySet());
        }
    }

    private static void performTransfer(Scanner scanner) {
        System.out.println("Enter account name to transfer from: ");
        String fromAccountName = scanner.next();
        System.out.println("Enter recipient's account name: ");
        String toAccountName = scanner.next();
        System.out.println("Enter transfer amount: ");
        double transferAmount = scanner.nextDouble();
        confirmWithPassword(scanner);

        // Check if the logged-in user has the "from" account
        if (!loggedInUser.getAccounts().containsKey(fromAccountName)) {
            System.out.println("Source account not found.");
            System.out.println("Available accounts: " + loggedInUser.getAccounts().keySet());
            return;
        }

        BankAccount fromAccount = loggedInUser.getAccount(fromAccountName);

        // Fetch recipient's account from the database using the account name only
        BankAccount toAccount = fetchAccountFromDatabase(toAccountName);

        // Check if the recipient account was found
        if (toAccount == null) {
            System.out.println("Recipient account not found.");
            System.out.println("Available accounts: " + loggedInUser.getAccounts().keySet());
            return;
        }

        // Proceed with the transfer
        fromAccount.transfer(toAccount, transferAmount);
        updateAccountBalanceInDatabase(fromAccountName, fromAccount.getBalance());
        updateAccountBalanceInDatabase(toAccountName, toAccount.getBalance());
        System.out.println("Transfer completed successfully.");
    }


    // Fetch an account from the database by account name
    private static BankAccount fetchAccountFromDatabase(String accountName) {
        String sql = "SELECT balance FROM accounts WHERE account_name = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
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
        System.out.print("Enter account name to print balance: ");
        String accountName = scanner.next();
        confirmWithPassword(scanner);

        if (loggedInUser.getAccounts().containsKey(accountName)) {
            loggedInUser.getAccount(accountName).printBalance();
        } else {
            System.out.println("Account name not found. Available accounts: " + loggedInUser.getAccounts().keySet());
        }
    }

    private static void printTransactionHistory(Scanner scanner) {
        System.out.println("Enter account name to print transaction history: ");
        String accountName = scanner.next();
        confirmWithPassword(scanner);
        if (loggedInUser.getAccounts().containsKey(accountName)) {
            loggedInUser.getAccount(accountName).displayTransactionHistory();
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

    private static void createAccount(Scanner scanner) {
        System.out.println("Enter a name for the new account: ");
        String accountName = scanner.next();
        System.out.println("Enter initial balance for the new account: ");
        double initialBalance = scanner.nextDouble();
        loggedInUser.createAccount(accountName, initialBalance);
    }

    private static void confirmWithPassword(Scanner scanner) {
        System.out.println("Please enter your password for confirmation: ");
        String password = scanner.next();
        if (!loggedInUser.checkPassword(password)) {
            System.out.println("Incorrect password. Action cancelled.");
            System.exit(0);
        }
    }

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
