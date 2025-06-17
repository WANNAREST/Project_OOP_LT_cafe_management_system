package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;
import obj.Employee;
import obj.ShiftRecord;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @FXML private Button btnDeleteSchedule; // THÊM NÚT XÓA
    
    // Table
    @FXML private TableView<ShiftRecord> scheduleTable;
    @FXML private TableColumn<ShiftRecord, String> colScheduleDate;
    @FXML private TableColumn<ShiftRecord, String> colScheduleShift;
    @FXML private TableColumn<ShiftRecord, String> colScheduleEmployeeId;
    @FXML private TableColumn<ShiftRecord, String> colScheduleEmployeeName;
    @FXML private TableColumn<ShiftRecord, String> colScheduleStatus;
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpWorkDate.setValue(LocalDate.now());
        
        cbShift.getItems().addAll(
            "Ca sáng (7:00-11:00)",
            "Ca chiều (14:00-18:00)", 
            "Ca đêm (19:00-23:00)"
        );
        
        cbShift1.getItems().addAll("Đủ", "Muộn", "Vắng");
        
        setupTable();
        loadShiftData();
        setupEmployeeAutoComplete();
        
        // THÊM LISTENER CHO TABLE SELECTION
        setupTableSelection();
    }

    // THÊM METHOD SETUP TABLE SELECTION
    private void setupTableSelection() {
        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Tự động điền form khi chọn 1 row trong table
                fillFormFromSelectedRecord(newSelection);
                btnDeleteSchedule.setDisable(false);
            } else {
                btnDeleteSchedule.setDisable(true);
            }
        });
        
        // Disable nút xóa ban đầu
        btnDeleteSchedule.setDisable(true);
    }

    // THÊM METHOD ĐIỀN FORM TỪ RECORD ĐƯỢC CHỌN
    private void fillFormFromSelectedRecord(ShiftRecord record) {
        dpWorkDate.setValue(LocalDate.parse(record.getWorkDate()));
        cbShift.setValue(record.getShift());
        tfScheduleEmployeeId.setText(record.getEmployeeId());
        cbShift1.setValue(record.getAttendanceStatus());
    }

    private void setupTable() {
        colScheduleDate.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        colScheduleShift.setCellValueFactory(new PropertyValueFactory<>("shift"));
        colScheduleEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colScheduleEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colScheduleStatus.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));
        
        colScheduleStatus.setCellFactory(ComboBoxTableCell.forTableColumn("Đủ", "Muộn", "Vắng"));
        colScheduleStatus.setOnEditCommit(event -> {
            ShiftRecord record = event.getRowValue();
            record.setAttendanceStatus(event.getNewValue());
            updateAttendanceStatus(record);
        });
        
        scheduleTable.setEditable(true);
    }

    private void setupEmployeeAutoComplete() {
        tfScheduleEmployeeId.setOnKeyReleased(event -> {
            String input = tfScheduleEmployeeId.getText();
            if (input.length() >= 2) {
                showEmployeesSuggestions(input);
            }
        });
    }

    private void showEmployeesSuggestions(String input) {
        ContextMenu contextMenu = new ContextMenu();
        String sql = "SELECT employee_id, employee_name FROM employee WHERE employee_id LIKE ? OR employee_name LIKE ? LIMIT 5";
        
        Connection empConnect = null;
        PreparedStatement empPrepare = null;
        ResultSet empResult = null;
        
        try {
            empConnect = database.connectDB();
            empPrepare = empConnect.prepareStatement(sql);
            empPrepare.setString(1, "%" + input + "%");
            empPrepare.setString(2, "%" + input + "%");
            empResult = empPrepare.executeQuery();
            
            while (empResult.next()) {
                String empId = empResult.getString("employee_id");
                String empName = empResult.getString("employee_name");
                
                MenuItem item = new MenuItem(empId + " - " + empName);
                item.setOnAction(e -> {
                    tfScheduleEmployeeId.setText(empId);
                    contextMenu.hide();
                });
                contextMenu.getItems().add(item);
            }
            
            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(tfScheduleEmployeeId, 
                    tfScheduleEmployeeId.localToScreen(0, tfScheduleEmployeeId.getHeight()).getX(),
                    tfScheduleEmployeeId.localToScreen(0, tfScheduleEmployeeId.getHeight()).getY());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(empResult, empPrepare, empConnect);
        }
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

    // THÊM METHOD XỬ LÝ XÓA LỊCH
    @FXML
    private void handleDeleteSchedule() {
        // CÁCH 1: XÓA THEO RECORD ĐƯỢC CHỌN TRONG TABLE
        ShiftRecord selectedRecord = scheduleTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord != null) {
            deleteSelectedRecord(selectedRecord);
        } else {
            // CÁCH 2: XÓA THEO THÔNG TIN TRONG FORM
            deleteByFormData();
        }
    }

    // METHOD XÓA RECORD ĐƯỢC CHỌN TRONG TABLE
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

    // METHOD XÓA THEO THÔNG TIN TRONG FORM
    private void deleteByFormData() {
        LocalDate workDate = dpWorkDate.getValue();
        String shift = cbShift.getValue();
        String employeeId = tfScheduleEmployeeId.getText().trim();
        
        if (workDate == null || shift == null || employeeId.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin để xóa!", Alert.AlertType.WARNING);
            return;
        }
        
        // Kiểm tra lịch có tồn tại không
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

    // METHOD XÓA TRONG DATABASE
    private void deleteShiftRecord(String employeeId, LocalDate workDate, String shift) {
        String sql = "DELETE FROM work_shifts WHERE employee_id = ? AND work_date = ? AND shift = ?";
        
        Connection deleteConnect = null;
        PreparedStatement deletePrepare = null;
        
        try {
            deleteConnect = database.connectDB();
            deletePrepare = deleteConnect.prepareStatement(sql);
            deletePrepare.setString(1, employeeId);
            deletePrepare.setDate(2, java.sql.Date.valueOf(workDate));
            deletePrepare.setString(3, shift);
            
            int rowsAffected = deletePrepare.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("Xóa lịch làm việc thành công!", Alert.AlertType.INFORMATION);
                clearForm();
                loadShiftData();
                
                // Clear selection trong table
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

    // CÁC METHOD KHÁC GIỮ NGUYÊN...
    private boolean isEmployeeExists(String employeeId) {
        String sql = "SELECT COUNT(*) FROM employee WHERE employee_id = ?";
        
        Connection empConnect = null;
        PreparedStatement empPrepare = null;
        ResultSet empResult = null;
        
        try {
            empConnect = database.connectDB();
            empPrepare = empConnect.prepareStatement(sql);
            empPrepare.setString(1, employeeId);
            empResult = empPrepare.executeQuery();
            
            if (empResult.next()) {
                return empResult.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(empResult, empPrepare, empConnect);
        }
        
        return false;
    }

    private boolean isShiftExists(String employeeId, LocalDate workDate, String shift) {
        String sql = "SELECT COUNT(*) FROM work_shifts WHERE employee_id = ? AND work_date = ? AND shift = ?";
        
        Connection shiftConnect = null;
        PreparedStatement shiftPrepare = null;
        ResultSet shiftResult = null;
        
        try {
            shiftConnect = database.connectDB();
            shiftPrepare = shiftConnect.prepareStatement(sql);
            shiftPrepare.setString(1, employeeId);
            shiftPrepare.setDate(2, java.sql.Date.valueOf(workDate));
            shiftPrepare.setString(3, shift);
            shiftResult = shiftPrepare.executeQuery();
            
            if (shiftResult.next()) {
                return shiftResult.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(shiftResult, shiftPrepare, shiftConnect);
        }
        
        return false;
    }

    private void addShiftRecord(String employeeId, LocalDate workDate, String shift, String attendanceStatus) {
        String sql = "INSERT INTO work_shifts (employee_id, work_date, shift, attendance_status, created_time) VALUES (?, ?, ?, ?, ?)";
        
        Connection addConnect = null;
        PreparedStatement addPrepare = null;
        
        try {
            addConnect = database.connectDB();
            addPrepare = addConnect.prepareStatement(sql);
            addPrepare.setString(1, employeeId);
            addPrepare.setDate(2, java.sql.Date.valueOf(workDate));
            addPrepare.setString(3, shift);
            addPrepare.setString(4, attendanceStatus != null ? attendanceStatus : "Chưa điểm danh");
            addPrepare.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            addPrepare.executeUpdate();
            showAlert("Thêm lịch làm việc thành công!", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(null, addPrepare, addConnect);
        }
    }

    private void loadShiftData() {
        ObservableList<ShiftRecord> data = FXCollections.observableArrayList();
        String sql = "SELECT ws.*, e.employee_name FROM work_shifts ws " +
                    "LEFT JOIN employee e ON ws.employee_id = e.employee_id " +
                    "ORDER BY ws.work_date DESC, ws.shift";
        
        Connection loadConnect = null;
        PreparedStatement loadPrepare = null;
        ResultSet loadResult = null;
        
        try {
            loadConnect = database.connectDB();
            loadPrepare = loadConnect.prepareStatement(sql);
            loadResult = loadPrepare.executeQuery();
            
            while (loadResult.next()) {
                ShiftRecord record = new ShiftRecord(
                    loadResult.getDate("work_date").toLocalDate().toString(),
                    loadResult.getString("shift"),
                    loadResult.getString("employee_id"),
                    loadResult.getString("employee_name") != null ? loadResult.getString("employee_name") : "N/A",
                    loadResult.getString("attendance_status") != null ? loadResult.getString("attendance_status") : "Chưa điểm danh"
                );
                data.add(record);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(loadResult, loadPrepare, loadConnect);
        }
        
        scheduleTable.setItems(data);
    }

    private void updateAttendanceStatus(ShiftRecord record) {
        String sql = "UPDATE work_shifts SET attendance_status = ?, updated_time = ? WHERE employee_id = ? AND work_date = ? AND shift = ?";
        
        Connection updateConnect = null;
        PreparedStatement updatePrepare = null;
        
        try {
            updateConnect = database.connectDB();
            updatePrepare = updateConnect.prepareStatement(sql);
            updatePrepare.setString(1, record.getAttendanceStatus());
            updatePrepare.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            updatePrepare.setString(3, record.getEmployeeId());
            updatePrepare.setDate(4, java.sql.Date.valueOf(LocalDate.parse(record.getWorkDate())));
            updatePrepare.setString(5, record.getShift());
            
            updatePrepare.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(null, updatePrepare, updateConnect);
        }
    }

    private void clearForm() {
        tfScheduleEmployeeId.clear();
        cbShift.setValue(null);
        cbShift1.setValue(null);
        dpWorkDate.setValue(LocalDate.now());
        
        // Clear table selection
        scheduleTable.getSelectionModel().clearSelection();
    }

    // THÊM METHOD CLOSE CONNECTION AN TOÀN
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
}