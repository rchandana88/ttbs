import java.sql.*;
import java.util.Random;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;

public class UserAuthentication {

    // ✅ User Registration with OTP Verification
    public static boolean register(Scanner scanner) {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        // Validate Contact Number (Must be exactly 10 digits)
        System.out.print("Enter Contact Number: ");
        String contact = scanner.nextLine();
        if (!contact.matches("\\d{10}")) {
            System.out.println(" Invalid Contact Number! It must be exactly 10 digits.");
            return false;
        }

        // Validate Email Format
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println(" Invalid Email Format! Please enter a valid email (e.g., user@example.com).");
            return false;
        }

        // Generate OTP and send to user's email
        String otp = generateOTP();
        sendEmail(email, "Rail Reserve Xpress - OTP Verification",
                "Dear " + username + ",\n\nYour OTP for email verification is: " + otp + "\n\n"
                        + "Enter this OTP to complete your registration.\n\nBest Regards,\nRail Reserve Xpress Team");

        // Verify OTP
        System.out.print("Enter the OTP sent to your email: ");
        String enteredOTP = scanner.nextLine();

        if (!enteredOTP.equals(otp)) {
            System.out.println(" Incorrect OTP! Email verification failed.");
            return false;
        }

        System.out.println("✅ Email verified successfully!");

        // Ask for Password & Confirmation
        String password, confirmPassword;
        while (true) {
            System.out.print("Choose a secure password: ");
            password = scanner.nextLine();

            if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$")) {
                System.out.println(" Weak password! Must be at least 5 characters long and contain both letters and numbers.");
                continue;
            }

            System.out.print("Confirm your password: ");
            confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println(" Passwords do not match! Try again.");
            } else {
                break;
            }
        }

        // Insert user into database
        String checkQuery = "SELECT * FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password, email, contact) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println(" Username already exists! Try another one.");
                return false;
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, contact);
            insertStmt.executeUpdate();

            System.out.println("✅ Registration successful! Welcome aboard.");

            // Send Confirmation Email
            sendEmail(email, "Welcome to Rail Reserve Xpress!",
                    "Dear " + username + ",\n\nYour account has been successfully created!\n\n"
                            + "Username: " + username + "\n\nHappy travels! 🚆\n\nBest Regards,\nRail Reserve Xpress Team");

            return true;
        } catch (SQLException e) {
            System.out.println("⚠️ Error registering user: " + e.getMessage());
            return false;
        }
    }

    // ✅ User Login (Using Email or Contact)
    public static String userLogin(Scanner scanner) {
        System.out.print("Enter Email or Contact Number: ");
        String userInput = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        String loginQuery = "SELECT username FROM users WHERE (email = ? OR contact = ?) AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(loginQuery)) {

            stmt.setString(1, userInput);
            stmt.setString(2, userInput);
            stmt.setString(3, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println(" Login Successful! Welcome, " + rs.getString("username") + "\n");
                return rs.getString("username");
            } else {
                System.out.println(" Login Failed! Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println(" Error during login: " + e.getMessage());
        }
        return null;
    }

    // ✅ Forgot Password (OTP Verification & Reset)
    public static boolean forgotPassword(Scanner scanner) {
        System.out.print("Enter your registered Email: ");
        String email = scanner.nextLine();

        // Step 1: Verify Email Exists
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Email not found! Please enter a registered email.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error checking email: " + e.getMessage());
            return false;
        }

        // Step 2: Send OTP
        String otp = generateOTP(); // 🔹 OTP is generated but NOT printed in the console

        boolean emailSent = sendEmail(email, "Rail Reserve Xpress - Password Reset OTP",
                "Dear User,\n\nYour OTP for password reset is: " + otp + "\n\n"
                        + "Enter this OTP to reset your password.\n\nBest Regards,\nRail Reserve Xpress Team");

        if (!emailSent) {
            System.out.println("❌ Failed to send OTP. Try again later.");
            return false;
        }

        System.out.print("Enter the OTP sent to your email: ");
        String enteredOTP = scanner.nextLine();

        if (!enteredOTP.equals(otp)) {
            System.out.println("❌ Invalid OTP. Password reset failed.");
            return false;
        }

        // Step 3: Allow User to Reset Password
        System.out.println("✅ OTP Verified! Please set a new password.");
        String newPassword, confirmPassword;
        while (true) {
            System.out.print("Enter new password: ");
            newPassword = scanner.nextLine();

            if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$")) {
                System.out.println("❌ Weak password! Must be at least 5 characters long and contain both letters and numbers.");
                continue;
            }

            System.out.print("Confirm new password: ");
            confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ Passwords do not match! Try again.");
            } else {
                break;
            }
        }

        if (resetPassword(email, newPassword)) {
            System.out.println("✅ Your password has been changed. Now login again.");
            return true;
        } else {
            System.out.println("❌ Password reset failed. Try again later.");
            return false;
        }
    }


    public static boolean resetPassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error updating password: " + e.getMessage());
        }
        return false;
    }


    // OTP Generation
    public static String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // Send Email
    public static boolean sendEmail(String email, String subject, String messageBody) {
        final String senderEmail = "railreservexpress@gmail.com";  // Replace with your Gmail
        final String senderPassword = "gaayzkixqjpdwbgm";  // Use 16-character Gmail App Password

        String host = "smtp.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("📧 Email sent successfully to: " + email);
            return true;
        } catch (MessagingException e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
