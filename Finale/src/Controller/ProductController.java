package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import obj.Cart; // ✅ THÊM import
import obj.Product;

import javax.naming.LimitExceededException;
import java.io.File;

public class ProductController<T extends Product> {

    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label productStockLabel;
    @FXML private Button addToCartBtn;

    private T product;
    // ✅ THÊM - Cart instance
    private Cart cart;

    public void setData(T product) {
        this.product = product;
        updateProductDisplay();
    }

    // ✅ THÊM - Set Cart
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    private void updateProductDisplay() {
        if (product != null) {
            productNameLabel.setText(product.getProductName());
            productPriceLabel.setText(product.getFormattedPrice());
            productStockLabel.setText("Còn lại: " + product.getStock());

            // Load image
            String imagePath = product.getImgPath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        productImageView.setImage(image);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }

            // Update button state
            updateAddToCartButton();
        }
    }

    private void updateAddToCartButton() {
        if (product != null && addToCartBtn != null) {
            boolean inStock = product.isInStock();
            addToCartBtn.setDisable(!inStock);
            addToCartBtn.setText(inStock ? "Thêm vào giỏ" : "Hết hàng");
            
            if (!inStock) {
                addToCartBtn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
            } else {
                addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
        }
    }

    @FXML
    private void handleAddToCart() {
        if (product == null) {
            showAlert("Lỗi", "Không có sản phẩm để thêm!", Alert.AlertType.ERROR);
            return;
        }

        if (!product.isInStock()) {
            showAlert("Hết hàng", "Sản phẩm này hiện đã hết hàng!", Alert.AlertType.WARNING);
            return;
        }

        if (cart == null) {
            showAlert("Lỗi", "Giỏ hàng chưa được khởi tạo!", Alert.AlertType.ERROR);
            return;
        }

        try {
            // ✅ THÊM - Add product to cart
            cart.addProduct(product);
            
            showAlert("Thành công", 
                     "Đã thêm '" + product.getProductName() + "' vào giỏ hàng!\n" +
                     "Tổng sản phẩm trong giỏ: " + cart.getTotalItems(), 
                     Alert.AlertType.INFORMATION);
            
            // Update button text briefly
            addToCartBtn.setText("✓ Đã thêm");
            addToCartBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            
            // Reset button after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        if (addToCartBtn != null) {
                            addToCartBtn.setText("Thêm vào giỏ");
                            addToCartBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (LimitExceededException e) {
            showAlert("Giỏ hàng đầy", "Giỏ hàng đã đạt số lượng tối đa cho phép!", Alert.AlertType.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể thêm sản phẩm vào giỏ hàng: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ THÊM - Quick view product details
    @FXML
    private void handleQuickView() {
        if (product != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chi tiết sản phẩm");
            alert.setHeaderText(product.getProductName());
            alert.setContentText(
                "Loại: " + product.getCategory() + "\n" +
                "Giá: " + product.getFormattedPrice() + "\n" +
                "Số lượng: " + product.getStock() + "\n" +
                "Ghi chú: " + (product.getNote() != null ? product.getNote() : "Không có")
            );
            alert.showAndWait();
        }
    }
}

