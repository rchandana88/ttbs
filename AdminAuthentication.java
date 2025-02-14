import java.util.Scanner;
    public class AdminAuthentication {
        public static boolean login(String username, String password) {
            return username.equals("admin") && password.equals("admin123");
        }
    }
