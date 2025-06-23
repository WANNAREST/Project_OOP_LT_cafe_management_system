package Controller;

import obj.Coffee;
import obj.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /**
     * Get all products from database
     * Since Product is abstract, we'll create Coffee instances for coffee category
     * and you can extend this for other product types
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products ORDER BY product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category = ? ORDER BY product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
        }
        return products;
    }

    /**
     * Get products that are in stock
     */
    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE stock > 0 ORDER BY product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                if (product != null && product.isAvailable()) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Get product by ID
     */
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM Products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createProductFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
        }
        return null;
    }

    /**
     * Search products by name
     */
    public List<Product> searchProductsByName(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE product_name LIKE ? ORDER BY product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Get all unique categories
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM Products WHERE category IS NOT NULL ORDER BY category";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Update product stock in database
     */
    public boolean updateProductStock(int productId, int newStock) {
        String sql = "UPDATE Products SET stock = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reduce product stock (for orders)
     */
    public boolean reduceProductStock(int productId, int quantity) {
        String sql = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error reducing product stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create Product instance from ResultSet
     * Since Product is abstract, we create concrete implementations based on category
     */
    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("product_id");
        String name = rs.getString("product_name");
        String category = rs.getString("category");
        int price = rs.getInt("price");
        int stock = rs.getInt("stock");
        String note = rs.getString("note");
        String imgPath = rs.getString("img_path"); // Get image path from database

        // Create appropriate concrete Product based on category
        Product product;
        if ("Coffee".equalsIgnoreCase(category)) {
            product = new Coffee(id, name, category, price, note);
        } else {
            // For other categories, you can create other concrete classes
            // For now, we'll use Coffee as a general product (you might want to create a GeneralProduct class)
            product = new Coffee(id, name, category, price, note);
        }

        // Set the stock quantity
        product.setQuantity(stock);

        // Set the image path from database
        product.setImgPath(imgPath);

        return product;
    }

    /**
     * Helper method to format price in VND
     */
    public static String formatPrice(int price) {
        return String.format("%,d VND", price);
    }

    /**
     * Update all image paths from "images/" to "img/" in the database
     */
    public boolean updateImagePaths() {
        String sql = "UPDATE Products SET img_path = REPLACE(img_path, 'images/', 'img/') WHERE img_path LIKE 'images/%'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println(" Updated " + rowsAffected + " image paths from 'images/' to 'img/'");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating image paths: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a specific product's image path
     */
    public boolean updateProductImagePath(int productId, String newImagePath) {
        String sql = "UPDATE Products SET img_path = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newImagePath);
            stmt.setInt(2, productId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product image path: " + e.getMessage());
            return false;
        }
    }

    // Test the DAO
    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAO();

        System.out.println("=== Testing ProductDAO with existing Product class ===");

        // Test database connection first
        DatabaseConnection.testConnection();

        // Fix image paths first
        System.out.println("\n Fixing image paths...");
        productDAO.updateImagePaths();

        // Test getting all products
        System.out.println("\n All Products:");
        List<Product> allProducts = productDAO.getAllProducts();
        for (Product product : allProducts) {
            System.out.println(product.getName() + " - " + formatPrice(product.getPrice()) +
                    " - Stock: " + product.getQuantity() + " - Available: " + product.isAvailable()) ;
                 //   " - Image: " + product.getImagePath());
        }

        // Test getting categories
        System.out.println("\n🏷 Categories:");
        List<String> categories = productDAO.getAllCategories();
        for (String category : categories) {
            System.out.println("- " + category);
        }

        // Test getting coffee products
        System.out.println("\n Coffee Products:");
        List<Product> coffeeProducts = productDAO.getProductsByCategory("Coffee");



        // Test search functionality
        System.out.println("\n Search for 'Latte':");
        List<Product> searchResults = productDAO.searchProductsByName("Latte");


        // Close connection
        DatabaseConnection.closeConnection();
    }
}