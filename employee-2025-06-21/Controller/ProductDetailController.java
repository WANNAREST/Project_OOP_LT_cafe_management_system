package Controller;

import javax.naming.LimitExceededException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import obj.Cart;
import obj.Products;

public class ProductDetailController {

	public Cart cart;
	public Products product;
	
	@FXML
	private Label desLabel;

	@FXML
	private Label nameLbael;

	@FXML
	private Label priceLabel;

	@FXML
	private Label quantitNumlabel;

	public void initializeData(Cart cart, Products product) {
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

    void setData(Products product){
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
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setProduct(Products product) {
        this.product = product;
        setData(product);
    }

}