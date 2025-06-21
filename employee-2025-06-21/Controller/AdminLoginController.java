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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DATABASE.DatabaseConnection;

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

    @FXML private Button fp_backtohome;
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

        String query = "SELECT * FROM users WHERE full_name = ? AND password = ? AND role = 'manager'";

        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, username);
            prepare.setString(2, password);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Admin login successful!");
                showAdminDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect admin username or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAdminDashboard() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Welcome Admin");
        info.setHeaderText(null);
        info.setContentText("Welcome to the Admin Dashboard!");
        info.showAndWait();
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

        String query = "SELECT * FROM users WHERE full_name = ? AND phone = ? AND role = 'manager'";

        try (Connection connect = DatabaseConnection.getConnection();
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

        String query = "UPDATE users SET password = ? WHERE full_name = ? AND role = 'manager'";

        try (Connection connect = DatabaseConnection.getConnection();
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

    // ==== Quay về màn hình chính ====
    @FXML
    private void backToHomeScreen(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/sample/cafeshopmanagement/LaunchApp.fxml"));
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
