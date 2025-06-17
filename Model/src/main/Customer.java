package main;

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
        // ✅ SỬA - Load LaunchApp.fxml thay vì user-main-app.fxml
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LaunchApp.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("OOP Coffee - Welcome");
            stage.setScene(scene);
            stage.setResizable(false); // Không cho resize
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading LaunchApp.fxml: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // ✅ KHỞI TẠO store với dữ liệu mẫu
        store = new Store();
        Coffee coffee1 = new Coffee("CF001", "Cà phê đen", 25000);
        Coffee coffee2 = new Coffee("CF002", "Cà phê sữa", 30000);
        Coffee coffee3 = new Coffee("CF003", "Cappuccino", 45000);
        Coffee coffee4 = new Coffee("CF004", "Americano", 35000);
        Coffee coffee5 = new Coffee("CF005", "Latte", 50000);
        
        store.addProduct(coffee1);
        store.addProduct(coffee2);
        store.addProduct(coffee3);
        store.addProduct(coffee4);
        store.addProduct(coffee5);

        launch(args);
    }
    
    // ✅ THÊM getter để các controller khác có thể truy cập store
    public static Store getStore() {
        return store;
    }
}
