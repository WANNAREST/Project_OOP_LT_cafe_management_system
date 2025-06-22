package Controller.customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import obj.Cart;
import obj.Product;

import java.io.IOException;

public class ProductController{

    @FXML
    private Label lblCost;

    @FXML
    private Label lblTitle;
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
            Controller.DetailProductController controller = loader.getController();
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


