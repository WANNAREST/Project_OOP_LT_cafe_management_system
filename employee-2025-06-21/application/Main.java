package application;

import java.io.IOException;
import Controller.StoreController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import obj.Store_CoffeeShop;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
    private Store_CoffeeShop store;

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Khởi tạo store
        store = new Store_CoffeeShop();
        store.loadProductsFromDB(); // Load sản phẩm từ database
        
        // 2. Khởi tạo controller với store đã có dữ liệu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-main-app.fxml"));
        StoreController controller = new StoreController(store);
        loader.setController(controller);
        
        // 3. Load FXML và hiển thị
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("OOP Coffee");
            stage.show();
            
            controller.refreshProductGrid();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}