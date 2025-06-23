package main;

import Controller.UserAppController;
import Controller.ProductDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import obj.Cart;
import obj.Coffee;
import obj.Customer;
import obj.Product;
import obj.Store;

import java.io.IOException;
import java.util.List;

public class Shop extends Application {
    private static Store store;

    public static void main(String[] args) {
        // Initialize the store
        store = new Store();

        // Load products from database
        loadProductsFromDatabase();

        // Launch the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load the launch app FXML (welcome screen with login options)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LaunchApp.fxml"));

            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root);

            // Set up the primary stage
            primaryStage.setTitle("Cafe Shop Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

            System.out.println(" Application started successfully!");

        } catch (IOException e) {
            System.err.println(" Error loading launch screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load products from database and add them to the store
     */
    private static void loadProductsFromDatabase() {
        try {
            ProductDAO productDAO = new ProductDAO();

            // Get all available products from database
            List<Product> productsFromDB = productDAO.getAvailableProducts();

            if (productsFromDB.isEmpty()) {
                System.out.println("No products found in database. Loading default products...");
                loadDefaultProducts();
            } else {
                System.out.println(" Loading " + productsFromDB.size() + " products from database...");

                // Add each product from database to the store
                for (Product product : productsFromDB) {
                    store.addProduct(product);
                    System.out.println("Added: " + product.getName() + " - " +
                            ProductDAO.formatPrice(product.getPrice()) +
                            " (Stock: " + product.getQuantity() + ")");
                }
            }

        } catch (Exception e) {
            System.err.println(" Error loading products from database: " + e.getMessage());
            System.out.println(" Loading default products as fallback...");
            loadDefaultProducts();
        }
    }

    /**
     * Fallback method to load default products if database is not available
     */
    private static void loadDefaultProducts() {
        // Add some default products
        Product coffee1 = new Coffee(1, "Espresso", "Coffee", 25000, "Strong Italian coffee");
        Product coffee2 = new Coffee(2, "Cappuccino", "Coffee", 35000, "Italian coffee with steamed milk");
        Product coffee3 = new Coffee(3, "Latte", "Coffee", 40000, "Coffee with steamed milk and foam");

        store.addProduct(coffee1);
        store.addProduct(coffee2);
        store.addProduct(coffee3);

        System.out.println(" Default products loaded successfully");
    }

    /**
     * Get the store instance (for use by other controllers)
     */
    public static Store getStore() {
        return store;
    }
}
