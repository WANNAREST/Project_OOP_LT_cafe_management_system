package obj;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShiftRecord {
    private final StringProperty workDate;
    private final StringProperty shift;
    private final StringProperty employeeId;
    private final StringProperty employeeName;
    private final StringProperty attendanceStatus;

    public ShiftRecord(String workDate, String shift, String employeeId, String employeeName, String attendanceStatus) {
        this.workDate = new SimpleStringProperty(workDate);
        this.shift = new SimpleStringProperty(shift);
        this.employeeId = new SimpleStringProperty(employeeId);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.attendanceStatus = new SimpleStringProperty(attendanceStatus);
    }

    //  THÊM - Property methods cho editable table
    public StringProperty workDateProperty() { return workDate; }
    public StringProperty shiftProperty() { return shift; }
    public StringProperty employeeIdProperty() { return employeeId; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty attendanceStatusProperty() { return attendanceStatus; }

    // Getters
    public String getWorkDate() { return workDate.get(); }
    public String getShift() { return shift.get(); }
    public String getEmployeeId() { return employeeId.get(); }
    public String getEmployeeName() { return employeeName.get(); }
    public String getAttendanceStatus() { return attendanceStatus.get(); }

    //  THÊM - Setters cho editable functionality
    public void setWorkDate(String workDate) { this.workDate.set(workDate); }
    public void setShift(String shift) { this.shift.set(shift); }
    public void setEmployeeId(String employeeId) { this.employeeId.set(employeeId); }
    public void setEmployeeName(String employeeName) { this.employeeName.set(employeeName); }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus.set(attendanceStatus); }
}