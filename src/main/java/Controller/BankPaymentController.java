package Controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import obj.Cart;
import obj.Customer;

public class BankPaymentController {

    @FXML
    private Button btnCheckBank;

    @FXML
    private ImageView qrImg;

    @FXML
    private Label scanLabel;



    private Cart cart;
    private String currentOrderId;
    private int paymentAmount;
    private Customer customer;
    private PaymentController parentController;
    private String deliveryAddress;

    private int discount = 0;

    public void setCart(Cart cart) {
        this.cart = cart;
        this.paymentAmount=cart.totalCost();

        generateQRCode();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setParentController(PaymentController parentController) {
        this.parentController = parentController;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    private void generateQRCode() {
        try {

            currentOrderId = "ORDER" + System.currentTimeMillis();
            // Generate QR code from VietQR API
            String qrCodeUrl = PaymentAPI.generateQRCode(
                   paymentAmount,
                    currentOrderId,
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
        btnCheckBank.setDisable(true);
        btnCheckBank.setText("Đang kiểm tra...");

        Task<Boolean> checkPaymentTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Thread.sleep(2000);
                // Call API to verify transaction
                return PaymentAPI.verifyTransaction(paymentAmount, currentOrderId);
            }

            @Override
            protected void succeeded() {
                // Re-enable button
                btnCheckBank.setDisable(false);
                btnCheckBank.setText("Kiểm tra thanh toán");

                // Handle result
                Boolean paymentFound = getValue();
                if (paymentFound) {
                    // Payment successful
                    showAlert("Thành công",
                            "Thanh toán thành công!\n" +
                                    "Mã đơn hàng: " + currentOrderId + "\n" +
                                    "Số tiền: " + String.format("%,d VND", paymentAmount),
                            Alert.AlertType.INFORMATION);

                    // You can add logic here to:
                    // - Save order to database
                    // - Close payment window
                    // - Navigate to success page
                    processSuccessfulPayment();

                } else {
                    // Payment not found
                    showAlert("Chưa thanh toán",
                            "Chưa tìm thấy giao dịch.\n\n" +
                                    "Vui lòng kiểm tra:\n" +
                                    "- Đã chuyển khoản đúng số tiền: " + String.format("%,d VND", paymentAmount) + "\n" +
                                    "- Nội dung chuyển khoản có chứa: " + currentOrderId + "\n" +
                                    "- Đợi 1-2 phút để hệ thống cập nhật",
                            Alert.AlertType.WARNING);
                }
            }

            @Override
            protected void failed() {
                // Re-enable button
                btnCheckBank.setDisable(false);
                btnCheckBank.setText("Kiểm tra thanh toán");

                Throwable exception = getException();
                String errorMessage = exception.getMessage();


                // Show error message
                if (errorMessage.contains("429")) {
                    showAlert("Lỗi",
                            "Đã gọi API quá nhiều lần. Vui lòng đợi 1-2 phút trước khi thử lại.",
                            Alert.AlertType.ERROR);
                } else {
                    showAlert("Lỗi",
                            "Không thể kiểm tra thanh toán: " + errorMessage,
                            Alert.AlertType.ERROR);
                }
            }
        };

        // Run task in background thread
        Thread thread = new Thread(checkPaymentTask);
        thread.setDaemon(true);
        thread.start();
    }
    private void processSuccessfulPayment() {
        // Create order in database
        if (customer == null || cart == null) {
            System.err.println("❌ Cannot process payment: customer or cart is null");
            return;
        }

        // Create order in database with bank payment method and delivery address
        int orderId = OrderDAO.createOrder(customer, cart, "bank", "paid", paymentAmount, deliveryAddress);
        
        if (orderId > 0) {
            System.out.println("✅ Order created in database with ID: " + orderId);
            
            // Reward customer points
            if (customer != null) {
                customer.rewardOrderPoints(); // Thưởng 20 point
                System.out.println("Customer " + customer.getFullName() +
                        " rewarded with 20 points. Total points: " + customer.getpoint());
            }

            // Clear cart and navigate back
            if (cart != null) {
                cart.clearCart();
            }

            if (parentController != null) {
                parentController.updatePointDisplay();
                parentController.orderSuccess();
            }
        } else {
            System.err.println("❌ Failed to create order in database");
            showAlert("Lỗi", "Không thể tạo đơn hàng trong cơ sở dữ liệu", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    }
