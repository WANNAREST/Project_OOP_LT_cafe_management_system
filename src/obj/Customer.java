package obj;

public class Customer extends User {
    private int point;              // điểm thưởng tích lũy

    // Constructor tương ứng với DB schema
    public Customer(int customerId, String firstName, String lastName,
                    String phone, String password, String address,
                    int point, String email) {
        super(customerId, firstName, lastName, phone, password, email, address);
        this.point = point;
    }

    public Customer(int customerId, String firstName, String lastName,
                    String phone, String password, String address, String email) {
        super(customerId, firstName, lastName, phone, password, email, address);
        this.point = 0; // Mặc định bắt đầu với 0 point
    }

    // Getter và Setter tương ứng với DB fields
    public int getpoint() {
        return point;
    }
    public void setpoint(int point) {
        this.point = point;
    }

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
        System.out.println("Added " + points + " points. Total points: " + this.point);
    }

    public boolean canUsePoints(int points) {
        return this.point >= points;
    }

    // Phương thức thưởng point khi đặt hàng thành công
    public void rewardOrderPoints() {
        addpoints(20); // Thưởng 20 point mỗi khi đặt hàng thành công
    }

    public boolean usePoints(int points) {
        if(canUsePoints(points)){
            this.point -= points;
            System.out.println("Remaining points: " + this.point);
            return true;
        }
        return false;
    }

    // Phương thức tính discount từ point (1 point = 200 VND)
    public int calculateDiscountFromPoints(int pointsToUse) {
        if (canUsePoints(pointsToUse)) {
            return pointsToUse * 200;
        }
        return 0;
    }
}
