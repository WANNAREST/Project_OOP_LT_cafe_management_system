package obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import DATABASE.DatabaseConnection;

public class Store_CoffeeShop {

	private static final int maxProductsInMenu = 50;
	private ArrayList<Products> itemsInStore = new ArrayList<>();
	private int countMedia = 0;
	
	public ArrayList<Products> getItemsInStore(){
		
		return itemsInStore;
	}
	
	public void loadProductsFromDB() {
		itemsInStore.clear();
        String query = "SELECT * FROM Products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Products product = new Products(
                    rs.getString("product_name"),
                    rs.getFloat("price"),
                    rs.getString("note"), // description
                    rs.getString("category"),
                    rs.getInt("stock")
                );
                product.setId(rs.getInt("product_id"));
                itemsInStore.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
