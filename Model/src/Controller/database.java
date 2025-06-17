package Controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class database {
	  public static Connection connectDB() {
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            Connection connect= DriverManager.getConnection("jdbc:mysql://localhost/cafe", "root", "");
	          //  System.out.println("Successfully connected to database");
	            return connect;
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Cannot connect to database");
	        }
	        return null;
	 }
	  
}
