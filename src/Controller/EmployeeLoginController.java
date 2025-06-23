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
import obj.Employee;
import obj.EmployeeDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeLoginController {

    // ==== FXML ====
    @FXML private TextField emp_phonenumber;
    @FXML private PasswordField emp_password;
    @FXML private Button emp_loginBtn;
    @FXML private Hyperlink emp_forgotPass;
    @FXML private AnchorPane emp_loginForm;

    @FXML private TextField emp_fp_email;
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
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private Employee currentEmployee = null;

    // ==== Đăng nhập ====
    public void loginBtn() {
        String phone = emp_phonenumber.getText();
        String password = emp_password.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        // Validate phone number format
        if (!phone.matches("\\d{10,15}")) {
            showAlert(Alert.AlertType.ERROR, "Phone number should contain 10-15 digits only");
            return;
        }

        try {
            currentEmployee = employeeDAO.authenticateEmployee(phone, password);
            
            if (currentEmployee != null) {
                // Show brief welcome message
                Alert welcome = new Alert(Alert.AlertType.INFORMATION);
                welcome.setTitle("Login Successful");
                welcome.setHeaderText("Welcome!");
                welcome.setContentText("Welcome " + currentEmployee.getName() + "!\nLoading your dashboard...");
                welcome.showAndWait();
                
                // Navigate to employee dashboard
                showEmployeeDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect phone number or password");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showEmployeeDashboard() {
        try {
            // Load the employee main app FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/employee-main-app.fxml"));
            Parent employeeDashboardRoot = loader.load();
            
            // Get the controller and pass employee data
            if (currentEmployee != null) {
                Object controller = loader.getController();
                // Check if the controller is StoreController and set employee data
                if (controller instanceof StoreController) {
                    StoreController storeController = (StoreController) controller;
                    // Pass employee object to the store controller
                    storeController.setCurrentEmployee(currentEmployee);
                }
            }
            
            // Create new scene and stage
            Scene employeeDashboardScene = new Scene(employeeDashboardRoot);
            Stage stage = (Stage) emp_loginBtn.getScene().getWindow();
            stage.setScene(employeeDashboardScene);
            stage.setTitle("Employee Dashboard - " + (currentEmployee != null ? currentEmployee.getName() : "Employee Portal"));
            stage.show();
            
        } catch (IOException e) {
            // Fallback to alert if FXML loading fails
            showAlert(Alert.AlertType.ERROR, "Error loading employee dashboard: " + e.getMessage());
            e.printStackTrace();
        }
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

    // ==== Xác minh email và số điện thoại ====
    public void proceedBtn() {
        String email = emp_fp_email.getText();
        String phonenumber = emp_fp_phonenumber.getText();

        if (email.isEmpty() || phonenumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all blank fields");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid email address");
            return;
        }

        // Validate phone number format
        if (!phonenumber.matches("\\d{10,15}")) {
            showAlert(Alert.AlertType.ERROR, "Phone number should contain 10-15 digits only");
            return;
        }

        String query = "SELECT * FROM Users WHERE email = ? AND phone = ? AND role = 'employee'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, email);
            prepare.setString(2, phonenumber);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                emp_newPassForm.setVisible(true);
                emp_forgotPassForm.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect email or phone number");
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
        String email = emp_fp_email.getText();
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

        if (newPass.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Password must be at least 6 characters long");
            return;
        }

        String query = "UPDATE Users SET password = ? WHERE email = ? AND role = 'employee'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, newPass);
            prepare.setString(2, email);

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Your password updated successfully.");

                // Reset form
                emp_loginForm.setVisible(true);
                emp_newPassForm.setVisible(false);
                emp_fp_email.clear();
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
