package obj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {
    // ✅ SỬA - URL database mới
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coffee_store";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; // Thay đổi theo config của bạn
    
    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            // Test connection
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Database connected successfully to: " + DB_URL);
                return connection;
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Method kiểm tra kết nối
    public static boolean testConnection() {
        try (Connection conn = connectDB()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method để đóng connection an toàn
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ Database connection closed");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}