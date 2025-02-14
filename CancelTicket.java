import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CancelTicket {
    public static void cancelTicket(int bookingId, String username) {
        String checkQuery = "SELECT train_number, passenger_name, booking_date, seatNumber, coach_number, total_fare FROM bookings WHERE booking_id = ? AND username = ?";
        String deleteQuery = "DELETE FROM bookings WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            checkStmt.setInt(1, bookingId);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Retrieve booking details before deletion
                int trainNumber = rs.getInt("train_number");
                String passengerName = rs.getString("passenger_name");
                String bookingDate = rs.getString("booking_date");
                int seatNumber = rs.getInt("seatNumber");
                String coachNumber = rs.getString("coach_number");
                int totalFare = rs.getInt("total_fare");

                deleteStmt.setInt(1, bookingId);
                int rowsDeleted = deleteStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Ticket cancellation successful!");

                    // Fetch user's email
                    String userEmail = Reservation.getUserEmail(username);
                    if (userEmail != null) {
                        sendCancellationEmail(userEmail, passengerName, trainNumber, seatNumber, coachNumber, bookingDate, totalFare);
                    }
                }
            } else {
                System.out.println("Booking not found or does not belong to you.");
            }
        } catch (SQLException e) {
            System.out.println("Error canceling ticket: " + e.getMessage());
        }
    }

    private static void sendCancellationEmail(String email, String passengerName, int trainNumber, int seatNumber, String coachNumber, String bookingDate, int totalFare) {
        String subject = "RAIL RESERVE XPRESS - TICKET CANCELLATION";
        String messageBody = "Dear " + passengerName + ",\n\n"
                + "Your ticket has been successfully cancelled.\n\n"
                + "Train Number: " + trainNumber + "\n"
                + "Seat Number: " + seatNumber + "\n"
                + "Coach Number: " + coachNumber + "\n"
                + "Travel Date: " + bookingDate + "\n"
                + "Fare Refunded: ₹" + totalFare + "\n\n"
                + "We hope to serve you again in the future.\n"
                + "Safe travels!\n";

        UserAuthentication.sendEmail(email, subject, messageBody);
    }

}
