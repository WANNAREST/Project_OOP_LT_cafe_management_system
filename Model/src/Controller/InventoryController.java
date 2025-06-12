package Controller;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import obj.Coffee;
import obj.Product;
import obj.Store;

public class InventoryController implements Initializable {
    @FXML
    private ImageView inventory_ImageView;
    
    @FXML
    private TextArea inventory_note;

    @FXML
    private TextField inventory_price;

    @FXML
    private TextField inventory_productID;

    @FXML
    private TextField inventory_productName;

    @FXML
    private TextField inventory_stock;

    @FXML
    private TextField inventory_type;

    @FXML
    private Button inventory_addBtn;

    @FXML
    private Button inventory_clearBtn;
    
    @FXML
    private TableView<Product> inventory_TableView;

    @FXML
    private HBox main_form;
    
    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private Button inventory_importBtn;

    @FXML
    private Button inventory_updateBtn;

    
    @FXML
    private TableColumn<Product, String> inventory_col_ID;

    @FXML
    private TableColumn<Product, String> inventory_col_date;

    @FXML
    private TableColumn<Product, String> inventory_col_price;

    @FXML
    private TableColumn<Product, String> inventory_col_product_name;

    @FXML
    private TableColumn<Product, String> inventory_col_note;

    @FXML
    private TableColumn<Product, String> inventory_col_stock;

    @FXML
    private TableColumn<Product, String> inventory_col_type;
    
    private Alert alert;
    private Image image;
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private static Store store;
    public void setStore(Store store) {
        this.store = store;
    }
    public void inventoryAddBtn() {
        // System.out.println("Has accesssed to AddBtn");
        
        if (inventory_productID.getText().isEmpty()
                || inventory_productName.getText().isEmpty()
                || inventory_type.getText().isEmpty() 
                || inventory_stock.getText().isEmpty()
                || inventory_price.getText().isEmpty()
                || data.path == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
           
        } else {
           String prodType = inventory_type.getText();
           String prodId = inventory_productID.getText();
           String prodName = inventory_productName.getText();
           int stock = Integer.parseInt(inventory_stock.getText());
           Double prodPrice = Double.parseDouble(inventory_price.getText());
           String note = inventory_note.getText();
           String image = data.path.replace("\\", "\\\\");
             Date date = new Date();
             java.sql.Date sqlDate = new java.sql.Date(date.getTime());


            String checkProdID = "SELECT prod_id FROM product WHERE prod_id = '"
                    + inventory_productID.getText() + "'";
            
            connect = database.connectDB();
            
            
            try {
                
                statement = connect.createStatement();
                result = statement.executeQuery(checkProdID);
                
                if (result.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(inventory_productID.getText() + " is already taken");
                    alert.showAndWait();
                } else {
                    String insertData = "INSERT INTO product "
                            + "(prod_id, prod_name, type, stock, price, note, image, date) "
                            + "VALUES(?,?,?,?,?,?,?,?)";
                    
                    prepare = connect.prepareStatement(insertData);
                    prepare.setString(1, inventory_productID.getText());
                    prepare.setString(2, inventory_productName.getText());
                    prepare.setString(3, (String) inventory_type.getText());
                    prepare.setString(4, inventory_stock.getText());
                    prepare.setString(5, inventory_price.getText());
                    prepare.setString(6, (String) inventory_note.getText());
                    
                    String path = data.path;
                    path = path.replace("\\", "\\\\");
                    
                    prepare.setString(7, path);
                 
                  
                  
       
                    prepare.setString(8, String.valueOf(sqlDate));
                    
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                    
                    inventoryShowData();
                    inventoryClearBtn();
                    Product product = new Product(prodName, prodType, prodPrice, image);
                    store.addProduct(product);
                   
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       
    }
    public void inventorySelectData() {	       
         System.out.println("Successfully Selected Data");
           Product prodData = inventory_TableView.getSelectionModel().getSelectedItem();
           int num = inventory_TableView.getSelectionModel().getSelectedIndex();
       
            if ((num - 1) < -1) {
            return;
          }
        
        inventory_productID.setText(prodData.getProductId());
        inventory_productName.setText(prodData.getProductName());
        inventory_stock.setText(String.valueOf(prodData.getStock()));
        inventory_type.setText(String.valueOf(prodData.getType()));
        inventory_price.setText(String.valueOf(prodData.getPrice()));
        inventory_note.setText(String.valueOf(prodData.getNote()));
        data.path = prodData.getImage();
        String path = "File:" + prodData.getImage();
        data.date = String.valueOf(prodData.getDate());
        data.id = prodData.getId();
      //  System.out.println(data.id);
        image = new Image(path, 186, 150, false, true);
        inventory_ImageView.setImage(image);
    }
    
    public void inventoryClearBtn() {
    //	System.out.println("Has accesssed to ClearBtn");
        inventory_productID.setText("");
        inventory_productName.setText("");
        inventory_type.setText("");
        inventory_stock.setText("");
        inventory_price.setText("");
        inventory_note.setText("");
        data.path = "";
        data.id = 0;
        inventory_ImageView.setImage(null);
        
    }
    public void inventoryImportBtn() {
    //	System.out.println("Has accessed to importBtn");
    	FileChooser openFile = new FileChooser();
        //openFile.getExtensionFilters().add(new ExtensionFilter("Open Image File", "*png", "*jpg"));
        openFile.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = openFile.showOpenDialog(main_form.getScene().getWindow());
        
        if (file != null) {
            
            data.path = file.getAbsolutePath();
            
            image = new Image(file.toURI().toString(), 186, 150, false, true);   
         
            inventory_ImageView.setImage(image);
        }
    }
    
   
    
    public ObservableList<Product> inventoryDataList() {
       ObservableList<Product> listData = FXCollections.observableArrayList();
       String sql = "SELECT * FROM product";
       connect = database.connectDB();
       try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            Product prod;
            
            while (result.next()) {
                prod = new Product(result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getString("type"),
                        result.getInt("stock"),
                        result.getDouble("price"),
                        result.getString("note"),
                        result.getString("image"),
                        result.getDate("date"));
       
                listData.add(prod);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listData;
    }
    //public ObservableList<Product> inventoryListData;
    public void inventoryShowData() {
    	System.out.println("Số sản phẩm khởi tạo ở main : " + store.itemsInStore);   	
    	store.itemsInStore = inventoryDataList();
    	inventory_col_ID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        inventory_col_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        inventory_TableView.setItems(store.itemsInStore);
        System.out.println("Số lượng dòng lấy được: " + store.itemsInStore.size());
//        ObservableList<Product> Copylist = FXCollections.observableArrayList(store.itemsInStore);;
//        for (Product product : Copylist) {
//        	Product newproduct = new Product(product.getProductName(), product.getType() , product.getPrice());
//            store.addProduct(newproduct);
//        }
//         System.out.println("Đã thêm " + store.itemsInStore.size() + " sản phẩm vào Store.");
    }
    public void inventoryUpdateBtn() {
        
        if (inventory_productID.getText().isEmpty()
                || inventory_productName.getText().isEmpty()
                || inventory_type.getText().isEmpty()
                || inventory_stock.getText().isEmpty()
                || inventory_price.getText().isEmpty()
                || data.path == null) {            
            if (data.path == null) {
            	alert.setContentText("Datapath is Null");
            } else {
        	alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            }
        } else {
            
            String path = data.path;
            path = path.replace("\\", "\\\\");
      
            String updateData = "UPDATE product SET "
                    + "prod_id = '" + inventory_productID.getText() + "', prod_name = '"
                    + inventory_productName.getText() + "', type = '"
                    + inventory_type.getText() + "', stock = '"
                    + inventory_stock.getText() + "', price = '"
                    + inventory_price.getText() + "', note = '"
                    + inventory_note.getText() + "', image = '"
                    + path + "', date = '"
                    + data.date + "' WHERE id = " + data.id;
            
            connect = database.connectDB();
            
            try {
                 
              
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE Product ID: " + inventory_productID.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();
                
                if (option.get().equals(ButtonType.OK)) {
                    prepare = connect.prepareStatement(updateData);
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Updated!");
                    alert.showAndWait();
                    // TO UPDATE YOUR TABLE VIEW
                    inventoryShowData();
                    // TO CLEAR YOUR FIELDS
                    inventoryClearBtn();
                    
                } else {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Cancelled.");
                    alert.showAndWait();
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void inventoryDeleteBtn() {
    	// System.out.println("Accessed to DeleteBtn");
        if (data.id == 0) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            
        } else {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to DELETE Product ID: " + inventory_productID.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();
            
            if (option.get().equals(ButtonType.OK)) {
                String deleteData = "DELETE FROM product WHERE id = " + data.id;
                
                
                try {
                    prepare = connect.prepareStatement(deleteData);
                    prepare.executeUpdate();
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Deleted!");
                    alert.showAndWait();

                    // TO UPDATE YOUR TABLE VIEW
                    inventoryShowData();
                    // TO CLEAR YOUR FIELDS
                    inventoryClearBtn();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Cancelled");
                alert.showAndWait();
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	System.out.println("Đã khởi động initialize");
    	if (store == null) {
            store = new Store(); 
        }
    	inventoryShowData();
    }
    
}
