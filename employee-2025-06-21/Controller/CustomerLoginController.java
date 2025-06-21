package Controller;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DATABASE.DatabaseConnection;

public class CustomerLoginController {

    // ==== FXML ====
    @FXML private TextField si_username;
    @FXML private PasswordField si_password;
    @FXML private Button si_loginBtn;
    @FXML private Hyperlink si_forgotPass;
    @FXML private AnchorPane si_loginForm;

    @FXML private TextField su_username;
    @FXML private PasswordField su_password;
    @FXML private TextField su_phonenumber;
    @FXML private TextField su_email;
    @FXML private Button su_signupBtn;
    @FXML private AnchorPane su_signupForm;

    @FXML private AnchorPane side_form;
    @FXML private Button side_CreateBtn;
    @FXML private Button side_alreadyHave;

    @FXML private AnchorPane fp_questionForm;
    @FXML private TextField fp_username;
    @FXML private TextField fp_phonenumber;
    @FXML private Button fp_proceedBtn;
    @FXML private Button fp_back;

    @FXML private AnchorPane np_newPassForm;
    @FXML private PasswordField np_newPassword;
    @FXML private PasswordField np_confirmPassword;
    @FXML private Button np_changePassBtn;
    @FXML private Button np_back;
    @FXML private Button fp_backtohome;

    private Alert alert;

    // ==== Đăng nhập ====
    public void loginBtn() {
        String fullName = si_username.getText();
        String password = si_password.getText();

        if (fullName.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE full_name = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                showMainScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect full name or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection failed");
        }
    }

    private void showMainScreen() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Welcome");
        info.setHeaderText(null);
        info.setContentText("Welcome to Cafe Shop!");
        info.showAndWait();
    }

    // ==== Đăng ký ====
    public void regBtn() {
        String fullName = su_username.getText();
        String password = su_password.getText();
        String phone = su_phonenumber.getText();
        String email = su_email.getText();

        // Kiểm tra rỗng
        if (fullName.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        // Kiểm tra định dạng số điện thoại
        if (!phone.matches("^(03[2-9]|05[6-9]|07[0-9]|08[1-9]|09[0-9])[0-9]{7}$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid phone number format");
            return;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            showAlert(Alert.AlertType.ERROR, "Email must be in format name@gmail.com");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra username đã tồn tại chưa
            String checkUserQuery = "SELECT * FROM users WHERE full_name = ?";
            PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, fullName);
            ResultSet userRs = checkUserStmt.executeQuery();
            if (userRs.next()) {
                showAlert(Alert.AlertType.ERROR, "Full name is already taken");
                return;
            }

            // Kiểm tra số điện thoại đã tồn tại chưa
            String checkPhoneQuery = "SELECT * FROM users WHERE phone = ?";
            PreparedStatement checkPhoneStmt = conn.prepareStatement(checkPhoneQuery);
            checkPhoneStmt.setString(1, phone);
            ResultSet phoneRs = checkPhoneStmt.executeQuery();
            if (phoneRs.next()) {
                showAlert(Alert.AlertType.ERROR, "Phone number already exists");
                return;
            }

            // Kiểm tra email đã tồn tại chưa
            String checkEmailQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailQuery);
            checkEmailStmt.setString(1, email);
            ResultSet emailRs = checkEmailStmt.executeQuery();
            if (emailRs.next()) {
                showAlert(Alert.AlertType.ERROR, "Email already exists");
                return;
            }

            // Thêm tài khoản mới nếu mọi thứ hợp lệ
            String insertQuery = "INSERT INTO users (full_name, password, phone, email, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, fullName);
            insertStmt.setString(2, password);
            insertStmt.setString(3, phone);
            insertStmt.setString(4, email);
            insertStmt.setString(5, "customer");
            insertStmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Account registered successfully!");

            // Clear form
            su_username.clear();
            su_password.clear();
            su_phonenumber.clear();
            su_email.clear();

            // Chuyển về login form
            switchToLoginForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration failed");
        }
    }



    private void switchToLoginForm() {
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(side_form);
        slider.setDuration(Duration.seconds(0.5));

        slider.setToX(0);
        su_signupForm.setVisible(false);
        fp_questionForm.setVisible(false);
        np_newPassForm.setVisible(false);
        si_loginForm.setVisible(true);
        side_CreateBtn.setVisible(true);
        side_alreadyHave.setVisible(false);

        slider.play();
    }


    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        slider.setNode(side_form);
        slider.setDuration(Duration.seconds(0.5));

        if (event.getSource() == side_CreateBtn) {
            slider.setToX(640);
            si_loginForm.setVisible(false);
            fp_questionForm.setVisible(false);
            np_newPassForm.setVisible(false);
            su_signupForm.setVisible(true);
            side_CreateBtn.setVisible(false);
            side_alreadyHave.setVisible(true);
        } else if (event.getSource() == side_alreadyHave) {
            slider.setToX(0);
            su_signupForm.setVisible(false);
            fp_questionForm.setVisible(false);
            np_newPassForm.setVisible(false);
            si_loginForm.setVisible(true);
            side_CreateBtn.setVisible(true);
            side_alreadyHave.setVisible(false);
        }

        slider.play();
    }

    public void switchForgotPass() {
        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);
    }

    public void backToLoginForm() {
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
    }

    public void proceedBtn() {
        String fullName = fp_username.getText();
        String phone = fp_phonenumber.getText();

        if (fullName.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE full_name = ? AND phone = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                np_newPassForm.setVisible(true);
                fp_questionForm.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect full name or phone number");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error");
        }
    }

    public void backToQuestionForm() {
        np_newPassForm.setVisible(false);
        fp_questionForm.setVisible(true);
    }

    public void changePassBtn() {
        String fullName = fp_username.getText();
        String newPass = np_newPassword.getText();
        String confirmPass = np_confirmPassword.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE users SET password = ? WHERE full_name = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, newPass);
            stmt.setString(2, fullName);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Password updated successfully!");
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
            showAlert(Alert.AlertType.ERROR, "Database error");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
