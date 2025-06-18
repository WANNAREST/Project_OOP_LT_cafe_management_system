package Model;

import java.time.LocalDate;

public class Employee {
    private int id;
    private String name;
    private LocalDate dob;
    private String phone;
    private String email;
    private String imagePath;
    private WorkSchedule workSchedule;

    public Employee(int id, String name, LocalDate dob, String phone, String email, String imagePath) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.imagePath = imagePath;
        this.workSchedule = new WorkSchedule(String.valueOf(id));
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getImagePath() { return imagePath; }
    public WorkSchedule getWorkSchedule() { return workSchedule; }

    public void loadSchedule(int month, int year) throws Exception {
        workSchedule.loadFromDatabase(id, month, year);
    }
}