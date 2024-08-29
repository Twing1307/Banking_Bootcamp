package Banking;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class BankAccount {

	private double balance;
	private LinkedList<String> transactionHistory;

	// Default constructor
	public BankAccount() {
		this.balance = 0.0;
		this.transactionHistory = new LinkedList<>();
		this.transactionHistory.add("Account created with initial balance: 0.0€");
	}

	// Constructor with parameter to set initial balance
	public BankAccount(double initialBalance) {
		this.balance = initialBalance;
		this.transactionHistory = new LinkedList<>();
		this.transactionHistory.add("Account created with initial balance: " + initialBalance + "€");
	}

	// Method to deposit some money into the account
	public void deposit(double amount) {
		if (amount > 0) {
			balance += amount;
			transactionHistory.add("Deposited: " + amount + "€ | New balance: " + balance + "€");
			System.out.println("Deposited: " + amount + "€");
		} else {
			System.out.println("Deposit amount must be positive (>0).");
		}
	}

	// Method to withdraw amount from the account
	public void withdraw(double amount) {
		if (amount > 0 && amount <= balance) {
			balance -= amount;
			transactionHistory.add("Withdrew: " + amount + "€ | New balance: " + balance + "€");
			System.out.println("Withdrew: " + amount + "€");
		} else {
			System.out.println("Withdrawal amount must be positive and less than or equal to balance.");
		}
	}

	// Method to print balance
	public void printBalance() {
		System.out.println("Current balance: " + balance + "€");
	}

	// Method to transfer balance from one bank account to another
	public void transfer(BankAccount otherAccount, double amount) {
		if (amount > 0 && amount <= balance) {
			this.withdraw(amount);
			otherAccount.deposit(amount);
			transactionHistory.add("Transferred: " + amount + "€ to another account | New balance: " + balance + "€");
			System.out.println("Transferred: " + amount + "€");
		} else if (amount > balance) {
			System.out.println("Insufficient funds for transfer.");
		} else {
			System.out.println("Transfer amount must be positive (>0).");
		}
	}

	// Method to print transaction history
	public void printTransactionHistory() {
		System.out.println("Transaction history: ");
		for (String transaction : transactionHistory) {
			System.out.println(transaction);
		}
	}

	// Method to get the current balance
	public double getBalance() {
		return balance;
	}

	// Method to get the transaction history
	public LinkedList<String> getTransactionHistory() {
		return transactionHistory;
	}

	// Method to generate a report of all accounts
	public static void generateReport(HashMap<String, BankAccount> accounts) throws IOException {
		FileWriter writer = new FileWriter("bank_accounts_report.txt");
		writer.write("AccountID, Balance, Transactions\n");
		for (String accountId : accounts.keySet()) {
			BankAccount account = accounts.get(accountId);
			writer.write(accountId + ", " + account.getBalance() + "€\n");
			for (String transaction : account.getTransactionHistory()) {
				writer.write("\t" + transaction + "\n");
			}
			writer.write("\n");
		}
		writer.close();
		System.out.println("Report generated: bank_accounts_report.txt");
	}

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
