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

}
