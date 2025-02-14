import java.util.Scanner;

public class TrainTicketReservation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String username = "";

        while (true) {
            try {
                System.out.println("\n===== Train Ticket Reservation System =====");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Admin Login");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 4) {
                    System.out.println("Exiting... Thank you for using our system!");
                    return;
                }

                if (choice == 2) { // Register (With OTP Verification)
                    if (UserAuthentication.register(scanner)) {
                        System.out.println("✅ Registration successful!");
                    } else {
                        System.out.println("❌ Registration failed! Try again.");
                    }
                } else if (choice == 1) { // User Login
                    username = UserAuthentication.userLogin(scanner);

                    if (username != null) {
                        System.out.println("✅ Login Successful!\n");
                        UserMenu.userMenu(scanner, username);
                    } else {
                        System.out.println("❌ Login Failed! Invalid credentials.");
                        System.out.print("Forgot Password? (Y/N): ");
                        String forgotPassword = scanner.nextLine();

                        if (forgotPassword.equalsIgnoreCase("Y")) {
                            UserAuthentication.forgotPassword(scanner);
                        }
                    }
                } else if (choice == 3) { // Admin Login
                    System.out.print("Enter Admin Username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter Admin Password: ");
                    String password = scanner.nextLine();

                    if (AdminAuthentication.login(username, password)) {
                        System.out.println("✅ Admin Login Successful!");
                        adminMenu(scanner);
                    } else {
                        System.out.println("❌ Admin Login Failed! Try again.");
                    }
                } else {
                    System.out.println("❌ Invalid choice! Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
    }

    private static void adminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View All Users");
            System.out.println("2. View Trains");
            System.out.println("3. Add Trains");
            System.out.println("4. View Feedback");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    Admin.viewUsers();
                    break;
                case 2:
                    Admin.listAllTrains();
                    break;
                case 3:
                    System.out.print("Enter Train Number: ");
                    int trainNumber = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Train Name: ");
                    String trainName = scanner.nextLine();
                    System.out.print("Enter Source: ");
                    String source = scanner.nextLine();
                    System.out.print("Enter Destination: ");
                    String destination = scanner.nextLine();
                    System.out.print("Enter Seats Available: ");
                    int seatsAvailable = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Arrival Time: ");
                    String arrivalTime = scanner.nextLine();
                    System.out.print("Enter Arrival Date (YYYY-MM-DD): ");
                    String arrivalDate = scanner.nextLine();
                    System.out.print("Enter General Fare: ");
                    int generalFare = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter AC Fare: ");
                    int acFare = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Sleeper Fare: ");
                    int sleeperFare = Integer.parseInt(scanner.nextLine());

                    Admin.addTrain(trainNumber, trainName, source, destination, seatsAvailable, arrivalTime, arrivalDate, generalFare, acFare, sleeperFare);
                    break;
                case 4:
                    Admin.viewFeedback();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("❌ Invalid Choice! Try Again.");
            }
        }
    }
}
