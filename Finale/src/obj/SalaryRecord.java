package obj;

import javafx.beans.property.*;

public class SalaryRecord {
    private StringProperty employeeId;
    private StringProperty salaryCode;
    private DoubleProperty basicSalary;
    private IntegerProperty present;
    private IntegerProperty late;
    private IntegerProperty absent;
    private DoubleProperty bonusPenalty;
    private DoubleProperty totalSalary;
    private StringProperty note;

    public SalaryRecord() {
        this.employeeId = new SimpleStringProperty();
        this.salaryCode = new SimpleStringProperty();
        this.basicSalary = new SimpleDoubleProperty();
        this.present = new SimpleIntegerProperty();
        this.late = new SimpleIntegerProperty();
        this.absent = new SimpleIntegerProperty();
        this.bonusPenalty = new SimpleDoubleProperty();
        this.totalSalary = new SimpleDoubleProperty();
        this.note = new SimpleStringProperty();
    }

    // ✅ Constructor cũ (giữ nguyên để tương thích)
    public SalaryRecord(String employeeId, String salaryCode, double basicSalary,
                       int present, int late, int absent, double bonusPenalty,
                       double totalSalary, String note) {
        this();
        setEmployeeId(employeeId);
        setSalaryCode(salaryCode);
        setBasicSalary(basicSalary);
        setPresent(present);
        setLate(late);
        setAbsent(absent);
        setBonusPenalty(bonusPenalty);
        setTotalSalary(totalSalary);
        setNote(note);
    }

    // ✅ THÊM - Constructor mới cho database schema với late_days
    public SalaryRecord(String salaryId, String employeeId, String employeeName, 
                       int month, int year, int workingDays, int lateDays, int absentDays,
                       double baseSalary, double bonus, double total, String note) {
        this();
        setSalaryCode(salaryId);  // salary_id -> salaryCode
        setEmployeeId(employeeId);
        setBasicSalary(baseSalary);
        setPresent(workingDays);  // working_days -> present
        setLate(lateDays);        // late_days -> late
        setAbsent(absentDays);    // absent_days -> absent
        setBonusPenalty(bonus);   // bonus -> bonusPenalty
        setTotalSalary(total);
        setNote(note != null ? note : (employeeName + " - " + month + "/" + year));
    }

    // Getters
    public String getEmployeeId() { return employeeId.get(); }
    public String getSalaryCode() { return salaryCode.get(); }
    public double getBasicSalary() { return basicSalary.get(); }
    public int getPresent() { return present.get(); }
    public int getLate() { return late.get(); }
    public int getAbsent() { return absent.get(); }
    public double getBonusPenalty() { return bonusPenalty.get(); }
    public double getTotalSalary() { return totalSalary.get(); }
    public String getNote() { return note.get(); }

    // Setters
    public void setEmployeeId(String value) { this.employeeId.set(value); }
    public void setSalaryCode(String value) { this.salaryCode.set(value); }
    public void setBasicSalary(double value) { this.basicSalary.set(value); }
    public void setPresent(int value) { this.present.set(value); }
    public void setLate(int value) { this.late.set(value); }
    public void setAbsent(int value) { this.absent.set(value); }
    public void setBonusPenalty(double value) { this.bonusPenalty.set(value); }
    public void setTotalSalary(double value) { this.totalSalary.set(value); }
    public void setNote(String value) { this.note.set(value); }

    // Properties
    public StringProperty employeeIdProperty() { return employeeId; }
    public StringProperty salaryCodeProperty() { return salaryCode; }
    public DoubleProperty basicSalaryProperty() { return basicSalary; }
    public IntegerProperty presentProperty() { return present; }
    public IntegerProperty lateProperty() { return late; }
    public IntegerProperty absentProperty() { return absent; }
    public DoubleProperty bonusPenaltyProperty() { return bonusPenalty; }
    public DoubleProperty totalSalaryProperty() { return totalSalary; }
    public StringProperty noteProperty() { return note; }
}