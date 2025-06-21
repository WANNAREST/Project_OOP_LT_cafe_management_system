package Controller;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import obj.Product;
import obj.Store;
import obj.database;

public class InventoryController implements Initializable {

    @FXML private Button inventory_addBtn;
    @FXML private TableColumn<Product, Integer> inventory_col_productID;
    @FXML private TableColumn<Product, String> inventory_col_productName;
    @FXML private TableColumn<Product, String> inventory_col_type;
    @FXML private TableColumn<Product, Integer> inventory_col_price;
    @FXML private TableColumn<Product, Integer> inventory_col_stock;
    @FXML private TableColumn<Product, String> inventory_col_note;
    @FXML private Button inventory_deleteBtn;
    @FXML private ImageView inventory_imageView;
    @FXML private Button inventory_importBtn;
    @FXML private TextField inventory_productID;
    @FXML private TextField inventory_productName;
    @FXML private ComboBox<String> inventory_type;
    @FXML private TextField inventory_price;
    @FXML private TextField inventory_stock;
    @FXML private TextArea inventory_note;
    @FXML private TableView<Product> inventory_tableView;
    @FXML private Button inventory_updateBtn;
    @FXML private Button inventory_clearBtn;
    
    private Alert alert;
    private Image image;
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private String imagePath = "";
    private Store store;

    public void setStore(Store store) {
        this.store = store;
    }

    // ✅ THÊM SẢN PHẨM MỚI
    @FXML  // ✅ THÊM
    public void inventoryAddBtn() {
        String productName = inventory_productName.getText();
        String category = inventory_type.getValue();
        String priceText = inventory_price.getText();
        String stockText = inventory_stock.getText();
        String note = inventory_note.getText();

        if (productName.isEmpty() || category == null || priceText.isEmpty() || 
            stockText.isEmpty() || imagePath.isEmpty()) {
            
            showAlert(AlertType.ERROR, "Vui lòng điền đầy đủ thông tin và chọn hình ảnh!");
            return;
        }

        try {
            int price = Integer.parseInt(priceText);
            int stock = Integer.parseInt(stockText);

            if (price <= 0 || stock < 0) {
                showAlert(AlertType.ERROR, "Giá phải lớn hơn 0 và số lượng không được âm!");
                return;
            }

            String insertQuery = "INSERT INTO Products (product_name, category, price, stock, img_path, note) VALUES (?, ?, ?, ?, ?, ?)";
            connect = database.connectDB();
            prepare = connect.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            prepare.setString(1, productName);
            prepare.setString(2, category);
            prepare.setInt(3, price);
            prepare.setInt(4, stock);
            prepare.setString(5, imagePath);
            prepare.setString(6, note);

            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = prepare.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int productId = generatedKeys.getInt(1);
                        
                        Product newProduct = new Product(productId, productName, category, price, stock, imagePath, note);
                        if (store != null) {
                            store.addProduct(newProduct);
                        }
                        
                        showAlert(AlertType.INFORMATION, "✅ Thêm sản phẩm thành công!\nMã SP: " + productId);
                        inventoryShowData();
                        inventoryClearBtn();
                    }
                }
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Vui lòng nhập số hợp lệ cho giá và số lượng!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi thêm sản phẩm: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    // ✅ CẬP NHẬT SẢN PHẨM
    @FXML  // ✅ THÊM
    public void inventoryUpdateBtn() {
        String productId = inventory_productID.getText();
        String productName = inventory_productName.getText();
        String category = inventory_type.getValue();
        String priceText = inventory_price.getText();
        String stockText = inventory_stock.getText();
        String note = inventory_note.getText();

        if (productId.isEmpty() || productName.isEmpty() || category == null || 
            priceText.isEmpty() || stockText.isEmpty()) {
            showAlert(AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        try {
            int id = Integer.parseInt(productId);
            int price = Integer.parseInt(priceText);
            int stock = Integer.parseInt(stockText);

            if (price <= 0 || stock < 0) {
                showAlert(AlertType.ERROR, "Giá phải lớn hơn 0 và số lượng không được âm!");
                return;
            }

            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận cập nhật");
            confirmAlert.setHeaderText("Bạn có chắc muốn cập nhật sản phẩm này?");
            confirmAlert.setContentText("Sản phẩm: " + productName + " (ID: " + productId + ")");
            
            Optional<ButtonType> option = confirmAlert.showAndWait();
            
            if (option.isPresent() && option.get() == ButtonType.OK) {
                String updateQuery = "UPDATE Products SET product_name = ?, category = ?, price = ?, stock = ?, img_path = ?, note = ? WHERE product_id = ?";
                
                connect = database.connectDB();
                prepare = connect.prepareStatement(updateQuery);
                prepare.setString(1, productName);
                prepare.setString(2, category);
                prepare.setInt(3, price);
                prepare.setInt(4, stock);
                prepare.setString(5, imagePath);
                prepare.setString(6, note);
                prepare.setInt(7, id);
                
                int rowsAffected = prepare.executeUpdate();
                
                if (rowsAffected > 0) {
                    if (store != null) {
                        store.updateProduct(id, productName, category, price, stock, imagePath, note);
                    }
                    
                    showAlert(AlertType.INFORMATION, "✅ Cập nhật sản phẩm thành công!");
                    inventoryShowData();
                    inventoryClearBtn();
                } else {
                    showAlert(AlertType.ERROR, "Không tìm thấy sản phẩm để cập nhật!");
                }
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Vui lòng nhập số hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi cập nhật sản phẩm: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    // ✅ XÓA SẢN PHẨM
    @FXML  // ✅ THÊM
    public void inventoryDeleteBtn() {
        String productId = inventory_productID.getText();
        
        if (productId.isEmpty()) {
            showAlert(AlertType.ERROR, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa sản phẩm");
        confirmAlert.setHeaderText("⚠️ CẢNH BÁO: Bạn có chắc muốn xóa sản phẩm này?");
        confirmAlert.setContentText("Mã sản phẩm: " + productId + "\nHành động này không thể hoàn tác!");
        
        Optional<ButtonType> option = confirmAlert.showAndWait();
        
        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                String deleteQuery = "DELETE FROM Products WHERE product_id = ?";
                
                connect = database.connectDB();
                prepare = connect.prepareStatement(deleteQuery);
                prepare.setInt(1, Integer.parseInt(productId));
                
                int rowsAffected = prepare.executeUpdate();
                
                if (rowsAffected > 0) {
                    if (store != null) {
                        store.removeProduct(Integer.parseInt(productId));
                    }
                    
                    showAlert(AlertType.INFORMATION, "✅ Xóa sản phẩm thành công!");
                    inventoryShowData();
                    inventoryClearBtn();
                } else {
                    showAlert(AlertType.ERROR, "Không tìm thấy sản phẩm để xóa!");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Lỗi xóa sản phẩm: " + e.getMessage());
            } finally {
                closeResources();
            }
        }
    }

    // ✅ CHỌN SẢN PHẨM TỪ BẢNG
    @FXML  // ✅ THÊM
    public void inventorySelectData() {
        Product productData = inventory_tableView.getSelectionModel().getSelectedItem();
        int num = inventory_tableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1 || productData == null) {
            return;
        }

        inventory_productID.setText(String.valueOf(productData.getProductId()));
        inventory_productName.setText(productData.getProductName());
        inventory_type.setValue(productData.getCategory());
        inventory_price.setText(String.valueOf(productData.getPrice()));
        inventory_stock.setText(String.valueOf(productData.getStock()));
        inventory_note.setText(productData.getNote());
        
        // Load hình ảnh
        String imgPath = productData.getImgPath();
        if (imgPath != null && !imgPath.isEmpty()) {
            try {
                File imageFile = new File(imgPath);
                if (imageFile.exists()) {
                    String path = imageFile.toURI().toString();
                    image = new Image(path, 186, 150, false, true);
                    inventory_imageView.setImage(image);
                    imagePath = imgPath;
                } else {
                    inventory_imageView.setImage(null);
                    imagePath = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                inventory_imageView.setImage(null);
                imagePath = "";
            }
        } else {
            inventory_imageView.setImage(null);
            imagePath = "";
        }
    }

    // ✅ XÓA FORM
    @FXML  // ✅ THÊM
    public void inventoryClearBtn() {
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_type.setValue(null);
        inventory_price.clear();
        inventory_stock.clear();
        inventory_note.clear();
        inventory_imageView.setImage(null);
        imagePath = "";
    }

    // ✅ IMPORT HÌNH ẢNH
    @FXML  // ✅ THÊM
    public void inventoryImportBtn() {
        FileChooser open = new FileChooser();
        open.setTitle("Chọn hình ảnh sản phẩm");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.gif"));

        File file = open.showOpenDialog(inventory_importBtn.getScene().getWindow());

        if (file != null) {
            imagePath = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 186, 150, false, true);
            inventory_imageView.setImage(image);
        }
    }

    // ✅ LẤY DỮ LIỆU SẢN PHẨM TỪ DATABASE
    public ObservableList<Product> inventoryDataList() {
        ObservableList<Product> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM Products ORDER BY product_id";

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                Product product = new Product(
                    result.getInt("product_id"),
                    result.getString("product_name"),
                    result.getString("category"),
                    result.getInt("price"),
                    result.getInt("stock"),
                    result.getString("img_path"), // ✅ SỬA từ "image_path"
                    result.getString("note")
                );
                listData.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi tải dữ liệu: " + e.getMessage());
        } finally {
            closeResources();
        }

        return listData;
    }

    // ✅ HIỂN THỊ DỮ LIỆU TRONG BẢNG
    public void inventoryShowData() {
        ObservableList<Product> inventoryList = inventoryDataList();

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("category"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_note.setCellValueFactory(new PropertyValueFactory<>("note"));

        inventory_tableView.setItems(inventoryList);
    }

    private void closeResources() {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String message) {
        alert = new Alert(type);
        alert.setTitle(type == AlertType.ERROR ? "Lỗi" : "Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup category combo box
        inventory_type.getItems().addAll(
            "Coffee", "Tea", "Juice", "Smoothie", "Pastry", "Snack", "Other"
        );
        
        if (store == null) {
            store = new Store(); 
        }
        inventoryShowData();
    }
}
