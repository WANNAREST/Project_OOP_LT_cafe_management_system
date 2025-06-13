package sample.cafeshopmanagement.controllers;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    @FXML private AnchorPane waitingScreen;

    private Alert alert;

    // ==== Dữ liệu employee giả lập ====
    private final List<HashMap<String, String>> employees = new ArrayList<>();

    // ==== Khởi tạo ====
    public void initialize() {
        HashMap<String, String> emp = new HashMap<>();
        emp.put("username", "employee");
        emp.put("password", "654321");
        emp.put("phonenumber", "0987654321");
        employees.add(emp);
    }

    // ==== Đăng nhập ====
    public void loginBtn() {
        String username = emp_username.getText();
        String password = emp_password.getText();

        Optional<HashMap<String, String>> emp = employees.stream()
                .filter(e -> e.get("username").equals(username) && e.get("password").equals(password))
                .findFirst();

        if (emp.isPresent()) {
            showAlert(Alert.AlertType.INFORMATION, "Employee login successful!");
            showEmployeeDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect employee username or password");
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

        Optional<HashMap<String, String>> matched = employees.stream()
                .filter(e -> e.get("username").equals(username) && e.get("phonenumber").equals(phonenumber))
                .findFirst();

        if (matched.isPresent()) {
            emp_newPassForm.setVisible(true);
            emp_forgotPassForm.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect username or phone number");
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

        Optional<HashMap<String, String>> emp = employees.stream()
                .filter(e -> e.get("username").equals(username))
                .findFirst();

        if (emp.isPresent()) {
            emp.get().put("password", newPass);
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
