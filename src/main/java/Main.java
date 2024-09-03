import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static User loggedInUser = null;

    public static void main(String[] args) throws IOException {
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
                        BankAccount.generateReport(loggedInUser.getAccounts());
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
            System.out.println("User registered successfully. Please login.");
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
                loggedInUser = new User(username, password);
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
        System.out.println("Enter account ID for deposit: ");
        String accountId = scanner.next();
        System.out.println("Enter deposit amount: ");
        double depositAmount = scanner.nextDouble();
        if (loggedInUser.getAccounts().containsKey(accountId)) {
            loggedInUser.getAccount(accountId).deposit(depositAmount);
        } else {
            System.out.println("Account ID not found.");
        }
    }

    private static void performWithdrawal(Scanner scanner) {
        System.out.println("Enter account ID for withdrawal: ");
        String accountId = scanner.next();
        System.out.println("Enter withdrawal amount: ");
        double withdrawAmount = scanner.nextDouble();
        if (loggedInUser.getAccounts().containsKey(accountId)) {
            loggedInUser.getAccount(accountId).withdraw(withdrawAmount);
        } else {
            System.out.println("Account ID not found.");
        }
    }

    private static void performTransfer(Scanner scanner) {
        System.out.println("Enter account ID to transfer from: ");
        String fromAccountId = scanner.next();
        System.out.println("Enter account ID to transfer to: ");
        String toAccountId = scanner.next();
        System.out.println("Enter transfer amount: ");
        double transferAmount = scanner.nextDouble();
        if (loggedInUser.getAccounts().containsKey(fromAccountId) && loggedInUser.getAccounts().containsKey(toAccountId)) {
            loggedInUser.getAccount(fromAccountId).transfer(loggedInUser.getAccount(toAccountId), transferAmount);
        } else {
            System.out.println("One or both account IDs not found.");
        }
    }

    private static void printBalance(Scanner scanner) {
        System.out.println("Enter account ID to print balance: ");
        String accountId = scanner.next();
        if (loggedInUser.getAccounts().containsKey(accountId)) {
            loggedInUser.getAccount(accountId).printBalance();
        } else {
            System.out.println("Account ID not found.");
        }
    }

    private static void printTransactionHistory(Scanner scanner) {
        System.out.println("Enter account ID to print transaction history: ");
        String accountId = scanner.next();
        if (loggedInUser.getAccounts().containsKey(accountId)) {
            loggedInUser.getAccount(accountId).printTransactionHistory();
        } else {
            System.out.println("Account ID not found.");
        }
    }
}
