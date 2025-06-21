package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import obj.Store;
import obj.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLoginController {
 
    // ==== FXML ====
    @FXML private TextField si_username;
    @FXML private PasswordField si_password;
    @FXML private Button si_loginBtn;
    @FXML private Hyperlink si_forgotPass;
    @FXML private AnchorPane si_loginForm;

    @FXML private TextField fp_username;
    @FXML private TextField fp_phonenumber;
    @FXML private Button fp_proceedBtn;
    @FXML private Button si_back;
    @FXML private AnchorPane fp_questionForm;

    @FXML private AnchorPane np_newPassForm;
    @FXML private PasswordField np_newPassword;
    @FXML private PasswordField np_confirmPassword;
    @FXML private Button np_changePassBtn;
    @FXML private Button np_back;

    @FXML private Button fp_back;
    @FXML private AnchorPane side_form;
    
    private Alert alert;

    // ==== Đăng nhập ====
    public void loginBtn() {
        String username = si_username.getText();
        String password = si_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        // ✅ SỬA - Sử dụng bảng Users với cấu trúc đúng
        String query = "SELECT u.*, e.employee_id FROM Users u " +
                      "LEFT JOIN Employees e ON u.user_id = e.employee_id " +
                      "WHERE u.full_name = ? AND u.password = ? AND u.role IN ('manager', 'employee')";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, username);
            prepare.setString(2, password);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                String userRole = result.getString("role");
                String fullName = result.getString("full_name");
                
                showAlert(Alert.AlertType.INFORMATION, 
                         "Login successful! Welcome " + userRole + " " + fullName + "!");
                openUserApp();
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect username or password, or insufficient permissions");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage());
        }
    }

    private void openUserApp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-main-app.fxml"));
            
            // ✅ SỬA - Tạo store và cart trước
            Store store = new Store();
            
            // Set controller trước khi load
            UserAppController controller = new UserAppController(store);
            loader.setController(controller);
            
            Parent userAppRoot = loader.load();
            Scene userAppScene = new Scene(userAppRoot);
            
            // ✅ SỬA - Dùng si_loginBtn thay vì adminLoginBtn
            Stage currentStage = (Stage) si_loginBtn.getScene().getWindow();
            currentStage.setScene(userAppScene);
            currentStage.setTitle("OOP Coffee Management System - Admin");
            currentStage.setMaximized(true);
            currentStage.centerOnScreen();
            
            System.out.println("✅ Opened Admin Panel successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể mở giao diện quản lý: " + e.getMessage());
        }
    }

    // ==== Quên mật khẩu chuyển sang form xác minh số điện thoại ====
    public void switchForgotPass() {
        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);
    }

    // ==== Quay lại form đăng nhập ====
    public void backToLoginForm() {
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
        np_newPassForm.setVisible(false);
    }

    // ==== Xác minh số điện thoại ====
    public void proceedBtn() {
        String username = fp_username.getText();
        String phonenumber = fp_phonenumber.getText();

        if (username.isEmpty() || phonenumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        // ✅ SỬA - Sử dụng bảng Users với cấu trúc đúng
        String query = "SELECT u.*, e.employee_id FROM Users u " +
                      "LEFT JOIN Employees e ON u.user_id = e.employee_id " +
                      "WHERE u.full_name = ? AND u.phone = ? AND u.role IN ('manager', 'employee')";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, username);
            prepare.setString(2, phonenumber);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                np_newPassForm.setVisible(true);
                fp_questionForm.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect username or phone number");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    // ==== Quay về form xác minh số điện thoại từ đổi mật khẩu ====
    public void backToQuestionForm() {
        np_newPassForm.setVisible(false);
        fp_questionForm.setVisible(true);
    }

    // ==== Đổi mật khẩu ====
    public void changePassBtn() {
        String username = fp_username.getText();
        String newPass = np_newPassword.getText();
        String confirmPass = np_confirmPassword.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match");
            return;
        }

        // ✅ SỬA - Sử dụng bảng Users với cấu trúc đúng
        String query = "UPDATE Users SET password = ? WHERE full_name = ? AND role IN ('manager', 'employee')";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, newPass);
            prepare.setString(2, username);

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Your password updated successfully.");

                // Reset form
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
                fp_username.clear();
                fp_phonenumber.clear();
                np_newPassword.clear();
                np_confirmPassword.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    // ==== Hiện thông báo ====
    private void showAlert(Alert.AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error Message" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ THÊM - showAlert với 3 parameters để tương thích
    private void showAlert(String title, String message, Alert.AlertType type) {
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==== Quay về màn hình chính ====
    @FXML
    private void backToHomeScreen(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/view/LaunchApp.fxml"));
            Scene homeScene = new Scene(homeRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(homeScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unable to load home screen");
        }
    }
}
