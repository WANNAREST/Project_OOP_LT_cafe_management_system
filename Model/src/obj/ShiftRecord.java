package obj;

public class ShiftRecord {
	private int id; 
    private String workDate;
    private String shift;
    private String employeeId;
    private String employeeName;
    private String attendanceStatus;

    public ShiftRecord(String workDate, String shift, String employeeId, String employeeName, String attendanceStatus) {
        this.workDate = workDate;
        this.shift = shift;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attendanceStatus = attendanceStatus;
    }
    public ShiftRecord(int id, String workDate, String shift, String employeeId, String employeeName, String attendanceStatus) {
        this.id = id;
        this.workDate = workDate;
        this.shift = shift;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attendanceStatus = attendanceStatus;
    }
    public int getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    // Getters and Setters
    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
	
}