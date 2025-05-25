package obj;

class Customer extends User {
    private int point;              // điểm thưởng tích lũy

    // Constructor tương ứng với DB schema
    public Customer(int customerId, String firstName, String lastName,
                    String phone, String password, String address,
                    int point, String email) {
        super(customerId, firstName, lastName, phone, password, email, address);
        this.point = point;
    }

    // Getter và Setter tương ứng với DB fields
    public int getpoint() { return point; }
    public void setpoint(int point) { this.point = point; }

    // Implement abstract methods - Đa hình (Polymorphism)
    @Override
    public boolean login(String phone, String password) {
        return this.phone.equals(phone) && this.password.equals(password);
    }

    @Override
    public void displayDashboard() {
        System.out.println("=== CUSTOMER DASHBOARD ===");
        System.out.println("Welcome back, " + getFullName());
        System.out.println("Bonus Points: " + point);
    }

    @Override
    public String getRole() { return "Customer"; }

    // Business logic methods
    public void addpoints(int points) {
        this.point += points;
    }

    public boolean canUsePoints(int points) {
        return this.point >= points;
    }
}
