package obj;

abstract class User {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String phone;
    protected String password;
    protected String email;
    protected String address;

    // Constructor
    public User(int id, String firstName, String lastName, String phone,
                String password, String email, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.address = address;
    }


    // Getter và Setter methods - Tính đóng gói (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Abstract methods - phải được implement bởi các lớp con
    public abstract boolean login(String phone, String password);
    public abstract void displayDashboard();
    public abstract String getRole();
}