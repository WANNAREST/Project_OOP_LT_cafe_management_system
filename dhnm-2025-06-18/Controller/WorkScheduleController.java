package Controller;

import Model.Employee;
import Model.ShiftInfo;
import Model.WorkShift;
import DAO.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class WorkScheduleController {
	@FXML
	private TextField employeeIdField;
	@FXML
	private TextField employeeNameField;
	@FXML
	private TextField dobField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField emailField;
	@FXML
	private ImageView employeeImageView;
	@FXML
	private MenuButton monthMenuButton;
	@FXML
	private MenuButton yearMenuButton;
	@FXML
	private TableView<WorkShift> scheduleTable;
	@FXML
	private TableColumn<WorkShift, String> weekOfDayColumn;
	@FXML
	private TableColumn<WorkShift, String> dayColumn;
	@FXML
	private TableColumn<WorkShift, String> shiftColumn;
	@FXML
	private TableColumn<WorkShift, String> statusColumn;
	@FXML
	private TextField currentSalaryField;
	@FXML
	private TextField bonusField;

	private Employee currentEmployee;
	private final EmployeeDAO employeeDAO = new EmployeeDAO();
	private int currentYear = Year.now().getValue();
	private Month currentMonth = Month.from(LocalDate.now());

	@FXML
	public void initialize() {
		setupTableColumns();
		initializeMonthMenu();
		initializeYearMenu();
		loadDefaultEmployee();
	}

	private void setupTableColumns() {
		weekOfDayColumn.setCellValueFactory(cellData -> cellData.getValue().dayProperty());
		dayColumn.setCellValueFactory(cellData -> cellData.getValue().morningProperty());
		shiftColumn.setCellValueFactory(cellData -> cellData.getValue().afternoonProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().eveningProperty());
	}

	private void initializeMonthMenu() {
		
		monthMenuButton.getItems().clear();
		
		for (Month month : Month.values()) {
			MenuItem item = new MenuItem("Tháng " + (month.getValue()));
			item.setOnAction(e -> {
				monthMenuButton.setText(item.getText());
				currentMonth = month;
				loadScheduleForMonth();
			});
			monthMenuButton.getItems().add(item);
		}
		monthMenuButton.setText("Tháng " + currentMonth.getValue());
	}

	private void initializeYearMenu() {
		
		yearMenuButton.getItems().clear();
		
		// Add current and next year
		for (int year = currentYear; year <= currentYear + 1; year++) {
			MenuItem item = new MenuItem(String.valueOf(year));
			item.setOnAction(e -> {
				yearMenuButton.setText(item.getText());
				currentYear = Integer.parseInt(item.getText());
				loadScheduleForMonth();
			});
			yearMenuButton.getItems().add(item);
		}
		yearMenuButton.setText(String.valueOf(currentYear));
	}

	private void loadDefaultEmployee() {
		try {
			setEmployee(employeeDAO.getEmployeeById(3)); // Default employee
		} catch (Exception e) {
			showAlert("Lỗi", "Không thể tải thông tin nhân viên: " + e.getMessage());
		}
	}

	public void setEmployee(Employee employee) {
		this.currentEmployee = employee;
		if (employee != null) {
			loadEmployeeInfo();
			loadScheduleForMonth();
			loadSalaryInfo();
		}
	}

	private void loadEmployeeInfo() {
		employeeIdField.setText(String.valueOf(currentEmployee.getId()));
		employeeNameField.setText(currentEmployee.getName());
		dobField.setText(currentEmployee.getDob().toString());
		phoneField.setText(currentEmployee.getPhone());
		emailField.setText(currentEmployee.getEmail());

		try {
			Image image = new Image(getClass().getResourceAsStream(currentEmployee.getImagePath()));
			employeeImageView.setImage(image);
		} catch (Exception e) {
			System.err.println("Không thể tải ảnh: " + e.getMessage());
		}
	}

	private void loadScheduleForMonth() {
		try {
			Map<LocalDate, List<ShiftInfo>> shifts = employeeDAO.getEmployeeSchedule(currentEmployee.getId(),
					currentMonth.getValue(), currentYear);

			ObservableList<WorkShift> scheduleData = FXCollections.observableArrayList();

			// Lặp qua các ngày có ca làm việc (đã sắp xếp)
			shifts.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
				LocalDate date = entry.getKey();
				String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("vi"));
				String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM"));

				// Mỗi ca làm việc thành 1 dòng riêng
				for (ShiftInfo shift : entry.getValue()) {
					scheduleData.add(new WorkShift(dayOfWeek, formattedDate, convertShiftName(shift.getTime()),
							convertStatus(shift.getStatus()) // Ánh xạ trạng thái
					));
				}
			});

			scheduleTable.setItems(scheduleData);
			loadSalaryInfo();
			
		} catch (Exception e) {
			showAlert("Lỗi", "Không thể tải lịch làm việc: " + e.getMessage());
		}
	}
	
	private void loadSalaryInfo() {
	    try {
	        Map<String, Double> salaryInfo = employeeDAO.getEmployeeSalary(
	            currentEmployee.getId(), 
	            currentMonth.getValue(), 
	            currentYear
	        );
	        
	        // Reset các trường về trống
	        currentSalaryField.setText("");
	        bonusField.setText("");
	        
	        if (salaryInfo != null) {
	            
	            Double totalSalary = salaryInfo.get("salary");
	            Double bonus = salaryInfo.get("bonus");
	            
	            if (totalSalary != null) {
	                currentSalaryField.setText(String.format("%,.0f VND", totalSalary));
	            }
	            
	            if (bonus != null ) { 
	                bonusField.setText(String.format("%,.0f VND", bonus));
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("Không thể tải thông tin lương: " + e.getMessage());
	    }
	}

	// Chuyển đổi trạng thái sang tiếng Việt
	private String convertStatus(String status) {
		return switch (status.toLowerCase()) {
		case "scheduled" -> "Đã lên lịch";
		case "completed" -> "Đã hoàn thành";
		case "absent" -> "Vắng mặt";
		case "late" -> "Đi muộn";
		default -> status;
		};
	}

	private String getVietnameseDayName(DayOfWeek day) {
		return day.getDisplayName(TextStyle.FULL, new Locale("vi"));
	}

	private String convertShiftName(String shift) {
		switch (shift.toLowerCase()) {
		case "morning":
			return "Ca sáng (7h-12h)";
		case "afternoon":
			return "Ca chiều (13h-18h)";
		case "evening":
			return "Ca tối (19h-22h)";
		default:
			return shift;
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}