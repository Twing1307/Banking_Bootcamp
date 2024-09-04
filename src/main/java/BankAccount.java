import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class BankAccount {
    private double balance;
    private LinkedList<String> transactionHistory;

    private static final String INITIAL_BALANCE_MESSAGE = "Account created with initial balance: %.2f€";

    public BankAccount() {
        this(0.0);
    }

    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new LinkedList<>();
        logTransaction(String.format(INITIAL_BALANCE_MESSAGE, initialBalance));
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            logTransaction(String.format("Deposited: %.2f€ | New balance: %.2f€", amount, balance));
        } else {
            System.out.println("Deposit amount must be positive (>0).");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            logTransaction(String.format("Withdrew: %.2f€ | New balance: %.2f€", amount, balance));
        } else {
            System.out.println("Withdrawal amount must be positive and less than or equal to balance.");
        }
    }

    public void transfer(BankAccount otherAccount, double amount) {
        if (amount > 0 && amount <= balance) {
            this.withdraw(amount);
            otherAccount.deposit(amount);
            logTransaction(String.format("Transferred: %.2f€ to another account | New balance: %.2f€", amount, balance));
        } else if (amount > balance) {
            System.out.println("Insufficient funds for transfer.");
        } else {
            System.out.println("Transfer amount must be positive (>0).");
        }
    }

    public void printBalance() {
        System.out.println("Current balance: " + balance + "€");
    }

    public void displayTransactionHistory() {
        System.out.println("Transaction history: ");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    public double getBalance() {
        return balance;
    }

    public LinkedList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public static void generateReport(HashMap<String, BankAccount> accounts) throws IOException {
        try (FileWriter writer = new FileWriter("bank_accounts_report.txt")) {
            writer.write("AccountID, Balance, Transactions\n");
            for (String accountId : accounts.keySet()) {
                BankAccount account = accounts.get(accountId);
                writer.write(accountId + ", " + account.getBalance() + "€\n");
                for (String transaction : account.getTransactionHistory()) {
                    writer.write("\t" + transaction + "\n");
                }
                writer.write("\n");
            }
        }
        System.out.println("Report generated: bank_accounts_report.txt");
    }

    private void logTransaction(String message) {
        transactionHistory.add(message);
    }
}
