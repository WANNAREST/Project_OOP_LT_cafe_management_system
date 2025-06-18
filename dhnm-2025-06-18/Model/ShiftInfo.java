package Model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Lớp lưu trữ thông tin chi tiết về một ca làm việc của nhân viên
 */
public class ShiftInfo {
    private String time;       // Loại ca: "morning", "afternoon", "evening"
    private String status;     // Trạng thái: "scheduled", "completed", "absent", "late"
    private LocalDate date;    // Ngày làm việc (thêm nếu cần cho xử lý logic)

    // Constructor
    public ShiftInfo(String time, String status) {
        this.time = time;
        this.status = status != null ? status : "scheduled"; // Mặc định nếu null
    }

    // Getter và Setter
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Phương thức chuyển đổi tên ca sang định dạng dễ đọc
    public String getFormattedShiftName() {
        return switch (time.toLowerCase()) {
            case "morning" -> "Ca sáng (7h-12h)";
            case "afternoon" -> "Ca chiều (13h-18h)";
            case "evening" -> "Ca tối (19h-22h)";
            default -> time;
        };
    }

    // Phương thức chuyển đổi trạng thái sang tiếng Việt
    public String getFormattedStatus() {
        return switch (status.toLowerCase()) {
            case "scheduled" -> "Đã lên lịch";
            case "completed" -> "Đã hoàn thành";
            case "absent" -> "Vắng mặt";
            case "late" -> "Đi muộn";
            default -> status;
        };
    }

    // Override equals và hashCode để so sánh đối tượng
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftInfo shiftInfo = (ShiftInfo) o;
        return Objects.equals(time, shiftInfo.time) && 
               Objects.equals(status, shiftInfo.status) &&
               Objects.equals(date, shiftInfo.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, status, date);
    }

    // Phương thức toString để debug
    @Override
    public String toString() {
        return "ShiftInfo{" +
                "time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}