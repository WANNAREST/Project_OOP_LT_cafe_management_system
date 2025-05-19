package application;

import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class cafeshopManagementSystem extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Cafe Shop Management System  - Admin version");
        stage.setScene(scene);
        stage.show();
	}
	public static void main(String[] args) {
        launch(args);
    }
      
} 
