package application;
import java.time.LocalDate;

public class AttendanceManager {
    private UserManager userManager;

    public AttendanceManager(UserManager userManager) {
        this.userManager = userManager;
    }
    //tham khảo ChatGPT, cần hỗ trợ thêm
    public void markAttendance(String employeeUsername, LocalDate date, String shift, int status) {
        User user = userManager.findUserByUsername(employeeUsername);
        if(user == null) {
            System.out.println("Không tìm thấy user với username: " + employeeUsername);
            return;
        }
        if(!(user instanceof Employee)) {
            System.out.println("User này không phải là nhân viên.");
            return;
        }

        Employee emp = (Employee) user;
        emp.updateAttendance(date, shift, status);  // Phương thức này có trong lớp Employee
        System.out.println("Đã cập nhật điểm danh cho nhân viên " + emp.getEmployeeID());
    }
}
