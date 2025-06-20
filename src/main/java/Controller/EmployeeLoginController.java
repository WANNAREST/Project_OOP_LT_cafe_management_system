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

public class EmployeeLoginController {

    // ==== FXML ====
    @FXML private TextField emp_username;
    @FXML private PasswordField emp_password;
    @FXML private Button emp_loginBtn;
    @FXML private Hyperlink emp_forgotPass;
    @FXML private AnchorPane emp_loginForm;

    @FXML private TextField emp_fp_username;
    @FXML private TextField emp_fp_phonenumber;
    @FXML private Button emp_fp_proceedBtn;
    @FXML private AnchorPane emp_forgotPassForm;

    @FXML private AnchorPane emp_newPassForm;
    @FXML private PasswordField emp_newPassword;
    @FXML private PasswordField emp_confirmPassword;
    @FXML private Button emp_np_changePassBtn;

    @FXML private Button emp_backToHome;
    @FXML private Button emp_fp_backToLogin;
    @FXML private Button emp_np_backToQuestion;

    private Alert alert;

    // ==== Đăng nhập ====
    public void loginBtn() {
        String username = emp_username.getText();
        String password = emp_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        String query = "SELECT * FROM Users WHERE full_name = ? AND password = ? AND role = 'employee'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, username);
            prepare.setString(2, password);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Employee login successful!");
                showEmployeeDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect employee username or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmployeeDashboard() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Welcome Employee");
        info.setHeaderText(null);
        info.setContentText("Welcome to the Employee Dashboard!");
        info.showAndWait();
    }

    // ==== Chuyển sang form Forgot Password ====
    public void switchForgotPass() {
        emp_forgotPassForm.setVisible(true);
        emp_loginForm.setVisible(false);
    }

    // ==== Quay lại Login form ====
    public void backToLoginForm() {
        emp_loginForm.setVisible(true);
        emp_forgotPassForm.setVisible(false);
        emp_newPassForm.setVisible(false);
    }

    // ==== Xác minh số điện thoại ====
    public void proceedBtn() {
        String username = emp_fp_username.getText();
        String phonenumber = emp_fp_phonenumber.getText();

        if (username.isEmpty() || phonenumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        String query = "SELECT * FROM Users WHERE full_name = ? AND phone = ? AND role = 'employee'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, username);
            prepare.setString(2, phonenumber);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                emp_newPassForm.setVisible(true);
                emp_forgotPassForm.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect username or phone number");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==== Quay lại form xác minh số điện thoại ====
    public void backToQuestionForm() {
        emp_newPassForm.setVisible(false);
        emp_forgotPassForm.setVisible(true);
    }

    // ==== Đổi mật khẩu ====
    public void changePassBtn() {
        String username = emp_fp_username.getText();
        String newPass = emp_newPassword.getText();
        String confirmPass = emp_confirmPassword.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match");
            return;
        }

        String query = "UPDATE Users SET password = ? WHERE full_name = ? AND role = 'employee'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, newPass);
            prepare.setString(2, username);

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Your password updated successfully.");

                // Reset form
                emp_loginForm.setVisible(true);
                emp_newPassForm.setVisible(false);
                emp_fp_username.clear();
                emp_fp_phonenumber.clear();
                emp_newPassword.clear();
                emp_confirmPassword.clear();
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
