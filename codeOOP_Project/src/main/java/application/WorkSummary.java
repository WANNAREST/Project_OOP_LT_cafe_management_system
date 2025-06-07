package application;

public class WorkSummary {
    private String employeeId;
    private int present;
    private int late;
    private int absent;
    private int salaryEarned;

    public WorkSummary(String employeeId) {
        this.employeeId = employeeId;
        this.present = 0;
        this.late = 0;
        this.absent = 0;
        this.salaryEarned = 0;
    }

    public void incrementPresent() {
        present++;
        updateSalary();
    }

    public void incrementLate() {
        late++;
        updateSalary();
    }

    public void incrementAbsent() {
        absent++;
    }

    private void updateSalary() {
        int daily = 200_000;
        int latePenalty = 50_000;
        this.salaryEarned = present * daily + late * (daily - latePenalty);
    }

    public String getEmployeeId() { return employeeId; }
    public int getPresent() { return present; }
    public int getLate() { return late; }
    public int getAbsent() { return absent; }
    public int getSalaryEarned() { return salaryEarned; }
}