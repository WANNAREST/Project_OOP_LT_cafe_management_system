package obj;

import javafx.beans.property.*;

public class SalaryRecord {
    private StringProperty salaryCode;
    private StringProperty employeeId;
    private IntegerProperty present;
    private IntegerProperty late;
    private IntegerProperty absent;
    private DoubleProperty basicSalaryPerShift;
    private DoubleProperty bonusPenalty;
    private StringProperty note;
    private DoubleProperty totalSalary;
    private BooleanProperty isViewable;

    public SalaryRecord(String salaryCode, WorkSummary summary, double basicSalaryPerShift, double bonusPenalty, String note) {
        this.salaryCode = new SimpleStringProperty(salaryCode);
        this.employeeId = new SimpleStringProperty(summary.getEmployeeId());
        this.present = new SimpleIntegerProperty(summary.getPresent());
        this.late = new SimpleIntegerProperty(summary.getLate());
        this.absent = new SimpleIntegerProperty(summary.getAbsent());
        this.basicSalaryPerShift = new SimpleDoubleProperty(basicSalaryPerShift);
        this.bonusPenalty = new SimpleDoubleProperty(bonusPenalty);
        this.note = new SimpleStringProperty(note);
        this.totalSalary = new SimpleDoubleProperty();
        this.isViewable = new SimpleBooleanProperty(false);
        updateSalary();
    }
    public static double calculateTotalSalary(int present, int late, int absent, 
            double basicSalary, double bonusPenalty) {
		    double latePenalty = 5000;
		    double absentPenalty = 10000;
		     return present * basicSalary + 
				           late * (basicSalary - latePenalty) - 
				           absent * absentPenalty + 
				           bonusPenalty;
    }

		private void updateSalary() {
		      double total = calculateTotalSalary(
		            present.get(), 
					late.get(), 
					absent.get(), 
					basicSalaryPerShift.get(), 
					bonusPenalty.get()
					);
					totalSalary.set(total);
		}
    
    
    // Property getters
    public StringProperty salaryCodeProperty() { return salaryCode; }
    public StringProperty employeeIdProperty() { return employeeId; }
    public IntegerProperty presentProperty() { return present; }
    public IntegerProperty lateProperty() { return late; }
    public IntegerProperty absentProperty() { return absent; }
    public DoubleProperty basicSalaryPerShiftProperty() { return basicSalaryPerShift; }
    public DoubleProperty bonusPenaltyProperty() { return bonusPenalty; }
    public StringProperty noteProperty() { return note; }
    public DoubleProperty totalSalaryProperty() { return totalSalary; }
    public BooleanProperty isViewableProperty() { return isViewable; }

    // Regular getters
    public String getSalaryCode() { return salaryCode.get(); }
    public String getEmployeeId() { return employeeId.get(); }
    public int getPresent() { return present.get(); }
    public int getLate() { return late.get(); }
    public int getAbsent() { return absent.get(); }
    public double getBasicSalaryPerShift() { return basicSalaryPerShift.get(); }
    public double getBonusPenalty() { return bonusPenalty.get(); }
    public String getNote() { return note.get(); }
    public double getTotalSalary() { return totalSalary.get(); }
    public boolean isViewable() { return isViewable.get(); }

    // Setters
    public void setSalaryCode(String value) { salaryCode.set(value); }
    public void setEmployeeId(String value) { employeeId.set(value); }
    public void setPresent(int value) { present.set(value); updateSalary(); }
    public void setLate(int value) { late.set(value); updateSalary(); }
    public void setAbsent(int value) { absent.set(value); updateSalary(); }
    public void setBasicSalaryPerShift(double value) { basicSalaryPerShift.set(value); updateSalary(); }
    public void setBonusPenalty(double value) { bonusPenalty.set(value); updateSalary(); }
    public void setNote(String value) { note.set(value); }
    public void setViewable(boolean value) { isViewable.set(value); }

    public void printSalary() {
        System.out.println("Salary ID: " + getSalaryCode());
        System.out.println("Employee ID: " + getEmployeeId());
        System.out.println("Present: " + getPresent() + ", Late: " + getLate() + ", Absent: " + getAbsent());
        System.out.println("Total Salary: " + getTotalSalary());
    }
	public void setTotalSalary(double value) {
		totalSalary.set(value);
		
	}
}