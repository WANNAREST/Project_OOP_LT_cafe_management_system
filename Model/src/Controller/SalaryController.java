package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import obj.SalaryRecord;
import obj.WorkSummary;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class SalaryController implements Initializable {

    // ===== FXML Controls =====
    @FXML private ComboBox<String> cbMonth;
    @FXML private ComboBox<String> cbYear;
    @FXML private Button btnFilter;
    @FXML private Button btnAddNew;
    @FXML private Button btnDeleteSalary; // THÊM NÚT XÓA
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
        setupAddSalaryForm();
        setupTableColumns();
        enableEditing();
        loadSalaryData();
        setupTableSelection(); // THÊM METHOD NÀY
    }

    private void setupComboBoxes() {
        // Setup months
        for (int i = 1; i <= 12; i++) {
            cbMonth.getItems().add(String.format("%02d", i));
        }
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        
        // Setup years
        for (int i = 2020; i <= 2030; i++) {
            cbYear.getItems().add(String.valueOf(i));
        }
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        
        // Add listeners
        cbMonth.setOnAction(e -> updateSelectedMonthLabel());
        cbYear.setOnAction(e -> updateSelectedMonthLabel());
        
        updateSelectedMonthLabel();
    }
    
    private void setupAddSalaryForm() {
        setupEmployeeAutoComplete();
    }
    
    private void updateSelectedMonthLabel() {
        String month = cbMonth.getValue();
        String year = cbYear.getValue();
        
        if (month != null && year != null) {
            lblSelectedMonth.setText(month + "/" + year);
        }
    }
    
    private void setupEmployeeAutoComplete() {
        tfEmployeeId.setOnKeyReleased(event -> {
            String input = tfEmployeeId.getText();
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
                    tfEmployeeId.setText(empId);
                    contextMenu.hide();
                });
                contextMenu.getItems().add(item);
            }
            
            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(tfEmployeeId, 
                    tfEmployeeId.localToScreen(0, tfEmployeeId.getHeight()).getX(),
                    tfEmployeeId.localToScreen(0, tfEmployeeId.getHeight()).getY());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (empResult != null) empResult.close();
                if (empPrepare != null) empPrepare.close();
                if (empConnect != null) empConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===== SETUP TABLE COLUMNS =====
    private void setupTableColumns() {
        salaryTable.setEditable(true);
        
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colSalaryCode.setCellValueFactory(new PropertyValueFactory<>("salaryCode"));
        colBasicSalary.setCellValueFactory(new PropertyValueFactory<>("basicSalaryPerShift"));
        colPresent.setCellValueFactory(new PropertyValueFactory<>("present"));
        colLate.setCellValueFactory(new PropertyValueFactory<>("late"));
        colAbsent.setCellValueFactory(new PropertyValueFactory<>("absent"));
        colBonusPenalty.setCellValueFactory(new PropertyValueFactory<>("bonusPenalty"));
        colTotalSalary.setCellValueFactory(new PropertyValueFactory<>("totalSalary"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
    }

    // ===== ENABLE EDITING =====
    private void enableEditing() {
        // Basic Salary - editable
        colBasicSalary.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBasicSalary.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            double newBasicSalary = event.getNewValue();
            
            updateOneColumnInDB(record.getSalaryCode(), "basic_salary_per_shift", String.valueOf(newBasicSalary));
            record.setBasicSalaryPerShift(newBasicSalary);
            
            // Recalculate total salary
            recalculateTotalSalary(record);
            salaryTable.refresh();
        });

        // Bonus/Penalty - editable
        colBonusPenalty.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBonusPenalty.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            double newBonusPenalty = event.getNewValue();
            
            updateOneColumnInDB(record.getSalaryCode(), "bonus_penalty", String.valueOf(newBonusPenalty));
            record.setBonusPenalty(newBonusPenalty);
            
            // Recalculate total salary
            recalculateTotalSalary(record);
            salaryTable.refresh();
        });

        // Note - editable
        colNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colNote.setOnEditCommit(event -> {
            SalaryRecord record = event.getRowValue();
            String newNote = event.getNewValue();
            
            updateOneColumnInDB(record.getSalaryCode(), "note", "'" + newNote + "'");
            record.setNote(newNote);
        });
    }

    // ===== RECALCULATE TOTAL SALARY =====
    private void recalculateTotalSalary(SalaryRecord record) {
        double latePenalty = 5000;
        double absentPenalty = 10000;
        
        double newTotalSalary = record.getPresent() * record.getBasicSalaryPerShift() + 
                               record.getLate() * (record.getBasicSalaryPerShift() - latePenalty) - 
                               record.getAbsent() * absentPenalty + 
                               record.getBonusPenalty();
        
        updateOneColumnInDB(record.getSalaryCode(), "total_salary", String.valueOf(newTotalSalary));
        record.setTotalSalary(newTotalSalary);
    }

    // ===== DATABASE METHODS =====
    private void updateOneColumnInDB(String salaryCode, String columnName, String value) {
        String updateSQL = "UPDATE salary_records SET " + columnName + " = " + value + 
                          " WHERE salary_code = ?";
        
        Connection updateConnect = null;
        PreparedStatement updatePrepare = null;
        
        try {
            updateConnect = database.connectDB();
            updatePrepare = updateConnect.prepareStatement(updateSQL);
            updatePrepare.setString(1, salaryCode);
            updatePrepare.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (updatePrepare != null) updatePrepare.close();
                if (updateConnect != null) updateConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===== EVENT HANDLERS =====
    @FXML
    private void handleFilter() {
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        if (selectedMonth != null && selectedYear != null) {
            ObservableList<SalaryRecord> filteredList = getFilteredSalaryData(selectedMonth, selectedYear);
            salaryTable.setItems(filteredList);
            
            int recordCount = filteredList.size();
            System.out.println("Found " + recordCount + " salary records for " + selectedMonth + "/" + selectedYear);
        } else {
            loadSalaryData();
        }
    }

    @FXML
    private void handleAddNew() {
        String employeeId = tfEmployeeId.getText().trim();
        String selectedMonth = cbMonth.getValue();
        String selectedYear = cbYear.getValue();
        
        // Validation
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
            
            // Clear chỉ employee ID, giữ nguyên tháng/năm để tiện add tiếp
            tfEmployeeId.clear();
            salaryTable.getSelectionModel().clearSelection();
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi format tháng/năm!", Alert.AlertType.ERROR);
        }
    }

    // ===== CREATE SALARY RECORD =====
    private void createSalaryRecordFromComboBox(String employeeId, int workMonth, int workYear) {
        String salaryCode = "SAL" + workMonth + workYear + System.currentTimeMillis();
        double basicSalary = getEmployeeBasicSalary(employeeId);
        
        String insertSQL = "INSERT INTO salary_records (salary_code, employee_id, present, late, absent, basic_salary_per_shift, bonus_penalty, note, total_salary, work_month, work_year, date_created) VALUES (?, ?, 0, 0, 0, ?, 0, '', 0, ?, ?, NOW())";
        
        Connection createConnect = null;
        PreparedStatement createPrepare = null;
        
        try {
            createConnect = database.connectDB();
            createPrepare = createConnect.prepareStatement(insertSQL);
            createPrepare.setString(1, salaryCode);
            createPrepare.setString(2, employeeId);
            createPrepare.setDouble(3, basicSalary);
            createPrepare.setInt(4, workMonth);
            createPrepare.setInt(5, workYear);
            
            createPrepare.executeUpdate();
            
            // Auto update attendance
            updateSalaryRecordWithAttendanceSafe(salaryCode, employeeId, workMonth, workYear);
            
            showAlert("✅ Tạo bảng lương thành công!\nNhân viên: " + employeeId + "\nTháng: " + workMonth + "/" + workYear + "\nĐiểm danh đã được cập nhật từ lịch làm việc.", Alert.AlertType.INFORMATION);
            
            handleFilter(); // Auto filter
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tạo bảng lương: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (createPrepare != null) createPrepare.close();
                if (createConnect != null) createConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===== AUTO UPDATE ATTENDANCE =====
    private void updateSalaryRecordWithAttendanceSafe(String salaryCode, String employeeId, int month, int year) {
        AttendanceSummary attendance = calculateAttendanceFromShiftsSafe(employeeId, month, year);
        
        String updateSQL = "UPDATE salary_records SET present = ?, late = ?, absent = ? WHERE salary_code = ?";
        
        Connection updateConnect = null;
        PreparedStatement updatePrepare = null;
        
        try {
            updateConnect = database.connectDB();
            updatePrepare = updateConnect.prepareStatement(updateSQL);
            updatePrepare.setInt(1, attendance.present);
            updatePrepare.setInt(2, attendance.late);
            updatePrepare.setInt(3, attendance.absent);
            updatePrepare.setString(4, salaryCode);
            
            updatePrepare.executeUpdate();
            
            // Update total salary
            updateTotalSalaryInDBSafe(salaryCode);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (updatePrepare != null) updatePrepare.close();
                if (updateConnect != null) updateConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AttendanceSummary calculateAttendanceFromShiftsSafe(String employeeId, int month, int year) {
        AttendanceSummary summary = new AttendanceSummary();
        
        String sql = "SELECT attendance_status, COUNT(*) as count FROM work_shifts " +
                    "WHERE employee_id = ? AND MONTH(work_date) = ? AND YEAR(work_date) = ? " +
                    "GROUP BY attendance_status";
        
        Connection shiftConnect = null;
        PreparedStatement shiftPrepare = null;
        ResultSet shiftResult = null;
        
        try {
            shiftConnect = database.connectDB();
            shiftPrepare = shiftConnect.prepareStatement(sql);
            shiftPrepare.setString(1, employeeId);
            shiftPrepare.setInt(2, month);
            shiftPrepare.setInt(3, year);
            shiftResult = shiftPrepare.executeQuery();
            
            while (shiftResult.next()) {
                String status = shiftResult.getString("attendance_status");
                int count = shiftResult.getInt("count");
                
                switch (status) {
                    case "Đủ":
                        summary.present = count;
                        break;
                    case "Muộn":
                        summary.late = count;
                        break;
                    case "Vắng":
                        summary.absent = count;
                        break;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (shiftResult != null) shiftResult.close();
                if (shiftPrepare != null) shiftPrepare.close();
                if (shiftConnect != null) shiftConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return summary;
    }

    private void updateTotalSalaryInDBSafe(String salaryCode) {
        String selectSQL = "SELECT present, late, absent, basic_salary_per_shift, bonus_penalty FROM salary_records WHERE salary_code = ?";
        
        Connection totalConnect = null;
        PreparedStatement totalPrepare = null;
        ResultSet totalResult = null;
        
        try {
            totalConnect = database.connectDB();
            totalPrepare = totalConnect.prepareStatement(selectSQL);
            totalPrepare.setString(1, salaryCode);
            totalResult = totalPrepare.executeQuery();
            
            if (totalResult.next()) {
                int present = totalResult.getInt("present");
                int late = totalResult.getInt("late");
                int absent = totalResult.getInt("absent");
                double basicSalary = totalResult.getDouble("basic_salary_per_shift");
                double bonusPenalty = totalResult.getDouble("bonus_penalty");
                
                double latePenalty = 5000;
                double absentPenalty = 10000;
                double newTotalSalary = present * basicSalary + 
                                       late * (basicSalary - latePenalty) - 
                                       absent * absentPenalty + 
                                       bonusPenalty;
                
                String updateSQL = "UPDATE salary_records SET total_salary = ? WHERE salary_code = ?";
                PreparedStatement updateStmt = totalConnect.prepareStatement(updateSQL);
                updateStmt.setDouble(1, newTotalSalary);
                updateStmt.setString(2, salaryCode);
                updateStmt.executeUpdate();
                updateStmt.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (totalResult != null) totalResult.close();
                if (totalPrepare != null) totalPrepare.close();
                if (totalConnect != null) totalConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===== LOAD DATA METHODS =====
    private void loadSalaryData() {
        salaryTable.setItems(getSalaryData());
    }

    private ObservableList<SalaryRecord> getSalaryData() {
        ObservableList<SalaryRecord> salaryList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM salary_records ORDER BY work_year DESC, work_month DESC, date_created DESC";
        
        Connection mainConnect = null;
        PreparedStatement mainPrepare = null;
        ResultSet mainResult = null;
        
        try {
            mainConnect = database.connectDB();
            mainPrepare = mainConnect.prepareStatement(sql);
            mainResult = mainPrepare.executeQuery();
            
            while (mainResult.next()) {
                String salaryCode = mainResult.getString("salary_code");
                String employeeId = mainResult.getString("employee_id");
                double basicSalary = mainResult.getDouble("basic_salary_per_shift");
                double bonusPenalty = mainResult.getDouble("bonus_penalty");
                String note = mainResult.getString("note") != null ? mainResult.getString("note") : "";
                
                int workMonth = mainResult.getInt("work_month");
                int workYear = mainResult.getInt("work_year");
                
                if (workMonth == 0 || workYear == 0) {
                    if (mainResult.getTimestamp("date_created") != null) {
                        LocalDate created = mainResult.getTimestamp("date_created").toLocalDateTime().toLocalDate();
                        workMonth = created.getMonthValue();
                        workYear = created.getYear();
                    } else {
                        workMonth = LocalDate.now().getMonthValue();
                        workYear = LocalDate.now().getYear();
                    }
                }
                
                updateSalaryRecordWithAttendanceSafe(salaryCode, employeeId, workMonth, workYear);
                
                WorkSummary summary = new WorkSummary(employeeId);
                SalaryRecord record = new SalaryRecord(salaryCode, summary, basicSalary, bonusPenalty, note);
                
                AttendanceData attendanceData = getUpdatedAttendanceData(salaryCode);
                record.setPresent(attendanceData.present);
                record.setLate(attendanceData.late);
                record.setAbsent(attendanceData.absent);
                record.setTotalSalary(attendanceData.totalSalary);
                
                salaryList.add(record);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mainResult != null) mainResult.close();
                if (mainPrepare != null) mainPrepare.close();
                if (mainConnect != null) mainConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return salaryList;
    }

    private ObservableList<SalaryRecord> getFilteredSalaryData(String month, String year) {
        ObservableList<SalaryRecord> filteredList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM salary_records WHERE work_month = ? AND work_year = ?";
        
        Connection filterConnect = null;
        PreparedStatement filterPrepare = null;
        ResultSet filterResult = null;
        
        try {
            filterConnect = database.connectDB();
            filterPrepare = filterConnect.prepareStatement(sql);
            filterPrepare.setInt(1, Integer.parseInt(month));
            filterPrepare.setInt(2, Integer.parseInt(year));
            filterResult = filterPrepare.executeQuery();
            
            while (filterResult.next()) {
                String salaryCode = filterResult.getString("salary_code");
                String employeeId = filterResult.getString("employee_id");
                double basicSalary = filterResult.getDouble("basic_salary_per_shift");
                double bonusPenalty = filterResult.getDouble("bonus_penalty");
                String note = filterResult.getString("note") != null ? filterResult.getString("note") : "";
                
                int workMonth = filterResult.getInt("work_month");
                int workYear = filterResult.getInt("work_year");
                
                updateSalaryRecordWithAttendanceSafe(salaryCode, employeeId, workMonth, workYear);
                
                WorkSummary summary = new WorkSummary(employeeId);
                SalaryRecord record = new SalaryRecord(salaryCode, summary, basicSalary, bonusPenalty, note);
                
                AttendanceData attendanceData = getUpdatedAttendanceData(salaryCode);
                record.setPresent(attendanceData.present);
                record.setLate(attendanceData.late);
                record.setAbsent(attendanceData.absent);
                record.setTotalSalary(attendanceData.totalSalary);
                
                filteredList.add(record);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (filterResult != null) filterResult.close();
                if (filterPrepare != null) filterPrepare.close();
                if (filterConnect != null) filterConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return filteredList;
    }

    private AttendanceData getUpdatedAttendanceData(String salaryCode) {
        AttendanceData data = new AttendanceData();
        String sql = "SELECT present, late, absent, total_salary FROM salary_records WHERE salary_code = ?";
        
        Connection dataConnect = null;
        PreparedStatement dataPrepare = null;
        ResultSet dataResult = null;
        
        try {
            dataConnect = database.connectDB();
            dataPrepare = dataConnect.prepareStatement(sql);
            dataPrepare.setString(1, salaryCode);
            dataResult = dataPrepare.executeQuery();
            
            if (dataResult.next()) {
                data.present = dataResult.getInt("present");
                data.late = dataResult.getInt("late");
                data.absent = dataResult.getInt("absent");
                data.totalSalary = dataResult.getDouble("total_salary");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataResult != null) dataResult.close();
                if (dataPrepare != null) dataPrepare.close();
                if (dataConnect != null) dataConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return data;
    }

    // ===== VALIDATION METHODS =====
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
            try {
                if (empResult != null) empResult.close();
                if (empPrepare != null) empPrepare.close();
                if (empConnect != null) empConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }

    private boolean isSalaryRecordExistsForMonth(String employeeId, int workMonth, int workYear) {
        String sql = "SELECT COUNT(*) FROM salary_records WHERE employee_id = ? AND work_month = ? AND work_year = ?";
        
        Connection salConnect = null;
        PreparedStatement salPrepare = null;
        ResultSet salResult = null;
        
        try {
            salConnect = database.connectDB();
            salPrepare = salConnect.prepareStatement(sql);
            salPrepare.setString(1, employeeId);
            salPrepare.setInt(2, workMonth);
            salPrepare.setInt(3, workYear);
            salResult = salPrepare.executeQuery();
            
            if (salResult.next()) {
                return salResult.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (salResult != null) salResult.close();
                if (salPrepare != null) salPrepare.close();
                if (salConnect != null) salConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }

    private double getEmployeeBasicSalary(String employeeId) {
        String sql = "SELECT base_salary FROM employee WHERE employee_id = ?";
        double basicSalary = 200000;
        
        Connection baseSalConnect = null;
        PreparedStatement baseSalPrepare = null;
        ResultSet baseSalResult = null;
        
        try {
            baseSalConnect = database.connectDB();
            baseSalPrepare = baseSalConnect.prepareStatement(sql);
            baseSalPrepare.setString(1, employeeId);
            baseSalResult = baseSalPrepare.executeQuery();
            
            if (baseSalResult.next()) {
                basicSalary = baseSalResult.getDouble("base_salary");
                if (basicSalary <= 0) basicSalary = 200000;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baseSalResult != null) baseSalResult.close();
                if (baseSalPrepare != null) baseSalPrepare.close();
                if (baseSalConnect != null) baseSalConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return basicSalary;
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== HELPER CLASSES =====
    private static class AttendanceSummary {
        int present = 0;
        int late = 0;
        int absent = 0;
    }

    private static class AttendanceData {
        int present = 0;
        int late = 0;
        int absent = 0;
        double totalSalary = 0;
    }

    // THÊM METHOD SETUP TABLE SELECTION
    private void setupTableSelection() {
        salaryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Tự động điền form khi chọn 1 row trong table
                fillFormFromSelectedRecord(newSelection);
                btnDeleteSalary.setDisable(false);
            } else {
                btnDeleteSalary.setDisable(true);
            }
        });
        
        // Disable nút xóa ban đầu
        btnDeleteSalary.setDisable(true);
    }

    // THÊM METHOD ĐIỀN FORM TỪ RECORD ĐƯỢC CHỌN
    private void fillFormFromSelectedRecord(SalaryRecord record) {
        tfEmployeeId.setText(record.getEmployeeId());
        
        // Tìm work_month và work_year từ database để set combo box
        fillMonthYearFromRecord(record.getSalaryCode());
    }

    private void fillMonthYearFromRecord(String salaryCode) {
        String sql = "SELECT work_month, work_year FROM salary_records WHERE salary_code = ?";
        
        Connection fillConnect = null;
        PreparedStatement fillPrepare = null;
        ResultSet fillResult = null;
        
        try {
            fillConnect = database.connectDB();
            fillPrepare = fillConnect.prepareStatement(sql);
            fillPrepare.setString(1, salaryCode);
            fillResult = fillPrepare.executeQuery();
            
            if (fillResult.next()) {
                int workMonth = fillResult.getInt("work_month");
                int workYear = fillResult.getInt("work_year");
                
                cbMonth.setValue(String.format("%02d", workMonth));
                cbYear.setValue(String.valueOf(workYear));
                updateSelectedMonthLabel();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fillResult != null) fillResult.close();
                if (fillPrepare != null) fillPrepare.close();
                if (fillConnect != null) fillConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // THÊM METHOD XỬ LÝ XÓA BẢNG LƯƠNG
    @FXML
    private void handleDeleteSalary() {
        // CÁCH 1: XÓA THEO RECORD ĐƯỢC CHỌN TRONG TABLE
        SalaryRecord selectedRecord = salaryTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord != null) {
            deleteSelectedSalaryRecord(selectedRecord);
        } else {
            // CÁCH 2: XÓA THEO THÔNG TIN TRONG FORM
            deleteSalaryByFormData();
        }
    }

    // METHOD XÓA RECORD ĐƯỢC CHỌN TRONG TABLE
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

    // METHOD XÓA THEO THÔNG TIN TRONG FORM
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
            
            // Tìm salary record
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

    // METHOD TÌM SALARY RECORD
    private String findSalaryRecord(String employeeId, int workMonth, int workYear) {
        String sql = "SELECT salary_code FROM salary_records WHERE employee_id = ? AND work_month = ? AND work_year = ?";
        
        Connection findConnect = null;
        PreparedStatement findPrepare = null;
        ResultSet findResult = null;
        
        try {
            findConnect = database.connectDB();
            findPrepare = findConnect.prepareStatement(sql);
            findPrepare.setString(1, employeeId);
            findPrepare.setInt(2, workMonth);
            findPrepare.setInt(3, workYear);
            findResult = findPrepare.executeQuery();
            
            if (findResult.next()) {
                return findResult.getString("salary_code");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (findResult != null) findResult.close();
                if (findPrepare != null) findPrepare.close();
                if (findConnect != null) findConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    // METHOD XÓA TRONG DATABASE
    private void deleteSalaryRecordFromDB(String salaryCode) {
        String sql = "DELETE FROM salary_records WHERE salary_code = ?";
        
        Connection deleteConnect = null;
        PreparedStatement deletePrepare = null;
        
        try {
            deleteConnect = database.connectDB();
            deletePrepare = deleteConnect.prepareStatement(sql);
            deletePrepare.setString(1, salaryCode);
            
            int rowsAffected = deletePrepare.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("✅ Xóa bảng lương thành công!", Alert.AlertType.INFORMATION);
                
                // Clear form và refresh data
                clearSalaryForm();
                handleFilter(); // Refresh theo filter hiện tại
                
                // Clear selection trong table
                salaryTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Không tìm thấy bảng lương để xóa!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi xóa bảng lương: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (deletePrepare != null) deletePrepare.close();
                if (deleteConnect != null) deleteConnect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // THÊM METHOD CLEAR FORM
    private void clearSalaryForm() {
        tfEmployeeId.clear();
        
        // Reset về tháng/năm hiện tại
        cbMonth.setValue(String.format("%02d", LocalDate.now().getMonthValue()));
        cbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        updateSelectedMonthLabel();
        
        // Clear table selection
        salaryTable.getSelectionModel().clearSelection();
    }
}