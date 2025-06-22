package obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Controller.DatabaseConnection;

public class Store_CoffeeShop {

	private static final int maxProductsInMenu = 50;
	private ArrayList<Product> itemsInStore = new ArrayList<>();
	private int countMedia = 0;
	
	public ArrayList<Product> getItemsInStore(){
		
		return itemsInStore;
	}
	
	public void loadProductsFromDB() {
		itemsInStore.clear();
        String query = "SELECT * FROM Products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("product_name"),
                    rs.getInt("price"),
                    rs.getString("note"),
                    rs.getString("category"),
                    rs.getInt("stock")
                );
                product.setId(rs.getInt("product_id"));
                
                product.setNote(rs.getString("note"));
                
                String imgPath = rs.getString("img_path");
                product.setImgPath(imgPath);
                
                itemsInStore.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
