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

    @FXML
    void orderBtnPressed(ActionEvent event) {
        // Hiển thị thông báo đặt hàng thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đặt hàng thành công");
        alert.setHeaderText(null);
        String message = "Bạn đã đặt hàng thành công!";
        if (customer != null) {
            message += "\n\nBạn được thưởng 20 điểm cho đơn hàng này!";
        }
        alert.setContentText(message);

        // Chỉ cần gọi showAndWait() một lần
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
    }

}
