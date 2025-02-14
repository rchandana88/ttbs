import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewTrains {
    public static void viewTrains(String source, String destination, String travelDate) {
        String query = "SELECT train_number, train_name, source, destination, arrival_date, arrival_time, seats_available FROM trains WHERE source = ? AND destination = ? AND arrival_date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setString(3, travelDate);

            ResultSet rs = stmt.executeQuery();
            System.out.println("\nAvailable Trains:");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-12s | %-10s | %-10s |\n", "Train No", "Train Name", "Source", "Destination", "Travel Date", "Time", "Seats");
            System.out.println("------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("| %-10d | %-20s | %-15s | %-15s | %-12s | %-10s | %-10d |\n",
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("arrival_date"),
                        rs.getString("arrival_time"),
                        rs.getInt("seats_available"));
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------");
        } catch (Exception e) {
            System.out.println("Error fetching trains. Please try again.");
        }
    }

}
