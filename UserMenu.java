import java.util.Scanner;

public class UserMenu {
    static void userMenu(Scanner scanner, String username) {
        while (true) {
            try {
                System.out.println("\n===== User Menu =====");
                System.out.println("1. View Trains");
                System.out.println("2. Book a Ticket");
                System.out.println("3. View My Bookings");
                System.out.println("4. Cancel a Ticket");
                System.out.println("5. Submit Feedback");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter Source: ");
                        String source = scanner.nextLine();
                        System.out.print("Enter Destination: ");
                        String destination = scanner.nextLine();
                        System.out.print("Enter Travel Date (YYYY-MM-DD): ");
                        String travelDate = scanner.nextLine();
                        ViewTrains.viewTrains(source, destination, travelDate);
                        break;
                    case 2:
                        System.out.print("Enter Train Number: ");
                        int trainNumber = BookTicket.getValidTrainNumber(scanner);
                        System.out.print("Enter Passenger Name: ");
                        String passengerName = scanner.nextLine();
                        System.out.print("Enter Travel Date (YYYY-MM-DD): ");
                        String bookingDate = BookTicket.getValidBookingDate(scanner);
                        System.out.print("Enter Seat Type (1-Sleeper, 2-AC, 3-General): ");
                        int seatChoice = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Age: ");
                        int age = Integer.parseInt(scanner.nextLine());

                        // ✅ Keep asking until user enters 'M', 'F', or 'O'
                        String gender;
                        while (true) {
                            System.out.print("Enter Gender (M/F/O): ");
                            gender = scanner.nextLine().trim().toUpperCase();
                            if (gender.equals("M") || gender.equals("F") || gender.equals("O")) {
                                break; // ✅ Valid gender, exit loop
                            }
                            System.out.println("❌ Invalid gender! Please enter 'M', 'F', or 'O'.");
                        }

                        double fare = Reservation.getFareFromDatabase(trainNumber, seatChoice);
                        if (fare != -1) {
                            boolean success = BookTicket.bookTicket(trainNumber, passengerName, username, bookingDate,
                                    (int) Math.round(fare), seatChoice, age, gender, scanner);
                            System.out.println(success ? "✅ Ticket booked successfully!" : "❌ Booking failed. Try again.");
                        }
                        break;
                    case 3:
                        ViewMyBookings.viewMyBookings(username);
                        break;
                    case 4:
                        System.out.print("Enter Booking ID to cancel: ");
                        int bookingId = Integer.parseInt(scanner.nextLine());
                        CancelTicket.cancelTicket(bookingId, username);
                        break;
                    case 5:
                        Feedback.submitFeedback(scanner);
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("❌ Invalid Choice! Try Again.");
                }
            } catch (Exception e) {
                System.out.println("⚠ An error occurred: " + e.getMessage());
            }
        }
    }
}