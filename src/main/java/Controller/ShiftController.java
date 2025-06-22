package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;
import obj.ShiftRecord;
import Controller.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShiftController implements Initializable {

    @FXML private VBox scheduleSection;

    // Schedule controls
    @FXML private DatePicker dpWorkDate;
    @FXML private ComboBox<String> cbShift;
    @FXML private TextField tfScheduleEmployeeId;
    @FXML private ComboBox<String> cbShift1;
    @FXML private Button btnAddSchedule;
    @FXML private Button btnDeleteSchedule;
    
    // Table
    @FXML private TableView<ShiftRecord> scheduleTable;
    @FXML private TableColumn<ShiftRecord, String> colScheduleDate;
    @FXML private TableColumn<ShiftRecord, String> colScheduleShift;
    @FXML private TableColumn<ShiftRecord, String> colScheduleEmployeeId;
    @FXML private TableColumn<ShiftRecord, String> colScheduleEmployeeName;
    @FXML private TableColumn<ShiftRecord, String> colScheduleStatus;
    
    // ✅ THÊM - ObservableList cho ComboBox options
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList(
        "scheduled", "completed", "absent", "late"
    );
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupTable();
        setupTableSelection();
        loadShiftData();
        
        // Set default date
        dpWorkDate.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        // Shift time combo
        cbShift.getItems().addAll("morning", "afternoon", "evening");
        
        // Attendance status combo
        cbShift1.setItems(statusOptions);
        cbShift1.setValue("scheduled");
    }

    private void setupTable() {
        // ✅ THÊM - Setup editable table
        scheduleTable.setEditable(true);
        
        colScheduleDate.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        colScheduleShift.setCellValueFactory(new PropertyValueFactory<>("shift"));
        colScheduleEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colScheduleEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        
        // ✅ THÊM - Setup editable ComboBox column cho điểm danh
        colScheduleStatus.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));
        colScheduleStatus.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        
        // ✅ THÊM - Xử lý khi edit điểm danh
        colScheduleStatus.setOnEditCommit(event -> {
            ShiftRecord record = event.getRowValue();
            String newStatus = event.getNewValue();
            String oldStatus = event.getOldValue();
            
            // Cập nhật trong database
            if (updateAttendanceStatus(record, newStatus)) {
                record.setAttendanceStatus(newStatus);
                showAlert("Cập nhật điểm danh thành công!", Alert.AlertType.INFORMATION);
            } else {
                // Rollback nếu update thất bại
                record.setAttendanceStatus(oldStatus);
                scheduleTable.refresh();
                showAlert("Lỗi cập nhật điểm danh!", Alert.AlertType.ERROR);
            }
        });
    }

    // ✅ SỬA - Method cập nhật trạng thái điểm danh
    private boolean updateAttendanceStatus(ShiftRecord record, String newStatus) {
        String updateSql = "UPDATE Shift_Details sd " +
                          "JOIN Shifts s ON sd.shift_id = s.shift_id " +
                          "SET sd.status = ? " +
                          "WHERE sd.employee_id = ? AND s.date = ? AND s.time = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, Integer.parseInt(record.getEmployeeId()));
            stmt.setDate(3, java.sql.Date.valueOf(record.getWorkDate()));
            stmt.setString(4, record.getShift());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // ✅ THÊM - Thông báo cập nhật bảng lương
                showAlert("Cập nhật điểm danh thành công!\n" +
                         "💡 Lưu ý: Hãy vào 'Quản lý lương' và nhấn 'Cập nhật chấm công' để đồng bộ bảng lương.", 
                         Alert.AlertType.INFORMATION);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupTableSelection() {
        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFormFromSelectedRecord(newSelection);
                btnDeleteSchedule.setDisable(false);
            } else {
                btnDeleteSchedule.setDisable(true);
            }
        });
        
        btnDeleteSchedule.setDisable(true);
    }

    private void fillFormFromSelectedRecord(ShiftRecord record) {
        dpWorkDate.setValue(LocalDate.parse(record.getWorkDate()));
        cbShift.setValue(record.getShift());
        tfScheduleEmployeeId.setText(record.getEmployeeId());
        cbShift1.setValue(record.getAttendanceStatus());
    }

    @FXML
    private void handleAddSchedule() {
        LocalDate workDate = dpWorkDate.getValue();
        String shift = cbShift.getValue();
        String employeeId = tfScheduleEmployeeId.getText().trim();
        String attendanceStatus = cbShift1.getValue();
        
        if (workDate == null || shift == null || employeeId.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin!", Alert.AlertType.WARNING);
            return;
        }
        
        if (!isEmployeeExists(employeeId)) {
            showAlert("Mã nhân viên không tồn tại!", Alert.AlertType.ERROR);
            return;
        }
        
        if (isShiftExists(employeeId, workDate, shift)) {
            showAlert("Nhân viên đã được xếp lịch cho ca này!", Alert.AlertType.WARNING);
            return;
        }
        
        addShiftRecord(employeeId, workDate, shift, attendanceStatus);
        clearForm();
        loadShiftData();
    }

    @FXML
    private void handleDeleteSchedule() {
        ShiftRecord selectedRecord = scheduleTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord != null) {
            deleteSelectedRecord(selectedRecord);
        } else {
            deleteByFormData();
        }
    }

    private void deleteSelectedRecord(ShiftRecord record) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa lịch làm việc này?");
        confirmAlert.setContentText(
            "Nhân viên: " + record.getEmployeeName() + " (" + record.getEmployeeId() + ")\n" +
            "Ngày: " + record.getWorkDate() + "\n" +
            "Ca: " + record.getShift() + "\n" +
            "Trạng thái: " + record.getAttendanceStatus()
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteShiftRecord(record.getEmployeeId(), LocalDate.parse(record.getWorkDate()), record.getShift());
        }
    }

    private void deleteByFormData() {
        LocalDate workDate = dpWorkDate.getValue();
        String shift = cbShift.getValue();
        String employeeId = tfScheduleEmployeeId.getText().trim();
        
        if (workDate == null || shift == null || employeeId.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin để xóa!", Alert.AlertType.WARNING);
            return;
        }
        
        if (!isShiftExists(employeeId, workDate, shift)) {
            showAlert("Không tìm thấy lịch làm việc để xóa!", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa lịch làm việc này?");
        confirmAlert.setContentText(
            "Nhân viên: " + employeeId + "\n" +
            "Ngày: " + workDate + "\n" +
            "Ca: " + shift
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteShiftRecord(employeeId, workDate, shift);
        }
    }

    // ✅ SỬA - method delete với syntax đúng
    private void deleteShiftRecord(String employeeId, LocalDate workDate, String shift) {
        // ✅ SỬA - Không dùng alias trong DELETE
        String deleteSql = "DELETE FROM Shift_Details " +
                          "WHERE employee_id = ? AND " +
                          "shift_id = (SELECT shift_id FROM Shifts WHERE date = ? AND time = ?)";
        
        Connection deleteConnect = null;
        PreparedStatement deletePrepare = null;
        
        try {
            deleteConnect = DatabaseConnection.getConnection();
            deletePrepare = deleteConnect.prepareStatement(deleteSql);
            deletePrepare.setInt(1, Integer.parseInt(employeeId));
            deletePrepare.setDate(2, java.sql.Date.valueOf(workDate));
            deletePrepare.setString(3, shift);
            
            int rowsAffected = deletePrepare.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("Xóa lịch làm việc thành công!", Alert.AlertType.INFORMATION);
                clearForm();
                loadShiftData();
                scheduleTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Không tìm thấy lịch để xóa!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi xóa: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(null, deletePrepare, deleteConnect);
        }
    }

    // ✅ SỬA - check employee exists với database mới
    private boolean isEmployeeExists(String employeeId) {
        String sql = "SELECT employee_id FROM Employees WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(employeeId));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ SỬA - check shift exists với database mới
    private boolean isShiftExists(String employeeId, LocalDate workDate, String shift) {
        String sql = "SELECT sd.employee_id FROM Shift_Details sd " +
                    "JOIN Shifts s ON sd.shift_id = s.shift_id " +
                    "WHERE sd.employee_id = ? AND s.date = ? AND s.time = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setDate(2, java.sql.Date.valueOf(workDate));
            stmt.setString(3, shift);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ SỬA - add shift record với database mới
    private void addShiftRecord(String employeeId, LocalDate workDate, String shift, String attendanceStatus) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Tìm hoặc tạo shift
            int shiftId = getOrCreateShift(conn, workDate, shift);
            
            // Thêm shift detail
            String insertDetailSql = "INSERT INTO Shift_Details (shift_id, employee_id, status) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertDetailSql)) {
                stmt.setInt(1, shiftId);
                stmt.setInt(2, Integer.parseInt(employeeId));
                stmt.setString(3, attendanceStatus);
                stmt.executeUpdate();
            }
            
            conn.commit();
            showAlert("Thêm lịch làm việc thành công!", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi thêm lịch: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getOrCreateShift(Connection conn, LocalDate workDate, String shift) throws Exception {
        // Tìm shift có sẵn
        String findSql = "SELECT shift_id FROM Shifts WHERE date = ? AND time = ?";
        try (PreparedStatement stmt = conn.prepareStatement(findSql)) {
            stmt.setDate(1, java.sql.Date.valueOf(workDate));
            stmt.setString(2, shift);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("shift_id");
                }
            }
        }
        
        // Tạo shift mới nếu chưa có
        String insertSql = "INSERT INTO Shifts (date, time) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, java.sql.Date.valueOf(workDate));
            stmt.setString(2, shift);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        throw new Exception("Cannot create shift");
    }

    // ✅ SỬA - load shift data với database schema đúng
    private void loadShiftData() {
        ObservableList<ShiftRecord> shiftList = FXCollections.observableArrayList();
        
        String sql = "SELECT s.date, s.time, sd.employee_id, u.full_name as employee_name, sd.status " +
                    "FROM Shifts s " +
                    "JOIN Shift_Details sd ON s.shift_id = sd.shift_id " +
                    "JOIN Employees e ON sd.employee_id = e.employee_id " +
                    "JOIN Users u ON e.employee_id = u.user_id " +
                    "ORDER BY s.date DESC, s.time, sd.employee_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ShiftRecord record = new ShiftRecord(
                    rs.getDate("date").toString(),
                    rs.getString("time"),
                    String.valueOf(rs.getInt("employee_id")),
                    rs.getString("employee_name"),
                    rs.getString("status")
                );
                shiftList.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        scheduleTable.setItems(shiftList);
    }

    private void clearForm() {
        tfScheduleEmployeeId.clear();
        cbShift.setValue(null);
        cbShift1.setValue("scheduled");
        dpWorkDate.setValue(LocalDate.now());
        scheduleTable.getSelectionModel().clearSelection();
    }

    private void closeConnection(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ THÊM - Methods còn thiếu
    @FXML
    private void handleClearForm() {
        clearForm();
        showAlert("Đã xóa form!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleRefreshData() {
        loadShiftData();
        showAlert("Đã làm mới dữ liệu!", Alert.AlertType.INFORMATION);
    }
}
