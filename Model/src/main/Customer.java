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
    public void start(@SuppressWarnings("exports") Stage stage) throws Exception {
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
        Coffee coffee1 = new Coffee("NULL", "NULL", 150000);
        store.addProduct(coffee1);
  

        launch(args);
    }
}
