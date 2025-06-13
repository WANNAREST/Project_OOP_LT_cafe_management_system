package main;

import Controller.UserAppController;
import Controller.ProductDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import obj.Coffee;
import obj.Product;
import obj.Store;

import java.io.IOException;
import java.util.List;

public class Customer extends Application {
    private static Store store;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-main-app.fxml"));
        UserAppController controller = new UserAppController(store);
        loader.setController(controller);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
            // Check for specific causes
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            return;
        }


        Scene scene = new Scene(root);
        stage.setTitle("OOP Coffee");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        store = new Store();

        // Load products from database instead of hardcoded values
        loadProductsFromDatabase();

        launch(args);
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
                System.out.println("⚠️ No products found in database. Loading default products...");

            } else {
                System.out.println("✅ Loading " + productsFromDB.size() + " products from database...");

                // Add each product from database to the store
                for (Product product : productsFromDB) {
                    store.addProduct(product);
                    System.out.println("Added: " + product.getName() + " - " +
                            ProductDAO.formatPrice(product.getPrice()) +
                            " (Stock: " + product.getQuantity() + ")");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading products from database: " + e.getMessage());
            System.out.println("📦 Loading default products as fallback...");

        }
    }

    /**
     * Fallback method to load default products if database is not available
     */

}
