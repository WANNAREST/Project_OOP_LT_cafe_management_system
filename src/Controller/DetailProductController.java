package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import obj.Cart;
import obj.Product;

import javax.naming.LimitExceededException;

public class DetailProductController {

    public Cart cart;
    public Product product;


    @FXML
    private Label desLabel;

    @FXML
    private Label nameLbael;

    @FXML
    private Label priceLabel;

    @FXML
    private Label quantitNumlabel;
    
    @FXML
    private ImageView productDetailImageView;


    public void initializeData(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
        setData(product);

        // Set quantity mặc định là 1 nếu label đã được load
        if (quantitNumlabel != null) {
            quantitNumlabel.setText("1");
        }

        System.out.println("Cart and Product initialized in DetailProductController");
    }




    @FXML
    void addLoveListbtnPressed(ActionEvent event) {

    }

    @FXML
    void addtoCartbtnPressed(ActionEvent event) throws LimitExceededException {
        try {
            if(cart == null){
                System.err.println("Error: Cart is null. Make sure to set the cart before adding to cart.");
                return;
            }
            if(product == null){
                System.err.println("Error: Product is null. Make sure to set the product before adding to cart.");
                return;
            }

            // Kiểm tra xem quantitNumlabel có null không
            if(quantitNumlabel == null || quantitNumlabel.getText() == null || quantitNumlabel.getText().isEmpty()) {
                System.err.println("Error: Quantity label is null or empty");
                return;
            }

            int quantity = Integer.parseInt(quantitNumlabel.getText());

            // Kiểm tra quantity hợp lệ
            if(quantity <= 0) {
                System.err.println("Error: Invalid quantity: " + quantity);
                return;
            }

            cart.addProduct(product, quantity);
            System.out.println("Successfully added " + quantity + " x " + product.getName() + " to cart");

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid quantity format: " + quantitNumlabel.getText());
        } catch (LimitExceededException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    void quantityAddbtnPressed(ActionEvent event) {
        try {
            if(quantitNumlabel == null) {
                System.err.println("Error: Quantity label is null");
                return;
            }

            int quantity = Integer.parseInt(quantitNumlabel.getText());
            quantity++;
            quantitNumlabel.setText(String.valueOf(quantity));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing quantity: " + e.getMessage());
            quantitNumlabel.setText("1"); // Reset to default
        }

    }

    @FXML
    void quantityRemovebtnPressed(ActionEvent event) {
        try {
            if(quantitNumlabel == null) {
                System.err.println("Error: Quantity label is null");
                return;
            }

            int quantity = Integer.parseInt(quantitNumlabel.getText());
            if(quantity > 1){
                quantity--;
            }
            quantitNumlabel.setText(String.valueOf(quantity));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing quantity: " + e.getMessage());
            quantitNumlabel.setText("1"); // Reset to default
        }

    }

    void setData(Product product){
        if (product == null) {
            System.err.println("Error: Null product passed to DetailProductController.setData()");
            return;
        }
        this.product = product;

        if ( nameLbael !=null){
            nameLbael.setText(product.getName());
        }
        if (desLabel !=null){
            desLabel.setText(product.getDescription());
        }
        if (priceLabel !=null){
            priceLabel.setText(String.valueOf(product.getPrice()) + "vnd");
        }
        if (quantitNumlabel != null) {
            quantitNumlabel.setText("1"); // Set quantity mặc định
        }
        
        // Set product image
        if (productDetailImageView != null) {
            setProductImage(product);
        }
    }
    
    private void setProductImage(Product product) {
        System.out.println("🖼️ DETAIL DEBUG: Product '" + product.getName() + "' - imgPath: '" + product.getImgPath() + "'");
        
        String imgPath = product.getImgPath();
        
        // Additional validation: if img_path doesn't look like a path, clear it
        if (imgPath != null && !imgPath.startsWith("/img/")) {
            System.out.println("⚠️ WARNING: Invalid img_path detected in DetailProductController: '" + imgPath + "' for product: " + product.getName());
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
                        System.out.println("✅ Successfully loaded product detail image from resources: " + imgPath);
                        productDetailImageView.setImage(loadedImage);
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
                            System.out.println("✅ Successfully loaded product detail image from file system: " + imgPath);
                            productDetailImageView.setImage(loadedImage);
                            return;
                        }
                    }
                    System.out.println("❌ Resource not found in resources or file system: " + imgPath);
                }
            } catch (Exception e) {
                System.out.println("❌ Failed to load product detail image: " + e.getMessage());
            }
        }
        
        // No image assigned - keep the default from FXML
        System.out.println("ℹ️ No specific image assigned to this product, keeping default from FXML");
    }
    


    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setProduct(Product product) {
        this.product = product;
        setData(product);
    }


}
