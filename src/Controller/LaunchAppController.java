package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LaunchAppController {

    @FXML
    private StackPane welcomeScreen;

    @FXML
    private void switchAdminLogin() {
        try {
            Parent adminLoginRoot = FXMLLoader.load(getClass().getResource("/view/admin-login.fxml"));
            Scene adminScene = new Scene(adminLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(adminScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchUserLogin() {
        try {
            Parent userLoginRoot = FXMLLoader.load(getClass().getResource("/view/CustomerLogin.fxml"));
            Scene userScene = new Scene(userLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(userScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchEmployeeLogin() {
        try {
            Parent employeeLoginRoot = FXMLLoader.load(getClass().getResource("/view/employee-login.fxml"));
            Scene employeeScene = new Scene(employeeLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(employeeScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
