package pack;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Roombookingsite {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n=================================");
            System.out.println("       ROOM BOOKING SYSTEM");
            System.out.println("=================================");
            System.out.println("1. View All Rooms");
            System.out.println("2. View Available Rooms");
            System.out.println("3. View Room Facilities");
            System.out.println("4. Book a Room");
            System.out.println("5. View Bookings");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    viewAllRooms();
                    break;

                case 2:
                    viewAvailableRooms();
                    break;

                case 3:
                    viewFacilities();
                    break;

                case 4:
                    bookRoom();
                    break;

                case 5:
                    viewBookings();
                    break;

                case 6:
                    cancelBooking();
                    break;

                case 7:
                    System.out.println("Thank you!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // 1. View all rooms
    static void viewAllRooms() {

        String sql = "SELECT * FROM rooms";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== ALL ROOMS ==========");

            while (rs.next()) {

                System.out.println(
                        "Room: " + rs.getInt("room_id")
                        + " | Type: " + rs.getString("room_type")
                        + " | Price: ₹" + rs.getDouble("price")
                        + " | Status: " + rs.getString("status"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View available rooms
    static void viewAvailableRooms() {

        String sql = "SELECT * FROM rooms WHERE status='Available'";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n======= AVAILABLE ROOMS =======");

            while (rs.next()) {

                System.out.println(
                        "Room: " + rs.getInt("room_id")
                        + " | Type: " + rs.getString("room_type")
                        + " | Price: ₹" + rs.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. View facilities
    static void viewFacilities() {

        System.out.print("Enter Room Number: ");
        int roomId = sc.nextInt();

        String sql = "SELECT * FROM rooms WHERE room_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("\nRoom Number: "
                        + rs.getInt("room_id"));

                System.out.println("Room Type: "
                        + rs.getString("room_type"));

                System.out.println("Price: ₹"
                        + rs.getDouble("price"));

                System.out.println("Status: "
                        + rs.getString("status"));

                System.out.println("Facilities: "
                        + rs.getString("facilities"));

            } else {

                System.out.println("Room not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Book room
    static void bookRoom() {

        System.out.print("Enter Room Number: ");
        int roomId = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine();

        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
        String checkIn = sc.nextLine();

        System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
        String checkOut = sc.nextLine();

        try (Connection con = DBConnection.getConnection()) {

            // Check room
            String checkSql =
                    "SELECT * FROM rooms WHERE room_id=? AND status='Available'";

            PreparedStatement check =
                    con.prepareStatement(checkSql);

            check.setInt(1, roomId);

            ResultSet rs = check.executeQuery();

            if (!rs.next()) {

                System.out.println("Room is not available!");
                return;
            }

            double price = rs.getDouble("price");

            LocalDate start = LocalDate.parse(checkIn);
            LocalDate end = LocalDate.parse(checkOut);

            long days = ChronoUnit.DAYS.between(start, end);

            if (days <= 0) {
                System.out.println("Invalid dates!");
                return;
            }

            double total = price * days;

            // Insert booking
            String insertSql =
                    "INSERT INTO bookings " +
                    "(room_id, customer_name, phone, check_in, check_out) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement insert =
                    con.prepareStatement(insertSql);

            insert.setInt(1, roomId);
            insert.setString(2, name);
            insert.setString(3, phone);
            insert.setDate(4, Date.valueOf(checkIn));
            insert.setDate(5, Date.valueOf(checkOut));

            insert.executeUpdate();

            // Update room
            String updateSql =
                    "UPDATE rooms SET status='Booked' WHERE room_id=?";

            PreparedStatement update =
                    con.prepareStatement(updateSql);

            update.setInt(1, roomId);
            update.executeUpdate();

            System.out.println("\nRoom booked successfully!");
            System.out.println("Room Number: " + roomId);
            System.out.println("Customer: " + name);
            System.out.println("Number of Days: " + days);
            System.out.println("Total Amount: ₹" + total);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. View bookings
    static void viewBookings() {

        String sql = "SELECT * FROM bookings";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n========== BOOKINGS ==========");

            while (rs.next()) {

                System.out.println(
                        "Booking ID: " + rs.getInt("booking_id")
                        + " | Room: " + rs.getInt("room_id")
                        + " | Customer: " + rs.getString("customer_name")
                        + " | Phone: " + rs.getString("phone")
                        + " | Check-in: " + rs.getDate("check_in")
                        + " | Check-out: " + rs.getDate("check_out"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. Cancel booking
    static void cancelBooking() {

        System.out.print("Enter Booking ID: ");
        int bookingId = sc.nextInt();

        try (Connection con = DBConnection.getConnection()) {

            // Get room number
            String selectSql =
                    "SELECT room_id FROM bookings WHERE booking_id=?";

            PreparedStatement select =
                    con.prepareStatement(selectSql);

            select.setInt(1, bookingId);

            ResultSet rs = select.executeQuery();

            if (!rs.next()) {

                System.out.println("Booking not found!");
                return;
            }

            int roomId = rs.getInt("room_id");

            // Delete booking
            String deleteSql =
                    "DELETE FROM bookings WHERE booking_id=?";

            PreparedStatement delete =
                    con.prepareStatement(deleteSql);

            delete.setInt(1, bookingId);
            delete.executeUpdate();

            // Make room available
            String updateSql =
                    "UPDATE rooms SET status='Available' WHERE room_id=?";

            PreparedStatement update =
                    con.prepareStatement(updateSql);

            update.setInt(1, roomId);
            update.executeUpdate();

            System.out.println("Booking cancelled successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
