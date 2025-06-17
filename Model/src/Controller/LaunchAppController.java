package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class LaunchAppController {

    @FXML private Button admin_btn;
    @FXML private Button employee_btn;
    @FXML private Button user_btn1;
    @FXML private StackPane welcomeScreen;

    @FXML
    private void switchAdminLogin() {
        try {
            Parent adminLoginRoot = FXMLLoader.load(getClass().getResource("/view/AdminLogin.fxml"));
            Scene adminScene = new Scene(adminLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(adminScene);
            stage.setTitle("OOP Coffee - Admin Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load AdminLogin.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void switchUserLogin() {
        try {
            // ✅ Có thể tạo CustomerLogin riêng hoặc dùng chung AdminLogin
            Parent userLoginRoot = FXMLLoader.load(getClass().getResource("/view/CustomerLogin.fxml"));
            Scene userScene = new Scene(userLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(userScene);
            stage.setTitle("OOP Coffee - Customer Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load CustomerLogin.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void switchEmployeeLogin() {
        try {
            // ✅ Có thể tạo EmployeeLogin riêng hoặc dùng chung AdminLogin
            Parent employeeLoginRoot = FXMLLoader.load(getClass().getResource("/view/EmployeeLogin.fxml"));
            Scene employeeScene = new Scene(employeeLoginRoot);
            Stage stage = (Stage) welcomeScreen.getScene().getWindow();
            stage.setScene(employeeScene);
            stage.setTitle("OOP Coffee - Employee Login");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load EmployeeLogin.fxml: " + e.getMessage());
        }
    }
}
