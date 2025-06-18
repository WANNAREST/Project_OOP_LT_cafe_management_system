package Controller;

import java.io.IOException;

import Cart_CoffeeShop.Cart_CoffeeShop;
import Menu.Products;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ProductController {

	@FXML
	private Label lblCost;

	@FXML
	private Label lblTitle;
	private Products product;

	private AnchorPane parentContainer;

	private Cart_CoffeeShop cart;

	@FXML
	void detailbtnPressed(ActionEvent event) {
		try {
			if (this.product == null) {
				System.err.println("Error: No product data available");
				return;
			}

			FXMLLoader loader = new FXMLLoader();
			// Đảm bảo đường dẫn đúng
			loader.setLocation(getClass().getResource("/view/user-product-detail-view.fxml"));
			AnchorPane detailView = loader.load();

			// Debug: Kiểm tra xem FXML có load được không
			System.out.println("FXML loaded successfully: " + (detailView != null));

			ProductDetailController controller = loader.getController();
			System.out.println("Controller instance: " + controller);

			controller.setCart(this.cart);
			controller.setProduct(this.product);

			// Clear và thêm nội dung mới
			if (parentContainer != null) {
				parentContainer.getChildren().clear();
				parentContainer.getChildren().add(detailView);
				AnchorPane.setTopAnchor(detailView, 0.0);
				AnchorPane.setBottomAnchor(detailView, 0.0);
				AnchorPane.setLeftAnchor(detailView, 0.0);
				AnchorPane.setRightAnchor(detailView, 0.0);
			} else {
				System.err.println("Parent container is null");
			}
		} catch (IOException e) {
			System.err.println("Error loading detail view: " + e.getMessage());
			e.printStackTrace();

			// Fallback UI
			Label errorLabel = new Label("Cannot load product details");
			if (parentContainer != null) {
				parentContainer.getChildren().clear();
				parentContainer.getChildren().add(errorLabel);
			}
		}
	}

	public ProductController(Products products) {
		this.product = product;
	}

	public ProductController() {
		// Constructor mặc định bắt buộc cho FXML
	}

	public void setData(Products product) {
		if (product == null) {
			System.err.println("Warning: Null product passed to ProductController.setData()");
			return;
		}
		this.product = product;

		if (lblCost != null) {
			lblCost.setText(String.valueOf(product.getPrice()) + "vnd");
		}
		if (lblTitle != null) {
			lblTitle.setText(product.getName());
		}
	}

	public void setParentContainer(AnchorPane parentContainer) {
		this.parentContainer = parentContainer;
	}

	public void setCart(Cart_CoffeeShop cart) {
		this.cart = cart;
		if (cart == null) {
			System.err.println("Error: Cart is null. Make sure to set the cart before initializing the view.");
			return;
		}
		if (lblTitle != null) {
			lblTitle.setText(product.getName());
		}
		if (lblCost != null) {
		}
	}
}