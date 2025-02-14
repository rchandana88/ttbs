import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMyBookings {
    public static void viewMyBookings(String username) {
        String query = "SELECT b.booking_id, b.passenger_name, b.train_number, t.train_name, t.source, t.destination, " +
                "b.booking_date, b.seatNumber, b.coach_number, b.age, b.gender, b.total_fare " +
                "FROM bookings b " +
                "JOIN trains t ON b.train_number = t.train_number " +
                "WHERE b.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nYour Bookings:");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-15s | %-10s | %-20s | %-10s | %-10s | %-12s | %-8s | %-8s | %-10s | %-6s | %-6s | %-6s |\n",
                    "Booking ID", "Passenger", "Train No", "Train Name", "Source", "Destination", "Travel Date", "Coach", "Type", "Seat", "Age", "Gender", "Fare");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            boolean hasBookings = false;
            while (rs.next()) {
                hasBookings = true;

                String coachNumber = rs.getString("coach_number");
                String coachType = Reservation.getCoachType(coachNumber); // ✅ Determine Coach Type

                System.out.printf("| %-10d | %-15s | %-10d | %-20s | %-10s | %-10s  | %-12s | %-8s | %-8s | %-10d | %-6d | %-6s | %-6d |\n",
                        rs.getInt("booking_id"),
                        rs.getString("passenger_name"),
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("booking_date"),
                        coachNumber,
                        coachType, // ✅ New Coach Type column
                        rs.getInt("seatNumber"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getInt("total_fare"));
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            if (!hasBookings) {
                System.out.println("You have no bookings.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
        }
    }
}
