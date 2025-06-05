package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import obj.Cart;

public class CODPaymentController {
    private Cart cart;
    private PaymentController parentController;

    @FXML
    private Button orderBtn;

    public void setCart(Cart cart){
        this.cart = cart;
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
        alert.setContentText("Bạn đã đặt hàng thành công!");

        // Chỉ cần gọi showAndWait() một lần
        alert.showAndWait().ifPresent(response -> {
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
