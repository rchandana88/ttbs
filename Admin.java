import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {

    public static void addTrain(int train_number, String train_name, String source, String destination,
                                int seats_available, String arrival_time, String arrival_date,
                                int general_fare, int ac_fare, int sleeper_fare) {
        String query = "INSERT INTO trains (train_number, train_name, source, destination, seats_available, " +
                "arrival_time, arrival_date, general_fare, ac_fare, sleeper_fare) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, train_number);
            stmt.setString(2, train_name);
            stmt.setString(3, source);
            stmt.setString(4, destination);
            stmt.setInt(5, seats_available);
            stmt.setString(6, arrival_time);
            stmt.setString(7, arrival_date);
            stmt.setInt(8, general_fare);
            stmt.setInt(9, ac_fare);
            stmt.setInt(10, sleeper_fare);

            stmt.executeUpdate();
            System.out.println("Train added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding train: " + e.getMessage());
        }
    }

    public static void viewUsers() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter date (YYYY-MM-DD): ");
        String bookingDate = scanner.nextLine();

        String query = "SELECT u.username, u.email, u.contact, " +
                "b.booking_id, b.passenger_name, b.train_number, " +
                "t.train_name, t.source, t.destination, " +
                "b.booking_date, b.seatNumber, b.coach_number, " +
                "b.age, b.gender, b.total_fare " +
                "FROM users u " +
                "LEFT JOIN bookings b ON u.username = b.username " +
                "LEFT JOIN trains t ON b.train_number = t.train_number " +
                "WHERE b.booking_date = ? OR b.booking_date IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookingDate);
            ResultSet rs = stmt.executeQuery();

            String border = "+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
            System.out.println(border);
            System.out.printf("| %-15s | %-25s | %-15s | %-10s | %-15s | %-10s | %-20s | %-10s | %-12s | %-12s | %-6s | %-6s | %-6s  | %-4s | %-6s | %-6s |\n",
                    "Username", "Email", "Contact", "Booking ID", "Passenger name", "Train No", "Train Name", "Source", "Destination", "Travel Date", "Seat", "Coach", "Type", "Age", "Gender", "Fare");
            System.out.println(border);

            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                String username = rs.getString("username");
                String email = rs.getString("email");
                String contactNumber = rs.getString("contact");
                Integer bookingId = rs.getObject("booking_id") != null ? rs.getInt("booking_id") : null;
                String passengerName = rs.getString("passenger_name");
                Integer trainNumber = rs.getObject("train_number") != null ? rs.getInt("train_number") : null;
                String trainName = rs.getString("train_name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                String travelDate = rs.getString("booking_date");
                Integer seatNumber = rs.getObject("seatNumber") != null ? rs.getInt("seatNumber") : null;
                String coachNumber = rs.getString("coach_number");
                String coachType = Reservation.getCoachType(coachNumber);
                Integer age = rs.getObject("age") != null ? rs.getInt("age") : null;
                String gender = rs.getString("gender");
                Integer totalFare = rs.getObject("total_fare") != null ? rs.getInt("total_fare") : null;

                System.out.printf("| %-15s | %-25s | %-15s | %-10s | %-15s | %-10s | %-20s | %-10s | %-12s | %-12s | %-6s | %-6s | %-6s | %-4s | %-6s | %-6s |\n",
                        username,
                        email,
                        contactNumber,
                        (bookingId != null ? bookingId : "N/A"),
                        (passengerName != null ? passengerName : "N/A"),
                        (trainNumber != null ? trainNumber : "N/A"),
                        (trainName != null ? trainName : "N/A"),
                        (source != null ? source : "N/A"),
                        (destination != null ? destination : "N/A"),
                        (travelDate != null ? travelDate : "N/A"),
                        (seatNumber != null ? seatNumber : "N/A"),
                        (coachNumber != null ? coachNumber : "N/A"),
                        (coachType != null ? coachType : "N/A"),
                        (age != null ? age : "N/A"),
                        (gender != null ? gender : "N/A"),
                        (totalFare != null ? "₹" + totalFare : "N/A"));
            }

            System.out.println(border);

            if (!hasUsers) {
                System.out.println("| No users or bookings found for the selected date.                                                                                                                 |");
                System.out.println(border);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user details: " + e.getMessage());
        }
    }


    public static void listAllTrains() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter  date (YYYY-MM-DD): ");
            String travelDate = scanner.nextLine();

            String query = "SELECT * FROM trains WHERE arrival_date = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, travelDate);
                ResultSet rs = stmt.executeQuery();

                String borderLine = "+-------------------------------------------------------------------------------------+";

                System.out.println(borderLine);
                System.out.println("|                   List of Trains Available                                          |");
                System.out.println(borderLine);

                System.out.printf("| %-12s | %-20s | %-10s | %-12s | %-15s   |\n",
                        "Train Number", "Train Name", "Source", "Destination", "Seats Available");
                System.out.println(borderLine);

                boolean hasTrains = false;
                while (rs.next()) {
                    hasTrains = true;
                    String trainName = rs.getString("train_name");
                    String trainNameFormatted = (trainName.length() > 18) ? trainName.substring(0, 18) : trainName;

                    System.out.printf("| %-12d | %-20s | %-10s | %-12s | %-15d   |\n",
                            rs.getInt("train_number"),
                            trainNameFormatted,
                            rs.getString("source"),
                            rs.getString("destination"),
                            rs.getInt("seats_available"));
                }

                System.out.println(borderLine);
                if (!hasTrains) {
                    System.out.println("No trains found for the selected date.");
                }
            } catch (SQLException e) {
                System.out.println("Error listing trains: " + e.getMessage());
            }
        }
    public static void viewFeedback() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter  date (YYYY-MM-DD): ");
        String bookingDate = scanner.nextLine();

        String query = "SELECT f.booking_id, b.passenger_name, f.train_number, f.coach_number, f.seatNumber, f.complaint, b.booking_date " +
                "FROM feedback f " +
                "JOIN bookings b ON f.booking_id = b.booking_id " +
                "WHERE b.booking_date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookingDate);
            ResultSet rs = stmt.executeQuery();

            System.out.println("-----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-15s | %-10s | %-10s | %-10s | %-30s | %-12s |\n",
                    "Booking ID", "Passenger", "Train No", "Coach", "Seat No", "Complaint", "Booking Date");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------");

            boolean hasFeedback = false;
            while (rs.next()) {
                hasFeedback = true;
                System.out.printf("| %-10d | %-15s | %-10d | %-10s | %-10d | %-30s | %-12s |\n",
                        rs.getInt("booking_id"),
                        rs.getString("passenger_name"),
                        rs.getInt("train_number"),
                        rs.getString("coach_number"),
                        rs.getInt("seatNumber"),
                        rs.getString("complaint"),
                        rs.getString("booking_date"));
            }

            System.out.println("-----------------------------------------------------------------------------------------------------------------------");
            if (!hasFeedback) {
                System.out.println("No feedback found for the selected booking date.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching feedback details. Please try again.");
        }
    }
}
