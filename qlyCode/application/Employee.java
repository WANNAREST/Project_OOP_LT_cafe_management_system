package application;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Employee extends User {
    private String employeeID;
    private String fullName;
    private LocalDate DOB;
    private String address;

    private Map<LocalDate, List<String>> workSchedule;
    private Map<LocalDate, Map<String, Integer>> attend;

    public Employee(String employeeID, String fullName, LocalDate DOB, String address, String username, String password) {
        super(username, password);
        this.employeeID = employeeID;
        this.fullName = fullName;
        this.DOB = DOB;
        this.address = address;
        this.username = username;
        this.password = password;
        this.workSchedule = new HashMap<>();
        this.attend = new HashMap<>();
    }
    public String getEmployeeID() {
        return employeeID;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String name){
        this.fullName = name;
    }
    public LocalDate getDOB() {
        return DOB;
    }
    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public void printAccountInfo(){
        System.out.println("Employee ID: " + employeeID);
        System.out.println("Full Name: " + fullName);
        System.out.println("DOB: " + DOB);
        System.out.println("Address: " + address);
    }
    public int followAttending(LocalDate date, String ca){
        return attend.getOrDefault(date, new HashMap<>()).getOrDefault(ca, 0);
    }
    public void updateAttendance(LocalDate date, String shift, int status) {
        if (attend == null) {
            attend = new HashMap<>();
        }

        Map<String, Integer> shifts = attend.getOrDefault(date, new HashMap<>());
        shifts.put(shift, status);
        attend.put(date, shifts);
    }
    public int sumOfWorking(){
        int sum = 0;
        for(Map<String, Integer> caMap : attend.values()){
            for(int status : caMap.values()){
                if(status == 1){
                    sum++;
                }
            }
        }
        return sum;
    }
    public int sumOfAbsent() {
        int sum = 0;
        for (Map<String, Integer> caMap : attend.values()) {
            for (int trangThai : caMap.values()) {
                if (trangThai == 0) sum++;
            }
        }
        return sum;
    }
    public void showFullWorking(LocalDate date){
        Map<String, Integer> caList = attend.get(date);
        if(caList == null){
            System.out.println("No working attendance found for date: " + date);
            return;
        }
        else{
            System.out.println("Attendance found for date: " + date);
            for(Map.Entry<String, Integer> caEntry : caList.entrySet()){
                System.out.println(caEntry.getKey() + ": " + caEntry.getValue());
            }
        }
    }

}
