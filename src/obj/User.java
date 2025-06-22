package obj;

abstract class User {
    protected int id;
    protected String fullname;
    protected String phone;
    protected String password;
    protected String email;
    protected String address;

    // Constructor
    public User(int id, String fullname, String phone,
                String password, String email, String address) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.address = address;
    }


    // Getter và Setter methods - Tính đóng gói (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullname; }
    public void setFullName(String fullname) { this.fullname = fullname; }

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