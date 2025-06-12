package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import obj.Product;
import javafx.scene.image.Image;
public class ProductController {

        @FXML
        private Button detailbtnPressed;

        @FXML
        private Label lblCost;

        @FXML
        private Label lblTitle;

        @FXML 

        private ImageView productImage;
        
        public ProductController() {

        }

        public void setData(Product product) {
            if (lblCost != null) {
                lblCost.setText(String.valueOf(product.getPrice()) + "vnd");
            }
            if(lblTitle != null) {
                lblTitle.setText(product.getProductName());
            }
            if (detailbtnPressed != null) {
                detailbtnPressed.setVisible(true);
            }
             if (productImage != null && product.getImage() != null && !product.getImage().isEmpty()) {
                 try {
                 Image img = new Image("file:" + product.getImage(), 151, 82, true, true);
                 productImage.setImage(img);
              }  catch (Exception e) {
                 e.printStackTrace();
                 System.err.println("Could not load image: " + product.getImage());
              }
             }
        }
}

