package Cart_CoffeeShop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import Cart_CoffeeShop.CartItem;
import Customer.CustomerManager;
import DAO.EmployeeDAO;
import DATABASE.DatabaseConnection;
import Menu.Products;
import Model.Employee;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Order_CoffeeShop {
    private String customerName;
    private String phoneNumber;
    private String orderType;
    private String deliveryAddress;
    private LocalDateTime orderTime;
    private List<CartItem> items;
    private String status;
    private int orderId;
    
    private final SimpleIntegerProperty orderIdProperty = new SimpleIntegerProperty();
    private final SimpleStringProperty customerNameProperty = new SimpleStringProperty();
    private final SimpleStringProperty phoneProperty = new SimpleStringProperty();
    private final SimpleObjectProperty<LocalDateTime> orderTimeProperty = new SimpleObjectProperty<>();
    
	private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Order_CoffeeShop(String customerName, String phoneNumber, String orderType, 
            String deliveryAddress, List<CartItem> items) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.orderType = orderType;
        this.deliveryAddress = deliveryAddress;
        this.orderTime = LocalDateTime.now();
        this.items = items;
        this.status = "pending";
        CustomerManager.addOrderToCustomer(this);
        
        this.orderIdProperty.set(orderId);
        this.customerNameProperty.set(customerName);
        this.phoneProperty.set(phoneNumber);
        this.orderTimeProperty.set(orderTime);
    }
    
    public SimpleIntegerProperty orderIdProperty() { return orderIdProperty; }
    public SimpleStringProperty customerNameProperty() { return customerNameProperty; }
    public SimpleStringProperty phoneProperty() { return phoneProperty; }
    public SimpleObjectProperty<LocalDateTime> orderTimeProperty() { return orderTimeProperty; }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
        this.orderIdProperty.set(orderId);
    }

    // Getter methods
    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getOrderId() {
        return orderId;
    }

    public double getTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream().mapToDouble(item -> item.getTotalPrice()).sum();
    }

    
//                        ============ QUẢN LÝ ĐƠN HÀNG OFFLINE ============
    
    private void makeAnOrder(Connection conn) throws Exception {
        if (items == null || items.isEmpty()) {
            throw new Exception("Cannot create order with empty items");
        }

        // Kiểm tra stock trước khi đặt hàng
        checkStockAvailability();

        double total = getTotalAmount();
        String dbStatus = "offline";
        if ("online".equalsIgnoreCase(orderType)) {
            dbStatus = "take-away";
        }

        String sql = "INSERT INTO Orders (customer_id, employee_id, date, status, " +
                     "payment_method, payment_status, total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        	
        	// Xử lý customer_id (có thể NULL)
        	int customerId = getCustomerIdByPhone(conn, this.phoneNumber);
            if (customerId == -1) {
                pstmt.setNull(1, Types.INTEGER); // customer_id = NULL
            } else {
                pstmt.setInt(1, customerId);
            }

            Employee assignedEmployee = employeeDAO.getEmployeeById(3); // Nhân viên mặc định (ID=3)
            //pstmt.setInt(1, customerId);
            pstmt.setInt(2, assignedEmployee.getId()); // Sử dụng getId()
            pstmt.setTimestamp(3, Timestamp.valueOf(orderTime));
            pstmt.setString(4, dbStatus);
            pstmt.setString(5, "cash");
            pstmt.setString(6, "pending");
            pstmt.setDouble(7, total);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new Exception("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.orderId = generatedKeys.getInt(1);
                } else {
                    throw new Exception("Creating order failed, no ID obtained.");
                }
            }
        }
    }
    
    private void makeAnOrderline(Connection conn) throws Exception {
        if (orderId <= 0) {
            throw new Exception("Invalid order ID. Call makeAnOrder() first.");
        }

        String sql = "INSERT INTO Orderlines (order_id, product_id, quantity, price) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CartItem item : items) {
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, item.getProduct().getId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getProduct().getPrice());
                
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        
        // Cập nhật stock sau khi đặt hàng thành công
        updateProductStock(conn);
    }
    
    private int getCustomerIdByPhone(Connection conn, String phone) throws SQLException, Exception {
        // Nếu số điện thoại rỗng -> không cần kiểm tra
        if (phone == null || phone.trim().isEmpty()) {
            return -1;
        }

        String sql = "SELECT u.user_id, u.role FROM Users u WHERE u.phone = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                if (!"customer".equals(role)) {
                    throw new Exception("Số điện thoại này thuộc tài khoản " + role + ". Chỉ khách hàng (customer) mới có thể đặt hàng.");
                }
                return rs.getInt("user_id"); // Trả về customer_id nếu tìm thấy
            }
            return -1; // Số điện thoại không tồn tại -> không tích điểm
        }
    }
    
    private void checkStockAvailability() throws Exception {
        for (CartItem item : items) {
            Products product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new Exception("Not enough stock for product: " + product.getName() + 
                                  ". Available: " + product.getQuantity());
            }
        }
    }
    
    private void updateProductStock(Connection conn) throws SQLException {
        String sql = "UPDATE Products SET stock = stock - ? WHERE product_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CartItem item : items) {
                pstmt.setInt(1, item.getQuantity());
                pstmt.setInt(2, item.getProduct().getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    public void placeCompleteOrder() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            makeAnOrder(conn);
            makeAnOrderline(conn);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
 //                        ============ QUẢN LÝ ĐƠN HÀNG ONLINE ============
    
    public static List<Order_CoffeeShop> getOnlineOrdersByStatus(String status) throws SQLException {
        List<Order_CoffeeShop> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, u.full_name, u.phone, " +
                     "o.date, o.status, o.delivery_address, o.total " +
                     "FROM Orders o JOIN Users u ON o.customer_id = u.user_id " +
                     "WHERE o.status = ? " +
                     "ORDER BY o.date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order_CoffeeShop order = new Order_CoffeeShop(
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    "offline",
                    rs.getString("delivery_address"),
                    null
                );
                order.setOrderId(rs.getInt("order_id")); // Sử dụng setter mới
                order.orderTime = rs.getTimestamp("date").toLocalDateTime();
                order.status = rs.getString("status");
                orders.add(order);
            }
        }
        return orders;
    }

    /**
     * Xác nhận đơn hàng online
     */
    public static boolean confirmOnlineOrder(int orderId, int employeeId) throws SQLException {
        String sql = "UPDATE Orders SET status = 'confirmed', payment_status = 'paid', employee_id = ? " +
                     "WHERE order_id = ? AND status = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }


    /**
     * Hủy đơn hàng online
     */
    public static boolean cancelOnlineOrder(int orderId, String reason, int employeeId) throws SQLException {
        String sql = "UPDATE Orders SET status = 'cancelled', note = ?, employee_id = ? " +
                     "WHERE order_id = ? AND status = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reason);
            stmt.setInt(2, employeeId);
            stmt.setInt(3, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Lấy chi tiết sản phẩm trong đơn hàng
     */
    public List<CartItem> getOrderItems() throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.price, ol.quantity " +
                     "FROM Orderlines ol " +
                     "JOIN Products p ON ol.product_id = p.product_id " +
                     "WHERE ol.order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Products product = new Products(
                    rs.getString("product_name"),
                    rs.getFloat("price"),
                    "", "", 0
                );
                product.setId(rs.getInt("product_id"));
                
                CartItem item = new CartItem(product, rs.getInt("quantity"));
                items.add(item);
            }
        }
        return items;
    }
}