package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RUN extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Sửa đường dẫn này cho đúng với cấu trúc project của bạn
        FXMLLoader fxmlLoader = new FXMLLoader(RUN.class.getResource("/view/LaunchApp.fxml"));
        
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Cafe Shop");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}