package obj;

import javafx.beans.property.*;

import java.sql.Date;

public class Employee {
    private IntegerProperty employeeId;
    private StringProperty fullName;
    private StringProperty phone;
    private StringProperty email;
    private StringProperty address;
    private StringProperty role;
    private StringProperty gender;
    private StringProperty citizenId;
    private ObjectProperty<Date> dob;

    public Employee() {
        this.employeeId = new SimpleIntegerProperty();
        this.fullName = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
        this.gender = new SimpleStringProperty();
        this.citizenId = new SimpleStringProperty();
        this.dob = new SimpleObjectProperty<>();
    }

    public Employee(int employeeId, String fullName, String phone, String email,
                   String address, String role, String gender, String citizenId, Date dob) {
        this();
        setEmployeeId(employeeId);
        setFullName(fullName);
        setPhone(phone);
        setEmail(email);
        setAddress(address);
        setRole(role);
        setGender(gender);
        setCitizenId(citizenId);
        setDob(dob);
    }

    // Getters
    public int getEmployeeId() { return employeeId.get(); }
    public String getFullName() { return fullName.get(); }
    public String getPhone() { return phone.get(); }
    public String getEmail() { return email.get(); }
    public String getAddress() { return address.get(); }
    public String getRole() { return role.get(); }
    public String getGender() { return gender.get(); }
    public String getCitizenId() { return citizenId.get(); }
    public Date getDob() { return dob.get(); }

    // Setters
    public void setEmployeeId(int value) { this.employeeId.set(value); }
    public void setFullName(String value) { this.fullName.set(value); }
    public void setPhone(String value) { this.phone.set(value); }
    public void setEmail(String value) { this.email.set(value); }
    public void setAddress(String value) { this.address.set(value); }
    public void setRole(String value) { this.role.set(value); }
    public void setGender(String value) { this.gender.set(value); }
    public void setCitizenId(String value) { this.citizenId.set(value); }
    public void setDob(Date value) { this.dob.set(value); }

    // Properties
    public IntegerProperty employeeIdProperty() { return employeeId; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty addressProperty() { return address; }
    public StringProperty roleProperty() { return role; }
    public StringProperty genderProperty() { return gender; }
    public StringProperty citizenIdProperty() { return citizenId; }
    public ObjectProperty<Date> dobProperty() { return dob; }

    // Utility methods
    public String getRoleDisplay() {
        switch (role.get()) {
            case "manager": return "Quản lý";
            case "employee": return "Nhân viên";
            default: return role.get();
        }
    }

    public String getGenderDisplay() {
        switch (gender.get()) {
            case "male": return "Nam";
            case "female": return "Nữ";
            default: return gender.get();
        }
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', role='%s', phone='%s'}",
                employeeId.get(), fullName.get(), role.get(), phone.get());
    }
}
