package sample.cafeshopmanagement.controllers;

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
import java.util.*;

public class UserLoginController {

    // ==== FXML ====
    @FXML private TextField si_username;
    @FXML private PasswordField si_password;
    @FXML private Button si_loginBtn;
    @FXML private Hyperlink si_forgotPass;
    @FXML private AnchorPane si_loginForm;

    @FXML private TextField su_username;
    @FXML private PasswordField su_password;
    @FXML private TextField su_phonenumber;
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

    // ==== Dữ liệu người dùng giả lập ====
    private final List<Map<String, String>> fakeUsers = new ArrayList<>();

    public void initialize() {
        // Dữ liệu mặc định
        Map<String, String> defaultUser = new HashMap<>();
        defaultUser.put("username", "admin");
        defaultUser.put("password", "123");
        defaultUser.put("phoneNumber", "0987654321");
        fakeUsers.add(defaultUser);
    }

    // ==== Đăng nhập ====
    public void loginBtn() {
        String username = si_username.getText();
        String password = si_password.getText();

        Optional<Map<String, String>> user = fakeUsers.stream()
                .filter(u -> u.get("username").equals(username) && u.get("password").equals(password))
                .findFirst();

        if (user.isPresent()) {
            showAlert(Alert.AlertType.INFORMATION, "Login successful!");
            // Mở trang chính (mockup)
            showMainScreen();
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect username or password");
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
        String username = su_username.getText();
        String password = su_password.getText();
        String phoneNumber = su_phonenumber.getText();

        if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        if (!phoneNumber.matches("^(03[2-9]|05[6-9]|07[0-9]|08[1-9]|09[0-9])[0-9]{7}$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid phone number format");
            return;
        }

        boolean usernameTaken = fakeUsers.stream().anyMatch(u -> u.get("username").equals(username));
        if (usernameTaken) {
            showAlert(Alert.AlertType.ERROR, "Username " + username + " is already taken");
            return;
        }

        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", username);
        newUser.put("password", password);
        newUser.put("phoneNumber", phoneNumber);
        fakeUsers.add(newUser);

        showAlert(Alert.AlertType.INFORMATION, "Account registered successfully!");
        clearRegForm();
    }

    private void clearRegForm() {
        su_username.clear();
        su_password.clear();
        su_phonenumber.clear();
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

    // ==== Quên mật khẩu: sang form số điện thoại ====
    public void switchForgotPass() {
        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);
    }

    public void backToLoginForm() {
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
    }

    public void proceedBtn() {
        String username = fp_username.getText();
        String phoneNumber = fp_phonenumber.getText();

        if (username.isEmpty() || phoneNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields");
            return;
        }

        if (!phoneNumber.matches("^(03[2-9]|05[6-9]|07[0-9]|08[1-9]|09[0-9])[0-9]{7}$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid phone number format");
            return;
        }

        Optional<Map<String, String>> matched = fakeUsers.stream()
                .filter(u -> u.get("username").equals(username) && u.get("phoneNumber").equals(phoneNumber))
                .findFirst();

        if (matched.isPresent()) {
            np_newPassForm.setVisible(true);
            fp_questionForm.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect username or phone number");
        }
    }

    public void backToQuestionForm() {
        np_newPassForm.setVisible(false);
        fp_questionForm.setVisible(true);
    }

    public void changePassBtn() {
        String username = fp_username.getText();
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

        Optional<Map<String, String>> user = fakeUsers.stream()
                .filter(u -> u.get("username").equals(username))
                .findFirst();

        if (user.isPresent()) {
            user.get().put("password", newPass);
            showAlert(Alert.AlertType.INFORMATION, "Password updated successfully!");

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