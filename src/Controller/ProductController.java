package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import obj.Cart;
import obj.Product;

import java.io.IOException;

public class ProductController{

        @FXML
        private Label lblCost;

        @FXML
        private Label lblTitle;
        
        @FXML
        private ImageView productImageView;
        
        private Product product;

        private AnchorPane parentContainer;

        private Cart cart;

    @FXML
    void detailbtnPressed(ActionEvent event) {
        try {
            // Check if product is null before proceeding
            if (this.product == null) {
                System.err.println("Error: No product data available to display details");
                return; // Exit the method early if product is null
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-product-detail-view.fxml"));
            AnchorPane detailView = loader.load();
            DetailProductController controller = loader.getController();
            controller.setCart(this.cart);
            controller.setProduct(this.product);


            // Set anchors
            AnchorPane.setTopAnchor(detailView, 0.0);
            AnchorPane.setBottomAnchor(detailView, 0.0);
            AnchorPane.setLeftAnchor(detailView, 0.0);
            AnchorPane.setRightAnchor(detailView, 0.0);

            // Thay thế nội dung của container cha bằng view chi tiết
            if (parentContainer != null) {
                parentContainer.getChildren().setAll(detailView);
            } else {
                System.err.println("Error: Parent container is not set");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public ProductController() {

        }

        public void setData(Product product) {
        this.product = product;
            if (product == null) {
                System.err.println("Warning: Null product passed to ProductController.setData()");
                return;
            }

            if (lblCost != null) {
                lblCost.setText(String.valueOf(product.getPrice()) + "vnd");
            }
            if(lblTitle != null) {
                lblTitle.setText(product.getName());
            }
            
            // Set product image
            if (productImageView != null) {
                setProductImage(product);
            }

        }
        
    private void setProductImage(Product product) {
        System.out.println("🖼️ PRODUCT DEBUG: Product '" + product.getName() + "' - imgPath: '" + product.getImgPath() + "'");
        
        String imgPath = product.getImgPath();
        
        // Additional validation: if img_path doesn't look like a path, clear it
        if (imgPath != null && !imgPath.startsWith("/img/")) {
            System.out.println("⚠️ WARNING: Invalid img_path detected in ProductController: '" + imgPath + "' for product: " + product.getName());
            imgPath = null; // Clear invalid paths
        }
        
        // Only load if product has a specific image assigned
        if (imgPath != null && !imgPath.trim().isEmpty()) {
            try {
                // First try from compiled resources
                var inputStream = getClass().getResourceAsStream(imgPath);
                if (inputStream != null) {
                    Image loadedImage = new Image(inputStream);
                    if (!loadedImage.isError()) {
                        System.out.println("✅ Successfully loaded product image from resources: " + imgPath);
                        productImageView.setImage(loadedImage);
                        return;
                    }
                } else {
                    // Try from file system (for newly uploaded images not yet compiled)
                    System.out.println("🔄 Resource not compiled, trying file system: " + imgPath);
                    String projectPath = System.getProperty("user.dir");
                    String filePath = projectPath + "/src/main/resources" + imgPath;
                    java.io.File imageFile = new java.io.File(filePath);
                    
                    if (imageFile.exists()) {
                        Image loadedImage = new Image(imageFile.toURI().toString());
                        if (!loadedImage.isError()) {
                            System.out.println("✅ Successfully loaded product image from file system: " + imgPath);
                            productImageView.setImage(loadedImage);
                            return;
                        }
                    }
                    System.out.println("❌ Resource not found in resources or file system: " + imgPath);
                }
            } catch (Exception e) {
                System.out.println("❌ Failed to load product image: " + e.getMessage());
            }
        }
        
        // No image assigned - keep the default from FXML
        System.out.println("ℹ️ No specific image assigned to this product, keeping default from FXML");
    }
    

    
    public void setParentContainer(AnchorPane parentContainer) {
        this.parentContainer = parentContainer;
    }

    public void setCart(Cart cart){
        this.cart = cart;
        if(cart == null){
            System.err.println("Error: Cart is null. Make sure to set the cart before initializing the view.");
            return;
        }
        if(lblTitle != null){
            lblTitle.setText(product.getName());
        }
        if(lblCost != null){}
    }


}


