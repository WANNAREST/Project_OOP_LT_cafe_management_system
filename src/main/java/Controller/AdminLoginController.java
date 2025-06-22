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

    // ==== Đăng nhập bằng số điện thoại và mật khẩu ====
    public void loginBtn() {
        String phoneNumber = si_username.getText();
        String password = si_password.getText();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert(Alert.AlertType.ERROR, "Số điện thoại không hợp lệ!");
            return;
        }

        String query = "SELECT * FROM Users WHERE phone = ? AND password = ? AND role = 'manager'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, phoneNumber);
            prepare.setString(2, password);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                showAdminDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Số điện thoại hoặc mật khẩu không chính xác!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^(\\+84|84|0)[3-9][0-9]{8}$";
        return phoneNumber.matches(phoneRegex);
    }

    private void showAdminDashboard() {
        try {
            Parent adminRoot = FXMLLoader.load(getClass().getResource("/view/admin-main-app.fxml"));
            Scene adminScene = new Scene(adminRoot);

            Stage stage = (Stage) si_loginBtn.getScene().getWindow();
            stage.setScene(adminScene);
            stage.setTitle("Cafe Shop - Admin Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể tải giao diện admin!");
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

    // ==== Xác minh số điện thoại cho quên mật khẩu ====
    public void proceedBtn() {
        String primaryPhone = fp_username.getText();
        String verificationPhone = fp_phonenumber.getText();

        if (primaryPhone.isEmpty() || verificationPhone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!primaryPhone.equals(verificationPhone)) {
            showAlert(Alert.AlertType.ERROR, "Hai số điện thoại không khớp!");
            return;
        }

        if (!isValidPhoneNumber(primaryPhone)) {
            showAlert(Alert.AlertType.ERROR, "Số điện thoại không hợp lệ!");
            return;
        }

        String query = "SELECT * FROM Users WHERE phone = ? AND role = 'manager'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, primaryPhone);

            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                np_newPassForm.setVisible(true);
                fp_questionForm.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Không tìm thấy admin với số điện thoại này!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    // ==== Quay về form xác minh số điện thoại từ đổi mật khẩu ====
    public void backToQuestionForm() {
        np_newPassForm.setVisible(false);
        fp_questionForm.setVisible(true);
    }

    // ==== Đổi mật khẩu ====
    public void changePassBtn() {
        String phoneNumber = fp_username.getText();
        String newPass = np_newPassword.getText();
        String confirmPass = np_confirmPassword.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (newPass.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Mật khẩu xác nhận không khớp!");
            return;
        }

        String query = "UPDATE Users SET password = ? WHERE phone = ? AND role = 'manager'";

        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement prepare = connect.prepareStatement(query);

            prepare.setString(1, newPass);
            prepare.setString(2, phoneNumber);

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Cập nhật mật khẩu thành công!");

                // Reset form
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
                fp_username.clear();
                fp_phonenumber.clear();
                np_newPassword.clear();
                np_confirmPassword.clear();
                si_username.clear();
                si_password.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Không thể cập nhật mật khẩu!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    // ==== Hiện thông báo ====
    private void showAlert(Alert.AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Lỗi" : "Thông báo");
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
            showAlert(Alert.AlertType.ERROR, "Không thể tải màn hình chính!");
        }
    }
}
