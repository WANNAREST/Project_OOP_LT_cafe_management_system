package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import obj.SalaryRecord;
import obj.database;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;
import java.util.ResourceBundle;

public class SalaryController implements Initializable {

    // ===== FXML Controls =====
    @FXML private ComboBox<String> cbMonth;
    @FXML private ComboBox<String> cbYear;
    @FXML private Button btnFilter;
    @FXML private Button btnAddNew;
    @FXML private Button btnDeleteSalary;
    @FXML private Button btnRefreshAttendance;
    @FXML private TextField tfEmployeeId;
    @FXML private Label lblSelectedMonth;
    
    // ===== TableView và Columns =====
    @FXML private TableView<SalaryRecord> salaryTable;
    @FXML private TableColumn<SalaryRecord, String> colEmployeeId;
    @FXML private TableColumn<SalaryRecord, String> colSalaryCode;
    @FXML private TableColumn<SalaryRecord, Double> colBasicSalary;
    @FXML private TableColumn<SalaryRecord, Integer> colPresent;
    @FXML private TableColumn<SalaryRecord, Integer> colLate;
    @FXML private TableColumn<SalaryRecord, Integer> colAbsent;
    @FXML private TableColumn<SalaryRecord, Double> colBonusPenalty;
    @FXML private TableColumn<SalaryRecord, Double> colTotalSalary;
    @FXML private TableColumn<SalaryRecord, String> colNote;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupTable();
        setupTableSelection();
        handleFilter();
    }

    private void setupComboBoxes() {
        // Setup months
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.valueOf(i));
        }
        
        // Setup years (current year ± 5 years)
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }
        
        // Set default values
        cbMonth.setValue(String.valueOf(java.time.LocalDate.now().getMonthValue()));
        cbYear.setValue(String.valueOf(currentYear));
        
        // Update label when selection changes
        cbMonth.valueProperty().addListener((obs, oldVal, newVal) -> updateSelectedMonthLabel());
        cbYear.valueProperty().addListener((obs, oldVal, newVal) -> updateSelectedMonthLabel());
        
        updateSelectedMonthLabel();
    }

    private void updateSelectedMonthLabel() {
        String month = cbMonth.getValue();
        String year = cbYear.getValue();
        if (month != null && year != null) {
            lblSelectedMonth.setText(month + "/" + year);
        }
    }

    private void setupTable() {
        // ✅ THIẾT LẬP EDITABLE TABLE
        salaryTable.setEditable(true);
        
        // Non-editable columns
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colSalaryCode.setCellValueFactory(new PropertyValueFactory<>("salaryCode"));
        colPresent.setCellValueFactory(new PropertyValueFactory<>("present"));
        colLate.setCellValueFactory(new PropertyValueFactory<>("late"));
        colAbsent.setCellValueFactory(new PropertyValueFactory<>("absent"));
        colTotalSalary.setCellValueFactory(new PropertyValueFactory<>("totalSalary"));
        
        // ✅ EDITABLE COLUMNS
        setupEditableBasicSalaryColumn();
        setupEditableBonusPenaltyColumn();
        setupEditableNoteColumn();
    }

    private void setupEditableBasicSalaryColumn() {
        colBasicSalary.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        colBasicSalary.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        colBasicSalary.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            double newBasicSalary = event.getNewValue();
            double oldBasicSalary = event.getOldValue();
            
            if (newBasicSalary <= 0) {
                showAlert("Lương cơ bản phải lớn hơn 0!", Alert.AlertType.ERROR);
                record.setBasicSalary(oldBasicSalary);
                salaryTable.refresh();
                return;
            }
            
            // Cập nhật database
            if (updateBasicSalary(record, newBasicSalary)) {
                record.setBasicSalary(newBasicSalary);
                // Tự động tính lại tổng lương
                recalculateTotalSalary(record);
                showAlert("Cập nhật lương cơ bản thành công!", Alert.AlertType.INFORMATION);
            } else {
                record.setBasicSalary(oldBasicSalary);
                salaryTable.refresh();
                showAlert("Lỗi cập nhật lương cơ bản!", Alert.AlertType.ERROR);
            }
        });
    }

    private void setupEditableBonusPenaltyColumn() {
        colBonusPenalty.setCellValueFactory(new PropertyValueFactory<>("bonusPenalty"));
        colBonusPenalty.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        colBonusPenalty.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            double newBonusPenalty = event.getNewValue();
            double oldBonusPenalty = event.getOldValue();
            
            // Cập nhật database
            if (updateBonusPenalty(record, newBonusPenalty)) {
                record.setBonusPenalty(newBonusPenalty);
                // Tự động tính lại tổng lương
                recalculateTotalSalary(record);
                showAlert("Cập nhật thưởng/phạt thành công!", Alert.AlertType.INFORMATION);
            } else {
                record.setBonusPenalty(oldBonusPenalty);
                salaryTable.refresh();
                showAlert("Lỗi cập nhật thưởng/phạt!", Alert.AlertType.ERROR);
            }
        });
    }

    private void setupEditableNoteColumn() {
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colNote.setCellFactory(TextFieldTableCell.forTableColumn());
        
        colNote.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            String newNote = event.getNewValue();
            String oldNote = event.getOldValue();
            
            // Cập nhật database
            if (updateNote(record, newNote)) {
                record.setNote(newNote);
                showAlert("Cập nhật ghi chú thành công!", Alert.AlertType.INFORMATION);
            } else {
                record.setNote(oldNote);
                salaryTable.refresh();
                showAlert("Lỗi cập nhật ghi chú!", Alert.AlertType.ERROR);
            }
        });
    }

    // ✅ CÁC METHOD CẬP NHẬT DATABASE
    private boolean updateBasicSalary(SalaryRecord record, double newBasicSalary) {
        String sql = "UPDATE Salary SET base_salary = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newBasicSalary);
            stmt.setInt(2, Integer.parseInt(record.getSalaryCode()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateBonusPenalty(SalaryRecord record, double newBonusPenalty) {
        String sql = "UPDATE Salary SET bonus = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newBonusPenalty);
            stmt.setInt(2, Integer.parseInt(record.getSalaryCode()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateNote(SalaryRecord record, String newNote) {
        String sql = "UPDATE Salary SET note = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newNote);
            stmt.setInt(2, Integer.parseInt(record.getSalaryCode()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ TỰ ĐỘNG TÍNH LẠI TỔNG LƯƠNG
    private void recalculateTotalSalary(SalaryRecord record) {
        // Công thức: lương = lương cơ bản * ngày đi đủ + (lương cơ bản - 10000) * ngày đi muộn + thưởng/phạt
        double basicSalary = record.getBasicSalary();
        int presentDays = record.getPresent();
        int lateDays = record.getLate();
        double bonusPenalty = record.getBonusPenalty();
        
        double totalSalary = (basicSalary * presentDays) + 
                           ((basicSalary - 10000) * lateDays) + 
                           bonusPenalty;
        
        // Đảm bảo không âm
        totalSalary = Math.max(0, totalSalary);
        
        // Cập nhật database
        String sql = "UPDATE Salary SET total = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, totalSalary);
            stmt.setInt(2, Integer.parseInt(record.getSalaryCode()));
            
            if (stmt.executeUpdate() > 0) {
                record.setTotalSalary(totalSalary);
                salaryTable.refresh();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ CẬP NHẬT SỐ NGÀY CHẤM CÔNG TỪ SHIFT
    @FXML
    private void handleRefreshAttendance() {
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        if (selectedMonth == null || selectedYear == null) {
            showAlert("Vui lòng chọn tháng và năm!", Alert.AlertType.WARNING);
            return;
        }
        
        int month = Integer.parseInt(selectedMonth);
        int year = Integer.parseInt(selectedYear);
        
        // Cập nhật chấm công cho tất cả nhân viên trong tháng
        updateAllAttendanceFromShifts(month, year);
        
        // Refresh table
        handleFilter();
        
        showAlert("Đã cập nhật chấm công từ bảng điểm danh!", Alert.AlertType.INFORMATION);
    }

    private void updateAllAttendanceFromShifts(int month, int year) {
        // Lấy danh sách tất cả salary records trong tháng
        String selectSql = "SELECT salary_id, employee_id FROM Salary WHERE month = ? AND year = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, month);
            selectStmt.setInt(2, year);
            
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    int salaryId = rs.getInt("salary_id");
                    int employeeId = rs.getInt("employee_id");
                    
                    // Tính chấm công từ shifts
                    int[] attendance = calculateDetailedAttendanceFromShifts(employeeId, month, year);
                    int completedShifts = attendance[0];  // completed
                    int lateShifts = attendance[1];       // late
                    int absentShifts = attendance[2];     // absent
                    
                    // Cập nhật vào database
                    updateAttendanceInDB(salaryId, completedShifts, lateShifts, absentShifts);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ TÍNH CHẤM CÔNG CHI TIẾT TỪ SHIFTS
    private int[] calculateDetailedAttendanceFromShifts(int employeeId, int month, int year) {
        int completedShifts = 0;
        int lateShifts = 0;
        int absentShifts = 0;
        
        String sql = "SELECT sd.status, COUNT(*) as count_shifts " +
                    "FROM Shift_Details sd " +
                    "JOIN Shifts s ON sd.shift_id = s.shift_id " +
                    "WHERE sd.employee_id = ? AND MONTH(s.date) = ? AND YEAR(s.date) = ? " +
                    "GROUP BY sd.status";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count_shifts");
                    
                    switch (status) {
                        case "completed":
                            completedShifts = count;
                            break;
                        case "late":
                            lateShifts = count;
                            break;
                        case "absent":
                            absentShifts = count;
                            break;
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new int[]{completedShifts, lateShifts, absentShifts};
    }

    private void updateAttendanceInDB(int salaryId, int present, int late, int absent) {
        String sql = "UPDATE Salary SET working_days = ?, late_days = ?, absent_days = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, present);
            stmt.setInt(2, late);
            stmt.setInt(3, absent);
            stmt.setInt(4, salaryId);
            
            stmt.executeUpdate();
            
            // Sau khi cập nhật chấm công, tính lại lương
            recalculateTotalSalaryInDB(salaryId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recalculateTotalSalaryInDB(int salaryId) {
        String selectSql = "SELECT base_salary, working_days, late_days, bonus FROM Salary WHERE salary_id = ?";
        String updateSql = "UPDATE Salary SET total = ? WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            selectStmt.setInt(1, salaryId);
            
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    double baseSalary = rs.getDouble("base_salary");
                    int workingDays = rs.getInt("working_days");
                    int lateDays = rs.getInt("late_days");
                    double bonus = rs.getDouble("bonus");
                    
                    // Công thức tính lương
                    double total = (baseSalary * workingDays) + 
                                  ((baseSalary - 10000) * lateDays) + 
                                  bonus;
                    
                    total = Math.max(0, total);
                    
                    updateStmt.setDouble(1, total);
                    updateStmt.setInt(2, salaryId);
                    updateStmt.executeUpdate();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableSelection() {
        salaryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFormFromSelectedRecord(newSelection);
                btnDeleteSalary.setDisable(false);
            } else {
                btnDeleteSalary.setDisable(true);
            }
        });
        
        btnDeleteSalary.setDisable(true);
    }

    private void fillFormFromSelectedRecord(SalaryRecord record) {
        tfEmployeeId.setText(record.getEmployeeId());
        fillMonthYearFromRecord(record.getSalaryCode());
    }

    private void fillMonthYearFromRecord(String salaryCode) {
        String sql = "SELECT month, year FROM Salary WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(salaryCode));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cbMonth.setValue(String.valueOf(rs.getInt("month")));
                    cbYear.setValue(String.valueOf(rs.getInt("year")));
                    updateSelectedMonthLabel();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilter() {
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        if (selectedMonth == null || selectedYear == null) {
            showAlert("Vui lòng chọn tháng và năm!", Alert.AlertType.WARNING);
            return;
        }
        
        ObservableList<SalaryRecord> salaryData = getSalaryData(
            Integer.parseInt(selectedMonth), 
            Integer.parseInt(selectedYear)
        );
        salaryTable.setItems(salaryData);
    }

    @FXML
    private void handleAddNew() {
        String employeeId = tfEmployeeId.getText().trim();
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        if (employeeId.isEmpty()) {
            showAlert("Vui lòng nhập mã nhân viên!", Alert.AlertType.WARNING);
            tfEmployeeId.requestFocus();
            return;
        }
        
        if (selectedMonth == null || selectedYear == null) {
            showAlert("Vui lòng chọn tháng và năm!", Alert.AlertType.WARNING);
            return;
        }
        
        if (!isEmployeeExists(employeeId)) {
            showAlert("Mã nhân viên '" + employeeId + "' không tồn tại!", Alert.AlertType.ERROR);
            tfEmployeeId.requestFocus();
            return;
        }
        
        try {
            int workMonth = Integer.parseInt(selectedMonth);
            int workYear = Integer.parseInt(selectedYear);
            
            if (isSalaryRecordExistsForMonth(employeeId, workMonth, workYear)) {
                showAlert("Nhân viên " + employeeId + " đã có bảng lương cho tháng " + selectedMonth + "/" + selectedYear + "!", Alert.AlertType.WARNING);
                return;
            }
            
            createSalaryRecordFromComboBox(employeeId, workMonth, workYear);
            tfEmployeeId.clear();
            salaryTable.getSelectionModel().clearSelection();
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi format tháng/năm!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteSalary() {
        SalaryRecord selectedRecord = salaryTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord != null) {
            deleteSelectedSalaryRecord(selectedRecord);
        } else {
            deleteSalaryByFormData();
        }
    }

    private void deleteSelectedSalaryRecord(SalaryRecord record) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa bảng lương");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa bảng lương này?");
        confirmAlert.setContentText(
            "Mã bảng lương: " + record.getSalaryCode() + "\n" +
            "Nhân viên: " + record.getEmployeeId() + "\n" +
            "Lương thực tế: " + String.format("%,.0f", record.getTotalSalary()) + " VND\n\n" +
            "⚠️ Hành động này không thể hoàn tác!"
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteSalaryRecordFromDB(record.getSalaryCode());
        }
    }

    private void deleteSalaryByFormData() {
        String employeeId = tfEmployeeId.getText().trim();
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        if (employeeId.isEmpty() || selectedMonth == null || selectedYear == null) {
            showAlert("Vui lòng điền đầy đủ thông tin hoặc chọn bảng lương từ danh sách để xóa!", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            int workMonth = Integer.parseInt(selectedMonth);
            int workYear = Integer.parseInt(selectedYear);
            
            String salaryCode = findSalaryRecord(employeeId, workMonth, workYear);
            if (salaryCode == null) {
                showAlert("Không tìm thấy bảng lương cho nhân viên " + employeeId + 
                         " trong tháng " + selectedMonth + "/" + selectedYear + "!", Alert.AlertType.ERROR);
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa bảng lương");
            confirmAlert.setHeaderText("Bạn có chắc muốn xóa bảng lương này?");
            confirmAlert.setContentText(
                "Nhân viên: " + employeeId + "\n" +
                "Tháng: " + selectedMonth + "/" + selectedYear + "\n\n" +
                "⚠️ Hành động này không thể hoàn tác!"
            );

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteSalaryRecordFromDB(salaryCode);
            }
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi format tháng/năm!", Alert.AlertType.ERROR);
        }
    }

    private void deleteSalaryRecordFromDB(String salaryCode) {
        String sql = "DELETE FROM Salary WHERE salary_id = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(salaryCode));
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("✅ Xóa bảng lương thành công!", Alert.AlertType.INFORMATION);
                clearSalaryForm();
                handleFilter();
                salaryTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Không tìm thấy bảng lương để xóa!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi xóa bảng lương: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ✅ SỬA - Lấy dữ liệu lương theo schema mới với cột late_days
    private ObservableList<SalaryRecord> getSalaryData(int month, int year) {
        ObservableList<SalaryRecord> salaryList = FXCollections.observableArrayList();
        
        String sql = "SELECT s.salary_id, s.employee_id, u.full_name as employee_name, " +
                    "s.month, s.year, s.working_days, COALESCE(s.late_days, 0) as late_days, " +
                    "s.absent_days, s.base_salary, s.bonus, s.total, COALESCE(s.note, '') as note " +
                    "FROM Salary s " +
                    "JOIN Employees e ON s.employee_id = e.employee_id " +
                    "JOIN Users u ON e.employee_id = u.user_id " +
                    "WHERE s.month = ? AND s.year = ? " +
                    "ORDER BY s.employee_id";

        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalaryRecord record = new SalaryRecord(
                        String.valueOf(rs.getInt("salary_id")),
                        String.valueOf(rs.getInt("employee_id")),
                        rs.getString("employee_name"),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getInt("working_days"),
                        rs.getInt("late_days"),
                        rs.getInt("absent_days"),
                        rs.getDouble("base_salary"),
                        rs.getDouble("bonus"),
                        rs.getDouble("total"),
                        rs.getString("note")
                    );
                    salaryList.add(record);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu lương: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        return salaryList;
    }

    private boolean isEmployeeExists(String employeeId) {
        String sql = "SELECT employee_id FROM Employees WHERE employee_id = ?";
        
        try (Connection conn = database.connectDB();
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

    private boolean isSalaryRecordExistsForMonth(String employeeId, int month, int year) {
        String sql = "SELECT salary_id FROM Salary WHERE employee_id = ? AND month = ? AND year = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ SỬA - Tạo bảng lương mới với late_days
    private void createSalaryRecordFromComboBox(String employeeId, int month, int year) {
        try {
            // Tính toán ngày làm việc từ Shift_Details
            int[] attendance = calculateDetailedAttendanceFromShifts(Integer.parseInt(employeeId), month, year);
            int workingDays = attendance[0];  // completed
            int lateDays = attendance[1];     // late
            int absentDays = attendance[2];   // absent
            
            // Lương cơ bản mặc định
            double baseSalary = 200000; // 200k/ngày
            double bonus = 0; // Mặc định 0
            
            // Tính lương theo công thức
            double total = (baseSalary * workingDays) + 
                          ((baseSalary - 10000) * lateDays) + 
                          bonus;
            total = Math.max(0, total);
            
            String sql = "INSERT INTO Salary (employee_id, month, year, working_days, late_days, absent_days, base_salary, bonus, total, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = database.connectDB();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, Integer.parseInt(employeeId));
                stmt.setInt(2, month);
                stmt.setInt(3, year);
                stmt.setInt(4, workingDays);
                stmt.setInt(5, lateDays);
                stmt.setInt(6, absentDays);
                stmt.setDouble(7, baseSalary);
                stmt.setDouble(8, bonus);
                stmt.setDouble(9, total);
                stmt.setString(10, "Tạo tự động từ " + month + "/" + year);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    showAlert("✅ Tạo bảng lương thành công!\n" +
                             "Ngày làm đủ: " + workingDays + "\n" +
                             "Ngày đi muộn: " + lateDays + "\n" +
                             "Ngày vắng: " + absentDays + "\n" +
                             "Tổng lương: " + String.format("%,.0f VND", total), 
                             Alert.AlertType.INFORMATION);
                    
                    handleFilter(); // Refresh data
                } else {
                    showAlert("Lỗi tạo bảng lương!", Alert.AlertType.ERROR);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tạo bảng lương: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String findSalaryRecord(String employeeId, int month, int year) {
        String sql = "SELECT salary_id FROM Salary WHERE employee_id = ? AND month = ? AND year = ?";
        
        try (Connection conn = database.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(employeeId));
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return String.valueOf(rs.getInt("salary_id"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private void clearSalaryForm() {
        tfEmployeeId.clear();
        cbMonth.setValue(String.valueOf(LocalDate.now().getMonthValue()));
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        updateSelectedMonthLabel();
        salaryTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}