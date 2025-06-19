    package trainbooking;
import java.sql.*;
import java.util.Scanner;

public class TrainBookingSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/TrainBookingDB";
    private static final String USER = "root";
    private static final String PASS = "Pk254565";   

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String choice;

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Welcome to Train Booking System");

            while (true) {
                System.out.println("1. View Available Trains");
                System.out.println("2. Book a Train");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        viewAvailableTrains(connection);
                        break;
                    case "2":
                        bookTrain(scanner, connection);
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewAvailableTrains(Connection connection) throws SQLException {
        String query = "SELECT * FROM trains WHERE available_seats > 0";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Available Trains:");
            while (rs.next()) {
                System.out.println("Train ID: " + rs.getInt(1) +
                                   ", Train Name: " + rs.getString(2) +
                                   ", Source: " + rs.getString(3) +
                                   ", Destination: " + rs.getString(4) +
                                   ", Available Seats: " + rs.getInt(5) +
                                   ", Price: " + rs.getBigDecimal(6));
            }
        }
    }

    private static void bookTrain(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Train ID: ");
        int trainId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Passenger Name: ");
        String passengerName = scanner.nextLine();
        System.out.print("Enter Seat Number: ");
        int seatNumber = Integer.parseInt(scanner.nextLine());

        String selectQuery = "SELECT available_seats FROM trains WHERE train_id = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
        selectStmt.setInt(1, trainId);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int availableSeats = rs.getInt("available_seats");

            if (availableSeats > 0) {
                String insertQuery = "INSERT INTO bookings (train_id, passenger_name, seat_number) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, trainId);
                insertStmt.setString(2, passengerName);
                insertStmt.setInt(3, seatNumber);
                insertStmt.executeUpdate();

                // Decrease available seats
                String updateQuery = "UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, trainId);
                updateStmt.executeUpdate();

                System.out.println("Booking successful for " + passengerName + " on Train ID " + trainId);
            } else {
                System.out.println("No available seats for Train ID " + trainId);
            }
        } else {
            System.out.println("Train not found.");
        }
    }
}