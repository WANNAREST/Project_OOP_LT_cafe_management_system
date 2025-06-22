package obj;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;              // FK đến Customer
    private int employeeId;              // FK đến Employee (ai tạo đơn)
    private Date date;                   // ngày tạo đơn
    private OrderStatus status;          // trạng thái đơn hàng
    private PaymentMethod paymentMethod; // phương thức thanh toán
    private PaymentStatus paymentStatus; // trạng thái thanh toán
    private String note;                 // ghi chú đơn hàng
    private String deliveryAddress;      // địa chỉ giao hàng
    private double total;                // tổng tiền (bao gồm VAT)

    // Additional fields for legacy compatibility
    private String customerName;
    private String phoneNumber;
    private List<CartItem> orderItems;
    private LocalDateTime orderTime;

    // Constructor tương ứng với DB schema
    public Order(int orderId, int customerId, int employeeId, Date date,
                 OrderStatus status, PaymentMethod paymentMethod,
                 PaymentStatus paymentStatus, String note,
                 String deliveryAddress, double total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.note = note;
        this.deliveryAddress = deliveryAddress;
        this.total = total;
        this.orderTime = LocalDateTime.now();
    }

    // Default constructor for legacy compatibility
    public Order() {
        this.orderId = 0;
        this.customerId = 0;
        this.employeeId = 0;
        this.date = new Date();
        this.status = OrderStatus.OFFLINE;
        this.paymentMethod = PaymentMethod.CASH;
        this.paymentStatus = PaymentStatus.PENDING;
        this.note = "";
        this.deliveryAddress = "";
        this.total = 0.0;
        this.orderTime = LocalDateTime.now();
    }

    // Legacy constructor for CartController compatibility
    public Order(String customerName, String phoneNumber, String paymentMethod,
                 String deliveryAddress, List<CartItem> orderItems, int discount) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.orderItems = orderItems;
        this.orderId = 0;
        this.customerId = 0;
        this.employeeId = 0;
        this.date = new Date();
        this.status = OrderStatus.OFFLINE;
        this.paymentMethod = PaymentMethod.CASH;
        this.paymentStatus = PaymentStatus.PENDING;
        this.note = "";
        this.total = calculateTotalFromItems() - discount;
        this.orderTime = LocalDateTime.now();
    }

    // Helper method to calculate total from cart items
    private double calculateTotalFromItems() {
        if (orderItems == null) return 0.0;
        return orderItems.stream()
                .mapToInt(item -> item.getTotalPrice())
                .sum();
    }

    // Getter và Setter tương ứng với DB fields
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // Legacy compatibility methods
    public double getTotalAmount() {
        return total;
    }

    public void setTotalAmount(double total) {
        this.total = total;
    }

    public String getCustomerName() {
        return customerName != null ? customerName : "";
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<CartItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CartItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderTime() {
        if (orderTime != null) {
            return orderTime;
        }
        // Convert Date to LocalDateTime if needed
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return LocalDateTime.now();
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
        // Also update the Date field
        if (orderTime != null) {
            this.date = Date.from(orderTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    // Static methods for order management (database operations)
    public static java.util.List<Order> getOnlineOrdersByStatus(String statusStr) {
        java.util.List<Order> orders = new java.util.ArrayList<>();
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();

            String query = "SELECT o.order_id, o.customer_id, o.employee_id, o.date, o.status, " +
                    "o.payment_method, o.payment_status, o.note, o.delivery_address, o.total, " +
                    "u.full_name as customer_name, u.phone as phone_number " +
                    "FROM Orders o " +
                    "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN Users u ON c.customer_id = u.user_id " +
                    "WHERE o.status = ? " +
                    "ORDER BY o.date DESC";

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, statusStr);

            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setDate(rs.getTimestamp("date"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status").toUpperCase()));
                order.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method").toUpperCase()));
                order.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status").toUpperCase()));
                order.setNote(rs.getString("note"));
                order.setDeliveryAddress(rs.getString("delivery_address"));
                order.setTotal(rs.getDouble("total"));

                // Set legacy fields for controller compatibility
                order.setCustomerName(rs.getString("customer_name"));
                order.setPhoneNumber(rs.getString("phone_number"));
                order.setOrderTime(rs.getTimestamp("date").toLocalDateTime());

                // Load order items
                order.setOrderItems(getOrderItems(order.getOrderId()));

                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error fetching orders by status: " + e.getMessage());
        }

        return orders;
    }

    public static boolean confirmOnlineOrder(int orderId, int employeeId) {
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();

            String updateQuery = "UPDATE Orders SET status = 'confirmed', employee_id = ? WHERE order_id = ? AND status = 'pending'";
            java.sql.PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, employeeId);
            stmt.setInt(2, orderId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Order " + orderId + " confirmed by employee " + employeeId);
                return true;
            } else {
                System.err.println("❌ No pending order found with ID: " + orderId);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error confirming order: " + e.getMessage());
            return false;
        }
    }

    public static boolean cancelOnlineOrder(int orderId, String reason, int employeeId) {
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();

            String updateQuery = "UPDATE Orders SET status = 'cancelled', note = ?, employee_id = ? WHERE order_id = ? AND status = 'pending'";
            java.sql.PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, reason);
            stmt.setInt(2, employeeId);
            stmt.setInt(3, orderId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Order " + orderId + " cancelled by employee " + employeeId + " - Reason: " + reason);
                return true;
            } else {
                System.err.println("❌ No pending order found with ID: " + orderId);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error cancelling order: " + e.getMessage());
            return false;
        }
    }

    // Helper method to get order items from database
    private static java.util.List<CartItem> getOrderItems(int orderId) {
        java.util.List<CartItem> items = new java.util.ArrayList<>();
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();

            String query = "SELECT ol.quantity, ol.price, p.product_id, p.product_name, p.price as product_price " +
                    "FROM Orderlines ol " +
                    "JOIN Products p ON ol.product_id = p.product_id " +
                    "WHERE ol.order_id = ?";

            System.out.println("🔍 GET ORDER ITEMS: Fetching items for order ID: " + orderId);
            System.out.println("🔍 GET ORDER ITEMS: SQL Query: " + query);

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);

            java.sql.ResultSet rs = stmt.executeQuery();

            // First check if we have any results
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("product_name"));
                // Use the historical price from orderlines, not current product price
                product.setPrice(rs.getInt("price")); // This is ol.price from Orderlines table

                CartItem item = new CartItem(product, rs.getInt("quantity"));
                items.add(item);

                System.out.println("🔍 GET ORDER ITEMS: Added item: " + rs.getString("product_name") +
                        " x" + rs.getInt("quantity") + " @ " + rs.getInt("price") + " VND");
            }

            if (!hasResults) {
                System.out.println("❌ GET ORDER ITEMS: No orderlines found for order ID: " + orderId);

                // Let's check if the order exists at all
                String checkOrderQuery = "SELECT COUNT(*) as order_exists FROM Orders WHERE order_id = ?";
                java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkOrderQuery);
                checkStmt.setInt(1, orderId);
                java.sql.ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next()) {
                    int orderExists = checkRs.getInt("order_exists");
                    System.out.println("🔍 GET ORDER ITEMS: Order " + orderId + " exists: " + (orderExists > 0));
                }
                checkRs.close();
                checkStmt.close();

                // Let's check if there are orderlines for this order (without JOIN)
                String checkOrderlinesQuery = "SELECT COUNT(*) as orderline_count FROM Orderlines WHERE order_id = ?";
                java.sql.PreparedStatement checkOrderlinesStmt = conn.prepareStatement(checkOrderlinesQuery);
                checkOrderlinesStmt.setInt(1, orderId);
                java.sql.ResultSet checkOrderlinesRs = checkOrderlinesStmt.executeQuery();
                if (checkOrderlinesRs.next()) {
                    int orderlineCount = checkOrderlinesRs.getInt("orderline_count");
                    System.out.println("🔍 GET ORDER ITEMS: Orderlines for order " + orderId + ": " + orderlineCount);
                }
                checkOrderlinesRs.close();
                checkOrderlinesStmt.close();
            }

            System.out.println("🔍 GET ORDER ITEMS: Total items loaded: " + items.size());

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error fetching order items for order " + orderId + ": " + e.getMessage());
        }

        return items;
    }

    public boolean placeCompleteOrder(int employeeId) {
        this.employeeId = employeeId;
        System.out.println("📝 SAVE ORDER: Attempting to save order to database");
        System.out.println("📝 SAVE ORDER: Customer: " + (customerName != null ? customerName : "N/A"));
        System.out.println("📝 SAVE ORDER: Phone: " + (phoneNumber != null && !phoneNumber.isEmpty() ? phoneNumber : "N/A"));
        System.out.println("📝 SAVE ORDER: Total: " + total);
        System.out.println("📝 SAVE ORDER: Items count: " + (orderItems != null ? orderItems.size() : 0));

        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Determine customer_id based on phone number (if provided)
            int customerId = 0;
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                customerId = findCustomerIdByPhone(phoneNumber);
                System.out.println("📝 SAVE ORDER: Found customer ID: " + customerId + " for phone: " + phoneNumber);
            } else {
                System.out.println("📝 SAVE ORDER: No phone number provided, using customer_id = NULL");
            }

            // Validate employee_id exists in Employees table
            if (!validateEmployeeId(employeeId)) {
                System.err.println("❌ SAVE ORDER: Invalid employee ID: " + employeeId);
                return false;
            }

            // Insert order into Orders table
            String insertOrderSql = "INSERT INTO Orders (customer_id, employee_id, date, status, payment_method, payment_status, note, delivery_address, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println("📝 SQL: " + insertOrderSql);
            java.sql.PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, java.sql.Statement.RETURN_GENERATED_KEYS);

            // Handle customer_id - use NULL if 0 (anonymous order)
            if (customerId > 0) {
                orderStmt.setInt(1, customerId);
            } else {
                orderStmt.setNull(1, java.sql.Types.INTEGER);
            }
            orderStmt.setInt(2, employeeId);
            orderStmt.setTimestamp(3, new java.sql.Timestamp(date.getTime()));
            orderStmt.setString(4, status.name().toLowerCase());
            orderStmt.setString(5, paymentMethod.name().toLowerCase());
            orderStmt.setString(6, paymentStatus.name().toLowerCase());
            orderStmt.setString(7, note != null ? note : "");
            orderStmt.setString(8, deliveryAddress != null ? deliveryAddress : "");
            // Convert double to BIGINT (multiply by 1 to ensure it's a whole number)
            orderStmt.setLong(9, Math.round(total));

            System.out.println("📝 PARAMS: customer_id=" + customerId + ", employee_id=" + employeeId +
                    ", status=" + status.name().toLowerCase() + ", total=" + total);

            int orderRowsAffected = orderStmt.executeUpdate();
            System.out.println("📝 RESULT: " + orderRowsAffected + " rows affected in Orders table");

            if (orderRowsAffected > 0) {
                // Get the generated order ID
                java.sql.ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedOrderId = generatedKeys.getInt(1);
                    this.orderId = generatedOrderId;
                    System.out.println("✅ SAVE ORDER: Order saved with ID: " + generatedOrderId);

                    // Insert order items into Orderlines table
                    if (orderItems != null && !orderItems.isEmpty()) {
                        String insertOrderLineSql = "INSERT INTO Orderlines (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                        java.sql.PreparedStatement orderLineStmt = conn.prepareStatement(insertOrderLineSql);

                        for (CartItem item : orderItems) {
                            orderLineStmt.setInt(1, generatedOrderId);
                            orderLineStmt.setInt(2, item.getProduct().getId());
                            orderLineStmt.setInt(3, item.getQuantity());
                            // Convert double price to INT (database expects INT)
                            orderLineStmt.setInt(4, (int) Math.round(item.getProduct().getPrice()));
                            orderLineStmt.addBatch();
                        }

                        int[] batchResults = orderLineStmt.executeBatch();
                        System.out.println("✅ SAVE ORDER: " + batchResults.length + " order items saved");
                    }

                    conn.commit();
                    System.out.println("✅ SAVE ORDER: Order placed successfully!");
                    return true;
                }
            }

            conn.rollback();
            System.err.println("❌ SAVE ORDER: Failed to save order");
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ SAVE ORDER: Database error: " + e.getMessage());
            return false;
        }
    }

    private int findCustomerIdByPhone(String phoneNumber) {
        System.out.println("🔍 LOOKUP: Searching for customer with phone: " + phoneNumber);
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();
            String query = "SELECT c.customer_id FROM Customers c " +
                    "JOIN Users u ON c.customer_id = u.user_id " +
                    "WHERE u.phone = ? AND u.role = 'customer'";

            System.out.println("🔍 LOOKUP: Executing query: " + query);
            java.sql.PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, phoneNumber);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                System.out.println("✅ LOOKUP: Found customer ID: " + customerId);
                return customerId;
            } else {
                System.out.println("❌ LOOKUP: No customer found with phone: " + phoneNumber);

                // Let's also check what customers exist in the database
                String debugQuery = "SELECT u.phone, u.full_name, c.customer_id FROM Users u " +
                        "JOIN Customers c ON u.user_id = c.customer_id " +
                        "WHERE u.role = 'customer'";
                java.sql.PreparedStatement debugStmt = conn.prepareStatement(debugQuery);
                java.sql.ResultSet debugRs = debugStmt.executeQuery();

                System.out.println("🔍 DEBUG: Existing customers in database:");
                while (debugRs.next()) {
                    System.out.println("  - Phone: " + debugRs.getString("phone") +
                            ", Name: " + debugRs.getString("full_name") +
                            ", ID: " + debugRs.getInt("customer_id"));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error finding customer ID: " + e.getMessage());
            e.printStackTrace();
        }

        return 0; // Return 0 if customer not found
    }

    private boolean validateEmployeeId(int employeeId) {
        try {
            java.sql.Connection conn = Controller.db.DatabaseConnection.getConnection();
            String query = "SELECT employee_id FROM Employees WHERE employee_id = ?";

            java.sql.PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            java.sql.ResultSet rs = stmt.executeQuery();

            boolean exists = rs.next();
            if (exists) {
                System.out.println("✅ EMPLOYEE CHECK: Employee ID " + employeeId + " exists");
            } else {
                System.err.println("❌ EMPLOYEE CHECK: Employee ID " + employeeId + " not found in Employees table");

                // Show existing employees for debugging
                String debugQuery = "SELECT e.employee_id, u.full_name FROM Employees e " +
                        "JOIN Users u ON e.employee_id = u.user_id";
                java.sql.PreparedStatement debugStmt = conn.prepareStatement(debugQuery);
                java.sql.ResultSet debugRs = debugStmt.executeQuery();

                System.out.println("🔍 DEBUG: Existing employees:");
                while (debugRs.next()) {
                    System.out.println("  - ID: " + debugRs.getInt("employee_id") +
                            ", Name: " + debugRs.getString("full_name"));
                }
            }

            return exists;
        } catch (Exception e) {
            System.err.println("❌ Error validating employee ID: " + e.getMessage());
            return false;
        }
    }

    public void confirmPayment() {
        this.paymentStatus = PaymentStatus.PAID;
    }

    public boolean isOffline() {
        return status == OrderStatus.OFFLINE;
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public enum OrderStatus {
        OFFLINE("Tại quán"),       // Đơn hàng tại quán (offline)
        PENDING("Đang chờ"),       // Đơn hàng đang chờ xử lý
        CONFIRMED("Xác nhận"),     // Đơn hàng đã được xác nhận
        CANCELLED("Hủy");          // Đơn hàng đã bị hủy

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    public enum PaymentMethod {
        CASH("Tiền mặt"),
        BANK("Chuyển khoản");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    public enum PaymentStatus {
        PAID("Đã thanh toán"),
        UNPAID("Chưa thanh toán"),
        PENDING("Chờ thanh toán");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}