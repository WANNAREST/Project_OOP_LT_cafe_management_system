package application;
import java.util.*;
public class SalaryRecord {
    private String salaryCode;
    private Employee employee;
    private int workingShifts;
    private double basicSalaryPerShift;
    private double bonusPenalty;
    private String note;
    private double totalSalary;
    private boolean isViewable;

    public SalaryRecord(String salaryCode, int workingShifts, double basicSalaryPerShift, String note) {
        this.salaryCode = salaryCode;
        this.workingShifts = workingShifts;
        this.basicSalaryPerShift = basicSalaryPerShift;
        this.note = note;
        updateSalary();
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
    public void setSalaryCode(String salaryCode) {
        this.salaryCode = salaryCode;
    }
    public int getWorkingShifts() {
        return workingShifts;
    }
    public void setWorkingShifts(int workingShifts) {
        this.workingShifts = workingShifts;
    }
    public double getBonusPenalty() {
        return bonusPenalty;
    }

    public void updateSalary() {
        this.workingShifts = employee.sumOfWorking();
        this.totalSalary = workingShifts * basicSalaryPerShift + bonusPenalty;
    }

    public void setBonusPenalty(double bonusPenalty, String note) {
        this.bonusPenalty = bonusPenalty;
        this.note = note;
        updateSalary();
    }

    public void printSalary() {
        System.out.println("Salary ID: " + salaryCode);
        System.out.println("Employee ID: " + employee.getEmployeeID());
        System.out.println("Total Shifts: " + workingShifts);
        System.out.println("Total Salary: " + totalSalary);
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public String getEmployeeID() {
        return employee.getEmployeeID();
    }
}
