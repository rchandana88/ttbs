import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Reservation {

    public static double getFareFromDatabase(int trainNumber, int seatChoice) {
        double fare = -1;
        String query = "SELECT general_fare, ac_fare, sleeper_fare FROM trains WHERE train_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, trainNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                switch (seatChoice) {
                    case 1:
                        fare = rs.getDouble("sleeper_fare");
                        break;
                    case 2:
                        fare = rs.getDouble("ac_fare");
                        break;
                    case 3:
                        fare = rs.getDouble("general_fare");
                        break;
                    default:
                        System.out.println("Invalid seat choice!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching fare from database: " + e.getMessage());
        }
        return fare;
    }


    static int getNextAvailableSeat(int trainNumber) {
        String query = "SELECT COALESCE(MAX(seatNumber), 0) + 1 AS next_seat FROM bookings WHERE train_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, trainNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("next_seat");
            }
        } catch (SQLException e) {
            System.out.println("Error finding available seat: " + e.getMessage());
        }
        return -1;
    }

    static String assignCoachNumber(int seatChoice) {
        Random rand = new Random();
        return seatChoice == 1 ? "S" + (rand.nextInt(9) + 1) :
                seatChoice == 2 ? "E" + (rand.nextInt(5) + 1) :
                        (rand.nextBoolean() ? "U" : "L") + (rand.nextInt(2) + 1);
    }

    static String getCoachType(String coachNumber) {
        if (coachNumber == null || coachNumber.isEmpty()) {
            return "Unknown"; // ✅ Handle NULL or empty values safely
        }

        if (coachNumber.startsWith("S")) {
            return "Sleeper";
        } else if (coachNumber.startsWith("E")) {
            return "AC";
        } else if (coachNumber.startsWith("U") || coachNumber.startsWith("L")) {
            return "General";
        }
        return "Unknown";
    }

    static String getUserEmail(String username) {
        String query = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user email: " + e.getMessage());
        }
        return null;
    }

    static void sendBookingEmail(String email, String passengerName, int train_number, int seatNumber,
                                 String coachNumber, String bookingDate, int fare) {

        // ✅ Initialize variables
        String train_name = "N/A", source = "N/A", destination = "N/A";

        // ✅ Check if 'source' and 'destination' exist in 'trains' table
        String query = "SELECT train_name, source, destination FROM trains WHERE train_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, train_number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                train_name = rs.getString("train_name");
                source = rs.getString("source");
                destination = rs.getString("destination");

                // ✅ Debugging Output
                System.out.println("✔ Train Details Found: " + train_name + " | " + source + " → " + destination);
            } else {
                System.out.println("❌ No train found with train_number: " + train_number);
            }
        } catch (SQLException e) {
            System.out.println("⚠ Database Error: " + e.getMessage());
        }

        // ✅ Prevent Null Values
        coachNumber = (coachNumber != null) ? coachNumber : "N/A";
        bookingDate = (bookingDate != null) ? bookingDate : "N/A";

        // ✅ Updated Email Content
        String subject = "RAIL RESERVE XPRESS - BOOKING CONFIRMATION";
        String messageBody = String.format(
                "Dear %s,\n\n"
                        + "Your ticket has been successfully booked!\n\n"
                        + " *Ticket Details:*\n"
                        + "──────────────────────────────────\n"
                        + " Train Name: %s\n"
                        + " Train Number: %d\n"
                        + " Route: %s → %s\n"
                        + " Travel Date: %s\n"
                        + " Coach Number: %s\n"
                        + " Seat Number: %d\n"
                        + " Fare: ₹%d\n"
                        + "──────────────────────────────────\n\n"
                        + "Thank you for using our Train Ticket Reservation System.\n"
                        + "Safe travels! \n",
                passengerName, train_name, train_number, source, destination, bookingDate, coachNumber, seatNumber, fare
        );

        UserAuthentication.sendEmail(email, subject, messageBody);
    }



}
