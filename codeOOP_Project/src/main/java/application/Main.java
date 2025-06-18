package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. new Employee
        Employee nvA = new Employee(
                "A001",
                LocalDate.of(2000, 5, 10),
                "123 Đường Lê Lợi, Quận 1",
                "nguyenvana",
                "123456",
                "Nguyễn Văn A",
                "0912345678",
                "anguyen@gmail.com"
        );
        //test với upcasting and downcasting
        User userA = nvA;
        System.out.println("\n===== THÔNG TIN TÀI KHOẢN  =====");
        userA.printAccountInfo();
        if (userA instanceof Employee emp) {
            System.out.println("SĐT: " + emp.getPhone());
            System.out.println("Email: " + emp.getEmail());
        }
        // 2. Tạo danh sách điểm danh cho A trong tháng 5
        List<ShiftDetail> attendanceList = new ArrayList<>();
        WorkSchedule shift1 = new WorkSchedule(1, LocalDate.of(2025, 5, 1), "morning");
        ShiftDetail detail1 = new ShiftDetail(shift1, nvA, "completed");
        attendanceList.add(detail1);
        WorkSchedule shift2 = new WorkSchedule(2, LocalDate.of(2025, 5, 2), "morning");
        ShiftDetail detail2 = new ShiftDetail(shift2, nvA, "late");
        attendanceList.add(detail2);
        // 3. Tổng hợp điểm danh thành WorkSummary
        WorkSummary summary = new WorkSummary(nvA.getEmployeeID());
        for (ShiftDetail record : attendanceList) {
            switch (record.getStatus()) {
                case "completed" -> summary.incrementPresent();
                case "late" -> summary.incrementLate();
                case "absent" -> summary.incrementAbsent();
            }
        }

        // 4. Tính lương
        double basicSalaryPerShift = 200_000;
        double bonusPenalty = 0;
        String note = "Lương tháng 5/2025";

        SalaryRecord salaryRecord = new SalaryRecord("SAL-A001-05-2025", summary, basicSalaryPerShift, bonusPenalty, note);

        // 5. In bảng lương
        System.out.println("===== BẢNG LƯƠNG NHÂN VIÊN =====");
        nvA.printAccountInfo();
        salaryRecord.printSalary();
    }
}
