package application.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import application.Employee;
import java.time.LocalDate;
public class EmployeeTest {
    public static void main(String[] args) {
        // Tạo một nhân viên mới
        Employee emp = new Employee(
                "NV001",
                "Nguyen Van A",
                LocalDate.of(1990, 5, 20),
                "123 Le Loi, HCM",
                "nv001",
                "password123"
        );

        // In thông tin nhân viên
        emp.printAccountInfo();

        // Giả sử quản lý cập nhật điểm danh cho nhân viên:
        // 2025-05-20, ca sáng: đi làm (1)
        //emp.attend.putIfAbsent(LocalDate.of(2025, 5, 20), new java.util.HashMap<>());
        //emp.attend.get(LocalDate.of(2025, 5, 20)).put("Sáng", 1);

        // 2025-05-20, ca chiều: nghỉ (0)
        //emp.attend.get(LocalDate.of(2025, 5, 20)).put("Chiều", 0);

        // Xem điểm danh ca sáng ngày 2025-05-20
        int status = emp.followAttending(LocalDate.of(2025, 5, 20), "Sáng");
        System.out.println("Trạng thái điểm danh ca Sáng ngày 2025-05-20: " + status);

        // Xem tổng số ca đi làm và nghỉ
        System.out.println("Tổng số ca đi làm: " + emp.sumOfWorking());
        System.out.println("Tổng số ca nghỉ: " + emp.sumOfAbsent());

        // Hiển thị chi tiết điểm danh ngày 2025-05-20
        emp.showFullWorking(LocalDate.of(2025, 5, 20));
    }
}
