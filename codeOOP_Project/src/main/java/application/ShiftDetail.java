package application;

public class ShiftDetail {
    private WorkSchedule shift;
    private Employee employee;
    private String status; // "scheduled", "completed", etc.

    public ShiftDetail(WorkSchedule shift, Employee employee, String status) {
        this.shift = shift;
        this.employee = employee;
        this.status = (status != null) ? status : "scheduled";
    }

    // Getter - Setter
    public WorkSchedule getShift() { return shift; }
    public Employee getEmployee() { return employee; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedStatus() {
        return switch (status.toLowerCase()) {
            case "scheduled" -> "Đã lên lịch";
            case "completed" -> "Đã hoàn thành";
            case "absent" -> "Vắng mặt";
            case "late" -> "Đi muộn";
            default -> status;
        };
    }
}
