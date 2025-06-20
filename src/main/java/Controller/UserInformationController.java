package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import obj.Customer;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UserInformationController implements Initializable {

    @FXML private TextField txtPhone;
    @FXML private TextField txtFullName;
    @FXML private TextField txtPassword;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private Button btnSave;
    
    @FXML private TableView<OrderData> ordersTable;
    @FXML private TableColumn<OrderData, String> colOrderDate;
    @FXML private TableColumn<OrderData, String> colProducts;
    @FXML private TableColumn<OrderData, Integer> colQuantity;
    @FXML private TableColumn<OrderData, String> colTotal;
    @FXML private TableColumn<OrderData, String> colPaymentMethod;
    @FXML private TableColumn<OrderData, String> colStatus;

    private Customer customer;
    private UserAppController parentController;
    private ObservableList<OrderData> ordersList = FXCollections.observableArrayList();

    public void setCustomer(Customer customer) {
        this.customer = customer;
        loadUserDetails();
        loadOrderHistory();
    }
    
    public void setParentController(UserAppController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colProducts.setCellValueFactory(new PropertyValueFactory<>("products"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        ordersTable.setItems(ordersList);
    }

    private void loadUserDetails() {
        if (customer == null) return;
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM Users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, customer.getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtPhone.setText(rs.getString("phone"));
                txtFullName.setText(rs.getString("full_name"));
                txtPassword.setText(rs.getString("password"));
                txtEmail.setText(rs.getString("email") != null ? rs.getString("email") : "");
                txtAddress.setText(rs.getString("address") != null ? rs.getString("address") : "");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading user details: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Failed to load user details");
        }
    }

    private void loadOrderHistory() {
        if (customer == null) return;
        
        ordersList.clear();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT o.date, o.total, o.payment_method, o.status, " +
                          "GROUP_CONCAT(CONCAT(p.product_name, ' (x', ol.quantity, ')') SEPARATOR ', ') as products, " +
                          "SUM(ol.quantity) as total_quantity " +
                          "FROM Orders o " +
                          "LEFT JOIN Orderlines ol ON o.order_id = ol.order_id " +
                          "LEFT JOIN Products p ON ol.product_id = p.product_id " +
                          "WHERE o.customer_id = ? " +
                          "GROUP BY o.order_id, o.date, o.total, o.payment_method, o.status " +
                          "ORDER BY o.date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, customer.getId());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderData order = new OrderData(
                    formatDateTime(rs.getTimestamp("date")),
                    rs.getString("products") != null ? rs.getString("products") : "No items",
                    rs.getInt("total_quantity"),
                    formatPrice(rs.getLong("total")),
                    capitalizeFirst(rs.getString("payment_method")),
                    capitalizeFirst(rs.getString("status"))
                );
                ordersList.add(order);
            }
            
            System.out.println("✅ Loaded " + ordersList.size() + " orders for customer: " + customer.getFullName());
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading order history: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Failed to load order history");
        }
    }

    @FXML
    private void saveUserDetails() {
        if (customer == null) return;
        
        // Validate inputs
        if (txtPhone.getText().trim().isEmpty() || txtFullName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Phone and Full Name are required!");
            return;
        }
        
        // Validate phone format
        String phone = txtPhone.getText().trim();
        if (!phone.matches("^(03[2-9]|05[6-9]|07[0-9]|08[1-9]|09[0-9])[0-9]{7}$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid phone number format!");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE Users " +
                          "SET phone = ?, full_name = ?, password = ?, email = ?, address = ? " +
                          "WHERE user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, phone);
            stmt.setString(2, txtFullName.getText().trim());
            stmt.setString(3, txtPassword.getText().trim());
            stmt.setString(4, txtEmail.getText().trim());
            stmt.setString(5, txtAddress.getText().trim());
            stmt.setInt(6, customer.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                // Update customer object - now using simple fullname
                customer.setFullName(txtFullName.getText().trim());
                customer.setPhone(phone);
                customer.setPassword(txtPassword.getText().trim());
                customer.setEmail(txtEmail.getText().trim());
                customer.setAddress(txtAddress.getText().trim());
                
                showAlert(Alert.AlertType.INFORMATION, "User details updated successfully!");
                System.out.println("✅ User details updated for: " + customer.getFullName());
                
                // Also update the customer in Customer table if it exists
                updateCustomerTable();
                
                // Call parent controller's updateUserDisplay method to refresh the name display
                if (parentController != null) {
                    parentController.updateUserDisplay();
                }
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update user details");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating user details: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    private void updateCustomerTable() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE Customers " +
                          "SET address = ?, points = ? " +
                          "WHERE user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, txtAddress.getText().trim());
            stmt.setInt(2, customer.getpoint()); // Keep existing points
            stmt.setInt(3, customer.getId());
            
            stmt.executeUpdate();
            System.out.println("✅ Customer table updated successfully");
            
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Could not update customer table: " + e.getMessage());
        }
    }

    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private String formatPrice(long price) {
        return String.format("%,d VND", price);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for order data
    public static class OrderData {
        private final String orderDate;
        private final String products;
        private final int totalQuantity;
        private final String total;
        private final String paymentMethod;
        private final String status;

        public OrderData(String orderDate, String products, int totalQuantity, 
                        String total, String paymentMethod, String status) {
            this.orderDate = orderDate;
            this.products = products;
            this.totalQuantity = totalQuantity;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.status = status;
        }

        // Getters for PropertyValueFactory
        public String getOrderDate() { return orderDate; }
        public String getProducts() { return products; }
        public int getTotalQuantity() { return totalQuantity; }
        public String getTotal() { return total; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getStatus() { return status; }
    }
} 