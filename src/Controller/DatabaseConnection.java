package Controller;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    // Database configuration
    private static String DB_HOST ;
    private static String DB_PORT;
    private static String DB_NAME ;
    private static String DB_USERNAME;
    private static String DB_PASSWORD; // Change this to your MySQL password



    static {
        try (InputStream input = PaymentAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            DB_HOST = prop.getProperty("db.host");
            DB_PORT = prop.getProperty("db.port");
            DB_NAME = prop.getProperty("db.name");
            DB_USERNAME = prop.getProperty("db.username");
            DB_PASSWORD = prop.getProperty("db.password");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // Connection URL
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    // Single connection instance
    private static Connection connection = null;

    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Set connection properties
                Properties props = new Properties();
                props.setProperty("user", DB_USERNAME);
                props.setProperty("password", DB_PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("allowPublicKeyRetrieval", "true");
                props.setProperty("serverTimezone", "UTC");
                props.setProperty("characterEncoding", "utf8");

                // Create connection
                connection = DriverManager.getConnection(DB_URL, props);
                System.out.println("Database connected successfully!");

            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                throw new SQLException("Driver not found", e);
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connection
     */
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection test successful!");
                System.out.println("Database: " + DB_NAME);
                System.out.println("Host: " + DB_HOST + ":" + DB_PORT);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed:");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\n📝 Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database '" + DB_NAME + "' exists");
            System.err.println("3. Username/password are correct");
            System.err.println("4. MySQL connector JAR is in lib folder");
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        testConnection();
        closeConnection();
    }
}