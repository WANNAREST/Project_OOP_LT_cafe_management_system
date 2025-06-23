package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import obj.Employee;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML private TextField employee_ID;
    @FXML private TextField employee_fullName;  //  SỬA - Chỉ có fullName
    @FXML private TextField employee_phone;
    @FXML private TextField employee_email;
    @FXML private TextField employee_address;
    @FXML private ComboBox<String> employee_gender;
    @FXML private TextField employee_citizenId;
    @FXML private DatePicker employee_dob;
    @FXML private TextField employee_role;
    @FXML private Button employee_addBtn;
    @FXML private Button employee_updateBtn;
    @FXML private Button employee_deleteBtn;
    @FXML private Button employee_clearBtn;
    @FXML private TableView<Employee> employee_tableView;
    @FXML private TableColumn<Employee, Integer> employee_col_ID;
    @FXML private TableColumn<Employee, String> employee_col_fullName;
    @FXML private TableColumn<Employee, String> employee_col_phone;
    @FXML private TableColumn<Employee, String> employee_col_role;
    @FXML private TableColumn<Employee, String> employee_col_address;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private Alert alert;

    //  SỬA - THÊM EMPLOYEE với Full Name
    @FXML
    public void employeeAddBtn() {
        String fullName = employee_fullName.getText().trim();  //  SỬA - Chỉ lấy fullName
        String phone = employee_phone.getText();
        String email = employee_email.getText();
        String address = employee_address.getText();
        String gender = employee_gender.getValue();
        String citizenId = employee_citizenId.getText();
        String role = employee_role.getText();
        String password = "123456"; // Default password

        if (fullName.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin bắt buộc (Họ tên, SĐT, Vai trò)!");
            return;
        }

        if (employee_dob.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng chọn ngày sinh!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Thêm vào bảng Users
            String insertUserSql = "INSERT INTO Users (full_name, address, dob, phone, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int userId;

            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, fullName);  //  SỬA - Dùng fullName trực tiếp
                userStmt.setString(2, address);
                userStmt.setDate(3, Date.valueOf(employee_dob.getValue()));
                userStmt.setString(4, phone);
                userStmt.setString(5, email.isEmpty() ? null : email);
                userStmt.setString(6, password);
                userStmt.setString(7, role);

                userStmt.executeUpdate();

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Không thể lấy user_id");
                    }
                }
            }

            // 2. Thêm vào bảng Employees
            String insertEmployeeSql = "INSERT INTO Employees (employee_id, gender, citizen_id) VALUES (?, ?, ?)";
            try (PreparedStatement empStmt = conn.prepareStatement(insertEmployeeSql)) {
                empStmt.setInt(1, userId);
                empStmt.setString(2, gender);
                empStmt.setString(3, citizenId.isEmpty() ? null : citizenId);

                empStmt.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, " Thêm nhân viên thành công!\nMã NV: " + userId);
            employeeShowData();
            employeeClearBtn();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi thêm nhân viên: " + e.getMessage());
        }
    }

    //  SỬA - CẬP NHẬT EMPLOYEE với Full Name
    @FXML
    public void employeeUpdateBtn() {
        String employeeId = employee_ID.getText();
        String fullName = employee_fullName.getText().trim();  //  SỬA - Chỉ lấy fullName
        String phone = employee_phone.getText();
        String email = employee_email.getText();
        String address = employee_address.getText();
        String gender = employee_gender.getValue();
        String citizenId = employee_citizenId.getText();
        String role = employee_role.getText();

        if (employeeId.isEmpty() || fullName.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận cập nhật");
        confirmAlert.setHeaderText("Bạn có chắc muốn cập nhật nhân viên ID: " + employeeId + "?");
        
        Optional<ButtonType> option = confirmAlert.showAndWait();
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                // 1. Cập nhật bảng Users
                String updateUserSql = "UPDATE Users SET full_name = ?, address = ?, dob = ?, phone = ?, email = ?, role = ? WHERE user_id = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(updateUserSql)) {
                    userStmt.setString(1, fullName);  //  SỬA - Dùng fullName trực tiếp
                    userStmt.setString(2, address);
                    userStmt.setDate(3, employee_dob.getValue() != null ? Date.valueOf(employee_dob.getValue()) : null);
                    userStmt.setString(4, phone);
                    userStmt.setString(5, email.isEmpty() ? null : email);
                    userStmt.setString(6, role);
                    userStmt.setInt(7, Integer.parseInt(employeeId));

                    userStmt.executeUpdate();
                }

                // 2. Cập nhật bảng Employees
                String updateEmployeeSql = "UPDATE Employees SET gender = ?, citizen_id = ? WHERE employee_id = ?";
                try (PreparedStatement empStmt = conn.prepareStatement(updateEmployeeSql)) {
                    empStmt.setString(1, gender);
                    empStmt.setString(2, citizenId.isEmpty() ? null : citizenId);
                    empStmt.setInt(3, Integer.parseInt(employeeId));

                    empStmt.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, " Cập nhật nhân viên thành công!");
                employeeShowData();
                employeeClearBtn();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    //  SỬA - XÓA EMPLOYEE với database mới
    @FXML
    public void employeeDeleteBtn() {
        String employeeId = employee_ID.getText();
        
        if (employeeId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Vui lòng chọn nhân viên để xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn XÓA nhân viên ID: " + employeeId + "?");
        confirmAlert.setContentText(" Hành động này sẽ xóa tất cả dữ liệu liên quan và không thể hoàn tác!");
        
        Optional<ButtonType> option = confirmAlert.showAndWait();
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            // Xóa từ Users sẽ tự động xóa từ Employees (CASCADE)
            String deleteUserSql = "DELETE FROM Users WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
                
                stmt.setInt(1, Integer.parseInt(employeeId));
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, " Xóa nhân viên thành công!");
                    employeeShowData();
                    employeeClearBtn();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Không tìm thấy nhân viên để xóa!");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi xóa nhân viên: " + e.getMessage());
            }
        }
    }

    //  SỬA - LẤY DỮ LIỆU từ Users + Employees
    public ObservableList<Employee> employeeDataList() {
        ObservableList<Employee> listData = FXCollections.observableArrayList();

        String sql = "SELECT u.user_id, u.full_name, u.address, u.dob, u.phone, u.email, u.role, " +
                    "e.gender, e.citizen_id " +
                    "FROM Users u " +
                    "JOIN Employees e ON u.user_id = e.employee_id " +
                    "WHERE u.role IN ('employee', 'manager') " +
                    "ORDER BY u.user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("role"),
                    rs.getString("gender"),
                    rs.getString("citizen_id"),
                    rs.getDate("dob")
                );
                listData.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu: " + e.getMessage());
        }

        return listData;
    }

    //  HIỂN THỊ DỮ LIỆU
    public void employeeShowData() {
        ObservableList<Employee> employeeList = employeeDataList();

        employee_col_ID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employee_col_fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        employee_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        employee_col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        employee_col_address.setCellValueFactory(new PropertyValueFactory<>("address"));

        employee_tableView.setItems(employeeList);
    }

    //  CHỌN DỮ LIỆU TỪ BẢNG
    @FXML
    public void employeeSelectData() {
        Employee employeeData = employee_tableView.getSelectionModel().getSelectedItem();
        int num = employee_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1 || employeeData == null) {
            return;
        }

        employee_ID.setText(String.valueOf(employeeData.getEmployeeId()));
        employee_fullName.setText(employeeData.getFullName());  //  SỬA - Đặt fullName trực tiếp
        employee_phone.setText(employeeData.getPhone());
        employee_email.setText(employeeData.getEmail() != null ? employeeData.getEmail() : "");
        employee_address.setText(employeeData.getAddress() != null ? employeeData.getAddress() : "");
        employee_role.setText(employeeData.getRole());
        employee_gender.setValue(employeeData.getGender());
        employee_citizenId.setText(employeeData.getCitizenId() != null ? employeeData.getCitizenId() : "");
        
        if (employeeData.getDob() != null) {
            employee_dob.setValue(employeeData.getDob());
        }
    }

    //  SỬA - XÓA FORM với Full Name
    @FXML
    public void employeeClearBtn() {
        employee_ID.clear();
        employee_fullName.clear();  //  SỬA - Chỉ clear fullName
        employee_phone.clear();
        employee_email.clear();
        employee_address.clear();
        employee_citizenId.clear();
        employee_gender.getSelectionModel().clearSelection();
        employee_role.clear();
        employee_dob.setValue(null);
    }



    private void showAlert(Alert.AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup ComboBoxes
        employee_gender.setItems(FXCollections.observableArrayList("male", "female"));

        // Setup table selection
        employee_tableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> employeeSelectData()
        );

        employeeShowData();
    }
}
