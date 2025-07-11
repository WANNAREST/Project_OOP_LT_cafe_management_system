package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import obj.Cart;
import obj.Customer;

public class CODPaymentController {
    private Cart cart;
    private PaymentController parentController;
    private Customer customer;
    private String deliveryAddress;

    @FXML
    private Button orderBtn;

    public void setCart(Cart cart){
        this.cart = cart;
    }

    public void setCustomer (Customer customer){
        this.customer = customer;
    }

    public void setParentController(PaymentController parentController){
        this.parentController = parentController;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @FXML
    void orderBtnPressed(ActionEvent event) {
        // Create order in database first
        if (customer == null || cart == null) {
            showAlert("Lỗi", "Thông tin đơn hàng không hợp lệ!", Alert.AlertType.ERROR);
            return;
        }

        // Calculate total (you may want to include discount logic here)
        double total = cart.totalCost();

        // Create order in database with delivery address
        int orderId = OrderDAO.createOrder(customer, cart, "cash", "unpaid", total, deliveryAddress);

        if (orderId > 0) {
            // Order created successfully - show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đặt hàng thành công");
            alert.setHeaderText(null);
            String message = "Bạn đã đặt hàng thành công!\nMã đơn hàng: " + orderId;
            if (customer != null) {
                message += "\n\nBạn được thưởng 20 điểm cho đơn hàng này!";
            }
            alert.setContentText(message);

            // Show alert and handle response
            alert.showAndWait().ifPresent(response -> {
                if (customer != null) {
                    customer.rewardOrderPoints(); // Thưởng 20 point
                    System.out.println("Customer " + customer.getFullName() +
                            " rewarded with 20 points. Total points: " + customer.getpoint());

                    // Update customer points in database
                    updateCustomerPointsInDatabase(customer.getPhone(), customer.getpoint());

                    if (parentController != null) {
                        parentController.updatePointDisplay();
                    }
                }

                if(cart != null){
                    cart.clearCart();
                }

                if(parentController != null){
                    // Gọi phương thức để quay về menu chính
                    parentController.orderSuccess();
                }
            });
        } else {
            // Order creation failed
            showAlert("Lỗi", "Không thể tạo đơn hàng. Vui lòng thử lại!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateCustomerPointsInDatabase(String phoneNumber, int newPointBalance) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            System.err.println(" Cannot update points: phone number is null or empty");
            return;
        }

        String updateSql = "UPDATE Customers c " +
                "JOIN Users u ON c.customer_id = u.user_id " +
                "SET c.bonus_point = ? " +
                "WHERE u.phone = ? AND u.role = 'customer'";

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, newPointBalance);
            stmt.setString(2, phoneNumber);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" Customer points updated in database: " + newPointBalance + " points for phone: " + phoneNumber);
            } else {
                System.err.println(" Failed to update customer points for phone: " + phoneNumber);
            }

        } catch (java.sql.SQLException e) {
            System.err.println(" Error updating customer points: " + e.getMessage());
        }
    }

}
