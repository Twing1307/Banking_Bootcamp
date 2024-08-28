package Banking.Banking_Bootcamp;

import java.util.Scanner;

public class BankAccount {
	private double balance;

	// Default constructor
	public BankAccount() {
		this.balance = 0.0;
	}

	// Contructor with parameter to set initial balance
	public BankAccount(double initialBalance) {
		this.balance = initialBalance;
	}

	// Method to deposit some money into the account
	public void deposit(double amount) {
		if (amount > 0) {
			balance += amount;
			System.out.println("Deposited: " + amount + "€");
		} else {
			System.out.println("Deposit amount must be positive (>0).");
		}
	}

		// Method to Withdraw amount from the account
	public void withdraw(double amount) {
		if (amount > 0 && amount <= balance) {
			balance -= amount;
			System.out.println("Withdrew: " + amount + "€");
		} else {
			System.out.println("Withdrawal amount must be posivite (>0)");
		}
	}

		// Method to print balance
	public void printBalance(){
		System.out.println("Current balance: " + balance + "€");
	}

		// Method to transfer balance from one bank account to another
	public void transfer(BankAccount otherAccount, double amount) {
		if (amount > 0 && amount <= balance) {
			this.withdraw(amount);
			otherAccount.deposit(amount);
			System.out.println("Transfered: " + amount + "€");
		} else if (amount > balance) {
			System.out.println("Insufficient funds for transfer.");
		} else {
			System.out.println("Transfer amount must be positive (>0)");
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Create two bank accounts
		BankAccount account1 = new BankAccount(); // empty account
		BankAccount account2 = new BankAccount(100.00); // some money

		boolean running = true;
		while (running) {
			System.out.println("\nBank Account:");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw");
			System.out.println("3. Transfer");
			System.out.println("4. Print Balance");
			System.out.println("Choose an option:");

			int choise = scanner.nextInt();

			switch (choise) {
				case 1:
					System.out.println("Enter deposit money: ");
					double depositAmount = scanner.nextDouble();
					scanner.nextLine();
					account1.deposit(depositAmount);
					break;
				case 2:
					System.out.println("Enter withdrawal amount: ");
					double withdrawAmount = scanner.nextDouble();
					scanner.nextLine();
					account1.withdraw(withdrawAmount);
					break;
				case 3:
					System.out.println("Enter transfer amount: ");
					double transferAmount = scanner.nextDouble();
					scanner.nextLine();
					account1.transfer(account2, transferAmount);
					break;
				case 4:
					System.out.println("Account 1 balance is : ");
					account1.printBalance();
					System.out.println("Account 2 balance is : ");
					account2.printBalance();
					scanner.nextLine();
				case 5:
					running = false;
					System.out.println("Exiting...");
					break;
				default:
					System.out.println("Invalid option. Please try again");
					break;
			}
		}
		scanner.close();
	}
}

