import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Feedback {

    public static void submitFeedback(Scanner scanner) throws SQLException {
        System.out.print("Enter Booking ID: ");
        int bookingId = Integer.parseInt(scanner.nextLine());

        String checkBookingQuery = "SELECT passenger_name, train_number, coach_number, seatNumber FROM bookings WHERE booking_id = ?";
        String insertFeedbackQuery = "INSERT INTO feedback (booking_id, name, train_number, coach_number, seatNumber, complaint) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE complaint=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkBookingQuery);
             PreparedStatement stmt = conn.prepareStatement(insertFeedbackQuery)) {

            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String passengerName = rs.getString("passenger_name");
                int trainNumber = rs.getInt("train_number");
                String coachNumber = rs.getString("coach_number");
                int seatNumber = rs.getInt("seatNumber");

                // ✅ Prevent NULL values for coach_number
                if (coachNumber == null || coachNumber.isEmpty()) {
                    coachNumber = "UNKNOWN";  // Set a default value instead of NULL
                }

                System.out.println("Hi " + passengerName + ", please enter your feedback:");
                System.out.print("Enter Your Feedback: ");
                String complaint = scanner.nextLine();

                stmt.setInt(1, bookingId);
                stmt.setString(2, passengerName);
                stmt.setInt(3, trainNumber);
                stmt.setString(4, coachNumber);
                stmt.setInt(5, seatNumber);
                stmt.setString(6, complaint);
                stmt.setString(7, complaint);  // For ON DUPLICATE KEY UPDATE

                stmt.executeUpdate();
                System.out.println("✅ Feedback submitted successfully!");
            } else {
                System.out.println("❌ Booking ID not found. Please enter a valid Booking ID.");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error submitting feedback: " + e.getMessage());
        }
    }
}
