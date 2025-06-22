package obj;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private int point;              // điểm thưởng tích lũy
    private Cart cart;              // Customer's shopping cart
    private List<Order> orderHistory; // Customer's order history

    // Constructor tương ứng với DB schema
    public Customer(int customerId, String fullname,
                    String phone, String password, String address,
                    int point, String email) {
        super(customerId, fullname, phone, password, email, address);
        this.point = point;
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    public Customer(int customerId, String fullname,
                    String phone, String password, String address, String email) {
        super(customerId, fullname, phone, password, email, address);
        this.point = 0; // Mặc định bắt đầu với 0 point
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    // Additional constructor for legacy compatibility
    public Customer(String fullname, String phone, String address) {
        super(1, fullname, phone, "", "", address); // Default values for missing fields
        this.point = 0;
        this.cart = new Cart();
        this.orderHistory = new ArrayList<>();
    }

    // Getter và Setter tương ứng với DB fields
    public int getpoint() {
        return point;
    }
    public void setpoint(int point) {
        this.point = point;
    }

    // Legacy compatibility methods
    public int getBonusPoint() {
        return point;
    }

    public void setBonusPoint(int point) {
        this.point = point;
    }

    public String getCustomerId() {
        return String.valueOf(getId());
    }

    public void setCustomerId(String customerId) {
        try {
            setId(Integer.parseInt(customerId));
        } catch (NumberFormatException e) {
            System.err.println("Invalid customer ID format: " + customerId);
        }
    }

    public String getName() {
        return getFullName();
    }

    public String getPhoneNumber() {
        return getPhone();
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
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

    // Phương thức tính discount từ point (20 coins = 200 VND, so 1 coin = 10 VND)
    public int calculateDiscountFromPoints(int pointsToUse) {
        if (canUsePoints(pointsToUse)) {
            return pointsToUse * 10;
        }
        return 0;
    }
}
