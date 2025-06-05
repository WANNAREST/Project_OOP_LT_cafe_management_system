package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import obj.Cart;

public class BankPaymentController {

    @FXML
    private Button btnCheckBank;

    @FXML
    private ImageView qrImg;

    @FXML
    private Label scanLabel;



    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;
        generateQRCode();
    }

    private void generateQRCode() {
        try {
            // Generate QR code from VietQR API
            String qrCodeUrl = PaymentAPI.generateQRCode(
                    cart.totalCost(),
                    "ORDER_" + System.currentTimeMillis(),
                    "970422" // MB Bank code
            );

            // Display QR code
            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                // Check if it's a data URL (base64 image) or regular URL
                if (qrCodeUrl.startsWith("data:image")) {
                    qrImg.setImage(new Image(qrCodeUrl));
                } else if (qrCodeUrl.startsWith("http")) {
                    qrImg.setImage(new Image(qrCodeUrl));
                } else {
                    // It's QR code string data - need to generate QR image from it
                    // For now, we'll create a service URL to generate the QR image
                    String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" +
                            java.net.URLEncoder.encode(qrCodeUrl, "UTF-8");
                    qrImg.setImage(new Image(qrImageUrl));
                }
            }

            // Display consistent manual transfer info (matching the API account)


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("QR Generation Error: " + e.getMessage());

            // Show manual transfer info even if QR fails
        }
    }

    @FXML
    private void confirmPaymentPressed() {
        System.out.println("Đơn hàng đã được thanh toán!");
        // Add your payment confirmation logic here
        // For example: close window or return to main screen
    }

    @FXML
    void checkBankbtnPressed(ActionEvent event) {
        // Add logic to check payment status
        // This could involve calling another API to verify payment
        System.out.println("Checking payment status...");
    }
}