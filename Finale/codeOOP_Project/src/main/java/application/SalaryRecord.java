package application;

public class SalaryRecord {
    private String salaryCode;
    private String employeeId;
    private int present;
    private int late;
    private int absent;
    private double basicSalaryPerShift;
    private double bonusPenalty; // cộng thêm hoặc trừ
    private String note;
    private double totalSalary;
    private boolean isViewable;

    public SalaryRecord(String salaryCode, WorkSummary summary, double basicSalaryPerShift, double bonusPenalty, String note) {
        this.salaryCode = salaryCode;
        this.employeeId = summary.getEmployeeId();
        this.present = summary.getPresent();
        this.late = summary.getLate();
        this.absent = summary.getAbsent();
        this.basicSalaryPerShift = basicSalaryPerShift;
        this.bonusPenalty = bonusPenalty;
        this.note = note;
        updateSalary();
    }

    private void updateSalary() {
        double latePenalty = 50_000;
        totalSalary = present * basicSalaryPerShift + late * (basicSalaryPerShift - latePenalty) + bonusPenalty;
    }

    public boolean isViewable() {
        return isViewable;
    }

    public void setViewable(boolean viewable) {
        isViewable = viewable;
    }

    public String getSalaryCode() {
        return salaryCode;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public int getPresent() {
        return present;
    }

    public int getLate() {
        return late;
    }

    public int getAbsent() {
        return absent;
    }

    public double getBonusPenalty() {
        return bonusPenalty;
    }

    public String getNote() {
        return note;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void printSalary() {
        System.out.println("Salary ID: " + salaryCode);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Present: " + present + ", Late: " + late + ", Absent: " + absent);
        System.out.println("Total Salary: " + totalSalary);
    }
}