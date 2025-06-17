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

        // 2. Tạo danh sách điểm danh cho A trong tháng 5
        List<WorkScheduleRecord> attendanceList = new ArrayList<>();
        attendanceList.add(new WorkScheduleRecord(nvA.getEmployeeID(), "2025-05-01", "Ca sáng", "08:00", "12:00", "Có mặt", ""));
        attendanceList.add(new WorkScheduleRecord(nvA.getEmployeeID(), "2025-05-02", "Ca sáng", "08:15", "12:00", "Đi trễ", "Trễ 15 phút"));
        attendanceList.add(new WorkScheduleRecord(nvA.getEmployeeID(), "2025-05-03", "Ca sáng", "-", "-", "Vắng", "Không lý do"));

        // 3. Tổng hợp điểm danh thành WorkSummary
        WorkSummary summary = new WorkSummary(nvA.getEmployeeID());
        for (WorkScheduleRecord record : attendanceList) {
            switch (record.getStatus()) {
                case "Có mặt" -> summary.incrementPresent();
                case "Đi trễ" -> summary.incrementLate();
                case "Vắng" -> summary.incrementAbsent();
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
