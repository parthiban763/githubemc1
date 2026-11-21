package pack;
import java.sql.*;
import java.util.Scanner;

public class BankingSystem {

    static final String URL = "jdbc:mysql://localhost:3306/bankingdb";
    static final String USER = "root";
    static final String PASSWORD = "Parthiban@6";
 
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Create Account
    static void createAccount(Scanner sc) {

        System.out.print("Enter Account Number: ");
        long accountNumber = sc.nextLong();
        sc.nextLine();

        System.out.print("Enter Account Holder Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Initial Deposit: ");
        double balance = sc.nextDouble();

        String sql = "INSERT INTO accounts VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNumber);
            ps.setString(2, name);
            ps.setDouble(3, balance);

            ps.executeUpdate();

            System.out.println("Account created successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Deposit
    static void deposit(Scanner sc) {

        System.out.print("Enter Account Number: ");
        long accountNumber = sc.nextLong();

        System.out.print("Enter Deposit Amount: ");
        double amount = sc.nextDouble();

        String sql = "UPDATE accounts SET balance = balance + ? "
                   + "WHERE account_number = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Money deposited successfully!");
            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Withdraw
    static void withdraw(Scanner sc) {

        System.out.print("Enter Account Number: ");
        long accountNumber = sc.nextLong();

        System.out.print("Enter Withdrawal Amount: ");
        double amount = sc.nextDouble();

        String checkSql =
            "SELECT balance FROM accounts WHERE account_number = ?";

        String withdrawSql =
            "UPDATE accounts SET balance = balance - ? "
          + "WHERE account_number = ?";

        try (Connection con = getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setLong(1, accountNumber);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                double balance = rs.getDouble("balance");

                if (amount <= 0) {
                    System.out.println("Invalid amount.");

                } else if (amount > balance) {
                    System.out.println("Insufficient balance.");

                } else {

                    try (PreparedStatement ps =
                            con.prepareStatement(withdrawSql)) {

                        ps.setDouble(1, amount);
                        ps.setLong(2, accountNumber);

                        ps.executeUpdate();

                        System.out.println("Cash withdrawal successful!");
                    }
                }

            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Check Balance
    static void checkBalance(Scanner sc) {

        System.out.print("Enter Account Number: ");
        long accountNumber = sc.nextLong();

        String sql =
            "SELECT account_holder, balance FROM accounts "
          + "WHERE account_number = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, accountNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("\nAccount Holder: "
                        + rs.getString("account_holder"));

                System.out.println("Account Number: "
                        + accountNumber);

                System.out.println("Available Balance: ₹"
                        + rs.getDouble("balance"));

            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main Menu
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== BANKING SYSTEM =====");
            System.out.println("1. Create Bank Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Cash Withdrawal");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    createAccount(sc);
                    break;

                case 2:
                    deposit(sc);
                    break;

                case 3:
                    withdraw(sc);
                    break;

                case 4:
                    checkBalance(sc);
                    break;

                case 5:
                    System.out.println("Thank you!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
