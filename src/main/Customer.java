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

        Coffee coffee1 = new Coffee("Espresso", "Coffee", 150000);
       Coffee coffee2 = new Coffee("Americano", "Coffee", 120000);
        Coffee coffee3 = new Coffee("Cappuccino", "Coffee", 180000);
        Coffee coffee4 = new Coffee("Mocha", "Coffee", 200000);
        Coffee coffee5 = new Coffee("Flat White", "Coffee", 170000);
        Coffee coffee6 = new Coffee("Macchiato", "Coffee", 160000);
        Coffee coffee7 = new Coffee("Ristretto", "Coffee", 130000);
        Coffee coffee8 = new Coffee("Affogato", "Coffee", 190000);
        Coffee coffee9 = new Coffee("Long Black", "Coffee", 140000);
        Coffee coffee10 = new Coffee("Latte", "Coffee", 220000);

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
