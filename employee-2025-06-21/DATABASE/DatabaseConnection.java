package DATABASE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	 private static final String URL = "jdbc:mysql://localhost:3306/CFSHOP";
	    private static final String USER = "root";
	    private static final String PASSWORD = "15102005";

	    public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    }
}
