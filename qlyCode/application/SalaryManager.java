package application;
import java.util.*;
public class SalaryManager {
    private List<SalaryRecord> salaryRecords;
    public SalaryManager() {
        salaryRecords = new ArrayList<>();
    }
    public void addSalaryRecord(SalaryRecord salaryRecord) {
        for(SalaryRecord record : salaryRecords) {
            if(record.getSalaryCode().equals(record.getSalaryCode())) {
                System.out.println("Salary record already exists");
                return;
            }
        }
        salaryRecords.add(salaryRecord);
        System.out.println("Salary record added");
    }
    public SalaryRecord findSalaryRecordByCode(String salaryCode) {
        for(SalaryRecord record : salaryRecords) {
            if(record.getSalaryCode().equals(salaryCode)) {
                return record;
            }
        }
        return null;
    }
    public double getTotalSalaryPaid() {
        double total = 0;
        for (SalaryRecord record : salaryRecords) {
            total += record.getTotalSalary();
        }
        return total;
    }
    public void setSalaryViewable(String code, boolean viewable) {
        for(SalaryRecord rc : salaryRecords) {
            if(rc.getSalaryCode().equals(code)) {
                rc.setViewable(viewable);
                System.out.println("Salary record viewable");
                return;
            }
        }
        System.out.println("Salary record not viewable");
    }
    public SalaryRecord getSalaryByCodeForEmployee(String salaryCode, String employeeID) {
        for (SalaryRecord record : salaryRecords) {
            if (record.getSalaryCode().equals(salaryCode) &&
                    record.getEmployeeID().equals(employeeID) &&
                    record.isViewable()) {
                return record;
            }
        }
        return null;
    }
}
