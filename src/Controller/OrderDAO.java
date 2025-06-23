package Controller;

import obj.Cart;
import obj.CartItem;
import obj.Customer;

import java.sql.*;
import java.util.Date;

/**
 * Data Access Object for Order operations
 */
public class OrderDAO {
    
    /**
     * Create a new order in the database
     * @param customer Customer placing the order
     * @param cart Cart containing items
     * @param paymentMethod "cash" for COD, "bank" for bank transfer
     * @param paymentStatus "paid", "unpaid", or "pending"
     * @param total Total amount including any discounts
     * @return Generated order ID, or -1 if failed
     */
    public static int createOrder(Customer customer, Cart cart, String paymentMethod, String paymentStatus, double total) {
        return createOrder(customer, cart, paymentMethod, paymentStatus, total, null);
    }

    /**
     * Create a new order in the database with delivery address
     * @param customer Customer placing the order
     * @param cart Cart containing items
     * @param paymentMethod "cash" for COD, "bank" for bank transfer
     * @param paymentStatus "paid", "unpaid", or "pending"
     * @param total Total amount including any discounts
     * @param deliveryAddress Custom delivery address, or null to use customer's address
     * @return Generated order ID, or -1 if failed
     */
    public static int createOrder(Customer customer, Cart cart, String paymentMethod, String paymentStatus, double total, String deliveryAddress) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Insert order into Orders table with all fields
            String orderQuery = "INSERT INTO Orders (customer_id, employee_id, date, status, payment_method, payment_status, note, delivery_address, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, customer.getId()); // customer_id
            orderStmt.setNull(2, Types.INTEGER);   // employee_id (null for customer orders)
            orderStmt.setTimestamp(3, new Timestamp(new Date().getTime())); // current date/time
            orderStmt.setString(4, "pending");     // status: enum('offline', 'pending', 'confirmed', 'cancelled')
            orderStmt.setString(5, paymentMethod); // payment_method: enum('cash', 'bank')
            orderStmt.setString(6, paymentStatus); // payment_status: enum('paid', 'unpaid', 'pending')
            orderStmt.setString(7, ""); // note
            String finalDeliveryAddress = (deliveryAddress != null && !deliveryAddress.trim().isEmpty()) 
                ? deliveryAddress.trim() 
                : (customer.getAddress() != null ? customer.getAddress() : "");
            orderStmt.setString(8, finalDeliveryAddress); // delivery_address
            orderStmt.setLong(9, Math.round(total)); // total (convert to BIGINT)
            
            int rowsAffected = orderStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated order ID
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    
                    // Create order lines for each cart item
                    if (createOrderLines(orderId, cart)) {
                        System.out.println(" Order created successfully with ID: " + orderId);
                        return orderId;
                    } else {
                        System.err.println(" Failed to create order lines for order ID: " + orderId);
                        return -1;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" Error creating order: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Create order lines (items) for an order
     * @param orderId Order ID to create lines for
     * @param cart Cart containing items
     * @return true if successful, false otherwise
     */
    private static boolean createOrderLines(int orderId, Cart cart) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            String orderLineQuery = "INSERT INTO Orderlines (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement orderLineStmt = conn.prepareStatement(orderLineQuery);
            
            for (CartItem item : cart.getItemsOrdered()) {
                orderLineStmt.setInt(1, orderId);
                orderLineStmt.setInt(2, item.getProduct().getId());
                orderLineStmt.setInt(3, item.getQuantity());
                orderLineStmt.setInt(4, item.getProduct().getPrice());
                orderLineStmt.addBatch();
            }
            
            int[] results = orderLineStmt.executeBatch();
            
            // Check if all order lines were created successfully
            for (int result : results) {
                if (result <= 0) {
                    System.err.println(" Failed to create some order lines");
                    return false;
                }
            }
            
            System.out.println(" Created " + results.length + " order lines successfully");
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" Error creating order lines: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update order payment status
     * @param orderId Order ID to update
     * @param paymentStatus New payment status
     * @return true if successful, false otherwise
     */
    public static boolean updateOrderPaymentStatus(int orderId, String paymentStatus) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            String updateQuery = "UPDATE Orders SET payment_status = ? WHERE order_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, paymentStatus);
            updateStmt.setInt(2, orderId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(" Updated order " + orderId + " payment status to: " + paymentStatus);
                return true;
            } else {
                System.err.println(" No order found with ID: " + orderId);
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" Error updating order payment status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update order status
     * @param orderId Order ID to update
     * @param status New order status
     * @return true if successful, false otherwise
     */
    public static boolean updateOrderStatus(int orderId, String status) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            String updateQuery = "UPDATE Orders SET status = ? WHERE order_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, status);
            updateStmt.setInt(2, orderId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println(" Updated order " + orderId + " status to: " + status);
                return true;
            } else {
                System.err.println(" No order found with ID: " + orderId);
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" Error updating order status: " + e.getMessage());
            return false;
        }
    }
} 