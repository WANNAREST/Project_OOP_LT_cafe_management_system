package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMainController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField searchField;
    @FXML private Label nameLabel;
    @FXML private ImageView avt;
    @FXML private AnchorPane contentPane;
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;

    private Alert alert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default admin name
        nameLabel.setText("Admin");
        
        // Load dashboard by default
        showDashboard();
    }

    // ==== NAVIGATION METHODS ====

    @FXML
    private void showSearch() {
        // Implementation for search functionality
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Tìm kiếm: " + searchText);
        }
    }

    @FXML
    private void showDashboard() {
        loadView("/view/admin-dashboard.fxml", "Dashboard");
    }

    @FXML
    private void showCart() {
        loadView("/view/admin-cart-view.fxml", "Quản lý đơn hàng");
    }

    @FXML
    private void showUserDetail() {
        loadView("/view/admin-employee.fxml", "Quản lý nhân viên");
    }

    @FXML
    private void showShift() {
        loadView("/view/admin-shift.fxml", "Quản lý ca làm việc");
    }

    @FXML
    private void showInventory() {
        loadView("/view/admin-inventory.fxml", "Quản lý kho hàng");
    }

    @FXML
    private void showSalaryRecord() {
        loadView("/view/admin-salary-record.fxml", "Quản lý lương");
    }

    @FXML
    private void signOut() {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/view/LaunchApp.fxml"));
            Scene homeScene = new Scene(homeRoot);

            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(homeScene);
            stage.setTitle("Cafe Shop Management System");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể đăng xuất!");
        }
    }

    // ==== UTILITY METHODS ====

    private void loadView(String fxmlPath, String viewName) {
        try {
            contentPane.getChildren().clear();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            contentPane.getChildren().add(view);
            
            // Adjust the loaded view to fit the content pane (works for any Parent type)
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            System.out.println("Loaded view: " + viewName);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể tải " + viewName + ": " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Lỗi" : "Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 