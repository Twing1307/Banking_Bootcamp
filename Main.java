package Banking;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Create a HashMap to store multiple bank accounts with unique IDs
        HashMap<String, BankAccount> accounts = new HashMap<>();

        // Adding two sample accounts
        accounts.put("Account1", new BankAccount());
        accounts.put("Account2", new BankAccount(100.00));

        boolean running = true;
        while (running) {
            System.out.println("\nBank Account:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Print Balance");
            System.out.println("5. Print Transaction History");
            System.out.println("6. Generate Report (.txt file)");
            System.out.println("7. Exit");
            System.out.println("Choose an option:");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter account ID for deposit: ");
                    String depositAccountId = scanner.next();
                    System.out.println("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    if (accounts.containsKey(depositAccountId)) {
                        accounts.get(depositAccountId).deposit(depositAmount);
                    } else {
                        System.out.println("Account ID not found.");
                    }
                    break;
                case 2:
                    System.out.println("Enter account ID for withdrawal: ");
                    String withdrawAccountId = scanner.next();
                    System.out.println("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    if (accounts.containsKey(withdrawAccountId)) {
                        accounts.get(withdrawAccountId).withdraw(withdrawAmount);
                    } else {
                        System.out.println("Account ID not found.");
                    }
                    break;
                case 3:
                    System.out.println("Enter account ID to transfer from: ");
                    String fromAccountId = scanner.next();
                    System.out.println("Enter account ID to transfer to: ");
                    String toAccountId = scanner.next();
                    System.out.println("Enter transfer amount: ");
                    double transferAmount = scanner.nextDouble();
                    if (accounts.containsKey(fromAccountId) && accounts.containsKey(toAccountId)) {
                        accounts.get(fromAccountId).transfer(accounts.get(toAccountId), transferAmount);
                    } else {
                        System.out.println("One or both account IDs not found.");
                    }
                    break;
                case 4:
                    System.out.println("Enter account ID to print balance: ");
                    String balanceAccountId = scanner.next();
                    if (accounts.containsKey(balanceAccountId)) {
                        accounts.get(balanceAccountId).printBalance();
                    } else {
                        System.out.println("Account ID not found.");
                    }
                    break;
                case 5:
                    System.out.println("Enter account ID to print transaction history: ");
                    String historyAccountId = scanner.next();
                    if (accounts.containsKey(historyAccountId)) {
                        accounts.get(historyAccountId).printTransactionHistory();
                    } else {
                        System.out.println("Account ID not found.");
                    }
                    break;
                case 6:
                    BankAccount.generateReport(accounts);
                    break;
                case 7:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
        scanner.close();
    }
}
