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

    // ==== Dữ liệu admin giả lập ====
    private final List<HashMap<String, String>> admins = new ArrayList<>();

    // ==== Khởi tạo ====
    public void initialize() {
        // Thêm admin mẫu
        HashMap<String, String> admin = new HashMap<>();
        admin.put("username", "admin");
        admin.put("password", "123456");
        admin.put("phonenumber", "0123456789");
        admins.add(admin);
    }

    // ==== Đăng nhập ====
    public void loginBtn() {
        String username = si_username.getText();
        String password = si_password.getText();

        Optional<HashMap<String, String>> admin = admins.stream()
                .filter(a -> a.get("username").equals(username) && a.get("password").equals(password))
                .findFirst();

        if (admin.isPresent()) {
            showAlert(Alert.AlertType.INFORMATION, "Admin login successful!");
            showAdminDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect admin username or password");
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

        Optional<HashMap<String, String>> matched = admins.stream()
                .filter(a -> a.get("username").equals(username) && a.get("phonenumber").equals(phonenumber))
                .findFirst();

        if (matched.isPresent()) {
            np_newPassForm.setVisible(true);
            fp_questionForm.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect username or phone number");
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

        Optional<HashMap<String, String>> admin = admins.stream()
                .filter(a -> a.get("username").equals(username))
                .findFirst();

        if (admin.isPresent()) {
            admin.get().put("password", newPass);
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