package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Cart_CoffeeShop.Cart_CoffeeShop;
import DAO.EmployeeDAO;
import Menu.Products;
import Model.Employee;
import Store.Store_CoffeeShop;

public class StoreController {

	public Store_CoffeeShop store;
	public Cart_CoffeeShop cart;

	private Parent cartView;
	private CartController cartController;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Circle avt;

	@FXML
	private AnchorPane contentPane;

	@FXML
	private GridPane gridPane;

	@FXML
	private Label nameLabel;

	@FXML
	private Label numCoinLabel;

	@FXML
	private ScrollPane srcollPane;

	@FXML
	private TextField searchField;

	public StoreController(Store_CoffeeShop store) {
		this(store, new Cart_CoffeeShop())	;
		// Load sản phẩm từ database khi khởi tạo
        store.loadProductsFromDB();
	}

	public StoreController(Store_CoffeeShop store, Cart_CoffeeShop cart) {
		this.store = store;
		 //this.cart = cart == null ? new Cart_CoffeeShop() : cart;
		this.cart = cart;
	}

	@FXML
	private void handleSearchAction(ActionEvent event) {
		performSearch();
	}

	private void performSearch() {
		String searchText = searchField.getText().toLowerCase().trim();
		if (searchText.isEmpty()) {
			refreshProductGrid(); // Show all products if search is empty
			return;
		}

		filterProduct(searchText);
	}

	private void filterProduct(String searchText) {
		if (contentPane == null || store == null) {
			System.err.println("Error: contentPane or store is null");
		}

		contentPane.getChildren().clear();
		ScrollPane newScrollPane = new ScrollPane();
		GridPane newGridPane = new GridPane();

		// Configure grid
		newGridPane.getChildren().clear();
		newGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
		newGridPane.setHgap(20);
		newGridPane.setVgap(20);
		newGridPane.setPadding(new Insets(20));
		newGridPane.getColumnConstraints().clear();

		for (int i = 0; i < 3; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(33.33);
			newGridPane.getColumnConstraints().add(column);
		}

		int col = 0;
		int row = 0;

		for (Products product : store.getItemsInStore()) {
			// Check if product name or description contains search text
			if (product.getName().toLowerCase().contains(searchText)) {
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));
					AnchorPane productView = fxmlLoader.load();
					productView.setPrefWidth(Region.USE_COMPUTED_SIZE);
					productView.setPrefHeight(Region.USE_COMPUTED_SIZE);

					ProductController controller = fxmlLoader.getController();
					controller.setData(product);
					controller.setParentContainer(contentPane);
					controller.setCart(this.cart);

					newGridPane.add(productView, col, row);
					GridPane.setMargin(productView, new Insets(10));

					col++;
					if (col == 3) {
						col = 0;
						row++;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Error loading product view: " + e.getMessage());
				}
			}
		}

		newGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
		newGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);

		newScrollPane.setFitToWidth(true);
		newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		newScrollPane.setPannable(true);
		newScrollPane.setContent(newGridPane);

		contentPane.getChildren().add(newScrollPane);

		AnchorPane.setTopAnchor(newScrollPane, 0.0);
		AnchorPane.setBottomAnchor(newScrollPane, 0.0);
		AnchorPane.setLeftAnchor(newScrollPane, 0.0);
		AnchorPane.setRightAnchor(newScrollPane, 0.0);
	}

	@FXML
	void showCart(ActionEvent event) {
		contentPane.getChildren().clear();
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/user-cart-view.fxml"));
			CartController controller = new CartController(cart);
			controller.setParentController(this);
			controller.setCoinLabel(numCoinLabel);
			fxmlLoader.setController(controller);
			HBox root = fxmlLoader.load();
			contentPane.getChildren().add(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void showFavouriteList(ActionEvent event) {

	}

	@FXML
	void showMenu(ActionEvent event) {
		refreshProductGrid();
	}

	public void refreshProductGrid() {
	    System.out.println("Đang refresh grid với " + store.getItemsInStore().size() + " sản phẩm");
	    createProductGrid();
	    
	    // Debug thêm
	    if (contentPane == null) {
	        System.err.println("Lỗi: contentPane là null");
	    }
	    if (store == null) {
	        System.err.println("Lỗi: store là null");
	    }
	}

	private void createProductGrid() {
		if (store == null) {
			System.err.println("Error: Store is null. Make sure to set the store before initializing the view.");
			return;
		}
		System.out.println("Số lượng sản phẩm trong cửa hàng: " + store.getItemsInStore().size());

		// Create new grid components
		ScrollPane newScrollPane = new ScrollPane();
		GridPane newGridPane = new GridPane();

		// Configure grid
		newGridPane.getChildren().clear();
		newGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
		newGridPane.setHgap(20);
		newGridPane.setVgap(20);
		newGridPane.setPadding(new Insets(20));
		newGridPane.getColumnConstraints().clear();

		for (int i = 0; i < 3; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(33.33);
			newGridPane.getColumnConstraints().add(column);
		}

		int col = 0;
		int row = 0;

		for (int i = 0; i < store.getItemsInStore().size(); i++) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));
			    AnchorPane productView = fxmlLoader.load();
			    
			    // Lấy controller và set data
			    ProductController controller = fxmlLoader.getController();
			    controller.setData(store.getItemsInStore().get(i));
			    controller.setParentContainer(contentPane);
			    controller.setCart(this.cart);

			    newGridPane.add(productView, col, row);
			    GridPane.setMargin(productView, new Insets(10));

			    col++;
			    if (col == 3) {
			        col = 0;
			        row++;
			    }

			} catch (IOException e) {
				System.err.println("Error loading product view: " + e.getMessage());
			    e.printStackTrace();
			    // Tạo fallback UI nếu cần
			    Label errorLabel = new Label("Error loading product: " + store.getItemsInStore().get(i).getName());
			    newGridPane.add(errorLabel, col, row);
			    col++;
			}
		}

		newGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
		newGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);

		newScrollPane.setFitToWidth(true);
		newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		newScrollPane.setPannable(true);
		newScrollPane.setContent(newGridPane);

		contentPane.getChildren().add(newScrollPane);

		AnchorPane.setTopAnchor(newScrollPane, 0.0);
		AnchorPane.setBottomAnchor(newScrollPane, 0.0);
		AnchorPane.setLeftAnchor(newScrollPane, 0.0);
		AnchorPane.setRightAnchor(newScrollPane, 0.0);
	}

	@FXML
	void showSearch(ActionEvent event) {
		performSearch();
	}

	@FXML
	void showUserDetail(ActionEvent event) {
		contentPane.getChildren().clear();

		try {
			// Use getResource with a proper path
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-information.fxml"));
			HBox root = loader.load();
			contentPane.getChildren().add(root);
		} catch (Exception e) {
			e.printStackTrace();
			// Add user-friendly error handling
			System.err.println("Could not load user detail view: " + e.getMessage());
		}
	}
	
	@FXML
	void showWorkSchedule(ActionEvent event) {
	    contentPane.getChildren().clear();
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/workSchedule.fxml"));
	        AnchorPane root = loader.load();
	        
	        WorkScheduleController controller = loader.getController();
	        
	        // 1. Lấy thông tin nhân viên đang đăng nhập (ví dụ: employeeId = 3 - Lê Minh C)
	        int loggedInEmployeeId = 3; // Thay bằng ID thực từ hệ thống đăng nhập
	        
	        // 2. Load thông tin từ database
	        EmployeeDAO employeeDAO = new EmployeeDAO();
	        Employee currentEmployee = employeeDAO.getEmployeeById(loggedInEmployeeId);
	        
	        // 3. Truyền dữ liệu thực vào controller
	        controller.setEmployee(currentEmployee);
	        
	        contentPane.getChildren().add(root);
	    } catch (Exception e) {
	        e.printStackTrace();
	        showAlert("Lỗi", "Không thể tải lịch làm việc: " + e.getMessage());
	    }
	}

	// Thêm phương thức hiển thị thông báo
	private void showAlert(String title, String message) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}

	@FXML
	void signOut(ActionEvent event) {

	}

	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the product grid
		createProductGrid();

		searchField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				performSearch();
			}
		});
	}
	
	@FXML
	void showOrderOnline(ActionEvent event) {
	    contentPane.getChildren().clear();
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/order-manager.fxml"));
	        Parent root = loader.load();
	        contentPane.getChildren().add(root);
	        
	        // Khởi tạo controller nếu cần
	        OrderManagerController controller = loader.getController();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}