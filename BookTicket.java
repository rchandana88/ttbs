import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class BookTicket {
    public static boolean bookTicket(int train_number, String passengerName, String username, String bookingDate,
                                     int fare, int seatChoice, int age, String gender, Scanner scanner)
    {
        // ✅ Ask Gender Only ONCE (Cancel if invalid)
        if (gender == null) {
            System.out.println("❌ Invalid gender! Booking canceled.");
            return false;
        }

        int seatNumber = Reservation.getNextAvailableSeat(train_number);
        if (seatNumber == -1) {
            System.out.println("❌ No available seats on this train.");
            return false;
        }

        String coachNumber = Reservation.assignCoachNumber(seatChoice);
        String trainDetailsQuery = "SELECT train_name, source, destination FROM trains WHERE train_number = ?";
        String trainName = "", source = "", destination = "";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement trainStmt = conn.prepareStatement(trainDetailsQuery)) {
            trainStmt.setInt(1, train_number);
            ResultSet trainRs = trainStmt.executeQuery();
            if (trainRs.next()) {
                trainName = trainRs.getString("train_name");
                source = trainRs.getString("source");
                destination = trainRs.getString("destination");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching train details: " + e.getMessage());
            return false;
        }

        // ✅ Display Ticket Details
        System.out.println("===============================================");
        System.out.println("|            TRAIN TICKET DETAILS             |");
        System.out.println("===============================================");
        System.out.printf("| Passenger Name : %-25s |\n", passengerName);
        System.out.printf("| Age            : %-25d |\n", age);
        System.out.printf("| Gender         : %-25s |\n", gender);
        System.out.printf("| Train Name     : %-25s |\n", trainName);
        System.out.printf("| Train Number   : %-25d |\n", train_number);
        System.out.printf("| Source         : %-25s |\n", source);
        System.out.printf("| Destination    : %-25s |\n", destination);
        System.out.printf("| Travel Date    : %-25s |\n", bookingDate);
        System.out.printf("| Coach Number   : %-25s |\n", coachNumber);
        System.out.printf("| Seat Number    : %-25d |\n", seatNumber);
        System.out.printf("| Fare           : ₹%-24d |\n", fare);
        System.out.println("===============================================");

        // ✅ Ask for Booking Confirmation
        System.out.print("Confirm booking? (Y/N): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("❌ Booking canceled.");
            return false;
        }

        // ✅ Insert Booking Into Database
        String query = "INSERT INTO bookings (train_number, passenger_name, username, booking_date, total_fare, seatNumber, coach_number, age, gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, train_number);
            stmt.setString(2, passengerName);
            stmt.setString(3, username);
            stmt.setString(4, bookingDate);
            stmt.setInt(5, fare);
            stmt.setInt(6, seatNumber);
            stmt.setString(7, coachNumber);
            stmt.setInt(8, age);
            stmt.setString(9, gender);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("\n✅ Your ticket details have been sent to your registered email.");

                // ✅ Retrieve User Email
                String userEmail = Reservation.getUserEmail(username);
                if (userEmail != null) {
                    Reservation.sendBookingEmail(userEmail, passengerName, train_number, seatNumber, coachNumber, bookingDate, fare);
                } else {
                    System.out.println("⚠️ Email not found. Ticket confirmation email could not be sent.");
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error booking ticket: " + e.getMessage());
        }
        return false;
    }
    // ✅ Add this inside BookTicket.java
    public static String getValidGenderInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String gender = scanner.nextLine().trim().toUpperCase(); // Convert input to uppercase

        if (gender.equals("M") || gender.equals("F") || gender.equals("O")) {
            return gender;  // ✅ Valid input, return it
        }

        return null;  // ❌ Invalid input, return null (Booking should be canceled)
    }

    public static String getValidBookingDate(Scanner scanner) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            String bookingDate = scanner.nextLine();

            try {
                LocalDate.parse(bookingDate, dateFormatter);
                return bookingDate; // ✅ Return the valid date if correct
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }



    public static int getValidTrainNumber(Scanner scanner) {
        while (true) {

            String input = scanner.nextLine();

            try {
                int trainNumber = Integer.parseInt(input);

                if (isTrainNumberValid(trainNumber)) {
                    return trainNumber; // ✅ Valid train number
                } else {
                    System.out.println("❌ Invalid train number! Please enter a valid train number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a numeric train number.");
            }
        }
    }

    private static boolean isTrainNumberValid(int trainNumber) {
        String query = "SELECT train_number FROM trains WHERE train_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trainNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // ✅ Returns true if train exists
        } catch (SQLException e) {
            System.out.println("⚠ Database error while checking train number: " + e.getMessage());
        }
        return false;
    }


}
