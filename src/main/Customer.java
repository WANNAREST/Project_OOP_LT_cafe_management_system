package main;

import Controller.UserAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import obj.Coffee;
import obj.Store;

import java.io.IOException;

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

        Coffee coffee1 = new Coffee(1, "Espresso", "Coffee", 150000, "Hellu");
        Coffee coffee2 = new Coffee(2, "Americano", "Coffee", 120000, "");
        Coffee coffee3 = new Coffee(3, "Cappuccino", "Coffee", 180000, "");
        Coffee coffee4 = new Coffee(4, "Mocha", "Coffee", 200000, "");
        Coffee coffee5 = new Coffee(5, "Flat White", "Coffee", 170000, "");
        Coffee coffee6 = new Coffee(6, "Macchiato", "Coffee", 160000, "");
        Coffee coffee7 = new Coffee(7, "Ristretto", "Coffee", 130000, "");
        Coffee coffee8 = new Coffee(8, "Affogato", "Coffee", 190000, "");
        Coffee coffee9 = new Coffee(9, "Long Black", "Coffee", 140000, "");
        Coffee coffee10 = new Coffee(10, "Latte", "Coffee", 220000, "");

        store.addProduct(coffee1);
        store.addProduct(coffee2);


        store.addProduct(coffee3);
        store.addProduct(coffee4);
        store.addProduct(coffee5);
        store.addProduct(coffee6);
        store.addProduct(coffee7);
        store.addProduct(coffee8);
        store.addProduct(coffee9);
        store.addProduct(coffee10);



        launch(args);
    }
}
