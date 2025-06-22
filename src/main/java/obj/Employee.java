package obj;

import java.time.LocalDate;
import java.sql.Date;

public class Employee {
    private int id;
    private String name;
    private LocalDate dob;
    private String phone;
    private String email;
    private WorkSchedule workSchedule;
    
    // Additional fields for AdminPart compatibility
    private String address;
    private String role;
    private String gender;
    private String citizenId;
    private Date dobDate; // For SQL Date compatibility

    public Employee(int id, String name, LocalDate dob, String phone, String email) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.workSchedule = new WorkSchedule(String.valueOf(id));
    }
    
    public Employee(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.workSchedule = new WorkSchedule(String.valueOf(id));
    }
    
    // Constructor for AdminPart compatibility
    public Employee(int id, String fullName, String phone, String email, String address, 
                   String role, String gender, String citizenId, Date dob) {
        this.id = id;
        this.name = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.role = role;
        this.gender = gender;
        this.citizenId = citizenId;
        this.dobDate = dob;
        this.dob = dob != null ? dob.toLocalDate() : null;
        this.workSchedule = new WorkSchedule(String.valueOf(id));
    }

    // Getters
    public String getDisplayName() {
        return this.name + " (" + this.phone + ")";
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public WorkSchedule getWorkSchedule() { return workSchedule; }
    
    // Additional getters for AdminPart compatibility
    public int getEmployeeId() { return id; }
    public String getFullName() { return name; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public String getGender() { return gender; }
    public String getCitizenId() { return citizenId; }
    public Date getDobDate() { return dobDate; }

    public void loadSchedule(int month, int year) throws Exception {
        workSchedule.loadFromDatabase(id, month, year);
    }
}