package application;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
//import java.sql.Date;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class SampleController implements Initializable {
	    @FXML
	    private Button calendar_btn;

	    @FXML
	    private Button customers_btn;

	    @FXML
	    private Button dashboard_btn;

	    @FXML
	    private ImageView inventory_ImageView;
	    
	    @FXML
	    private TableView<productData> inventory_TableView;
	    @FXML
	    private Button inventory_addBtn;

	    @FXML
	    private Button inventory_btn;

	    @FXML
	    private Button inventory_clearBtn;

	    @FXML
	    private TableColumn<productData, String> inventory_col_ID;

	    @FXML
	    private TableColumn<productData, String> inventory_col_date;

	    @FXML
	    private TableColumn<productData, String> inventory_col_price;

	    @FXML
	    private TableColumn<productData, String> inventory_col_product_name;

	    @FXML
	    private TableColumn<productData, String> inventory_col_note;

	    @FXML
	    private TableColumn<productData, String> inventory_col_stock;

	    @FXML
	    private TableColumn<productData, String> inventory_col_type;

	    @FXML
	    private Button inventory_deleteBtn;

	    @FXML
	    private AnchorPane inventory_form;

	    @FXML
	    private Button inventory_importBtn;
	    @FXML
	    private Button inventory_updateBtn;
	    @FXML
	    private Button logout_btn;
	    
	    @FXML
	    private AnchorPane main_form;
	    @FXML
	    private TextField inventory_price;

	    @FXML
	    private TextField inventory_productID;

	    @FXML
	    private TextField inventory_productName;
	    @FXML
	    private TextField inventory_stock;
      
	    @FXML
	    private TextArea inventory_note;
	    @FXML
	    private ComboBox<?> inventory_type;
	    
	    @FXML
	    private Button tracking_btn;
	    
	    @FXML
	    private Label username;
	    
	    private String[] typeList =  {"Meals", "Drinks"};
	    private Alert alert;
	    private Image image;
	    private Connection connect;
	    private PreparedStatement prepare;
	    private Statement statement;
	    private ResultSet result;
	    
	    
	    public void inventoryAddBtn() {
	        System.out.println("Has accesssed to AddBtn");
	        if (inventory_productID.getText().isEmpty()
	                || inventory_productName.getText().isEmpty()
	                || inventory_type.getSelectionModel().getSelectedItem() == null
	                || inventory_stock.getText().isEmpty()
	                || inventory_price.getText().isEmpty()
	                || data.path == null) {
	            alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Please fill all blank fields");
	            alert.showAndWait();
	        } else {
	            // CHECK PRODUCT ID
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
	                    prepare.setString(3, (String) inventory_type.getSelectionModel().getSelectedItem());
	                    prepare.setString(4, inventory_stock.getText());
	                    prepare.setString(5, inventory_price.getText());
	                    prepare.setString(6, (String) inventory_note.getText());
	                    
	                    String path = data.path;
	                    path = path.replace("\\", "\\\\");
	                    
	                    prepare.setString(7, path);

	                    // TO GET CURRENT DATE
	                    java.util.Date date = new java.util.Date();
	                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	                 
	                    prepare.setString(8, String.valueOf(sqlDate));
	                    
	                    prepare.executeUpdate();
	                    
	                    alert = new Alert(AlertType.INFORMATION);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Successfully Added!");
	                    alert.showAndWait();
	                    
	                    inventoryShowData();
	                    inventoryClearBtn();
	                }
	                
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    public void inventorySelectData() {	        
	        productData prodData = inventory_TableView.getSelectionModel().getSelectedItem();
	        int num = inventory_TableView.getSelectionModel().getSelectedIndex();
	        
	        if ((num - 1) < -1) {
	            return;
	        }
	        
	        inventory_productID.setText(prodData.getProductId());
	        inventory_productName.setText(prodData.getProductName());
	        inventory_stock.setText(String.valueOf(prodData.getStock()));
	        inventory_price.setText(String.valueOf(prodData.getPrice()));
	        
	        data.path = prodData.getImage();
	        
	        String path = "File:" + prodData.getImage();
	        data.date = String.valueOf(prodData.getDate());
	        data.id = prodData.getId();
	        
	        image = new Image(path, 186, 150, false, true);
	        inventory_ImageView.setImage(image);
	    }
        
	    public void inventoryClearBtn() {
	    	System.out.println("Has accesssed to ClearBtn");
	        inventory_productID.setText("");
	        inventory_productName.setText("");
	        inventory_type.getSelectionModel().clearSelection();
	        inventory_stock.setText("");
	        inventory_price.setText("");
	        inventory_note.setText("");
	        data.path = "";
	        data.id = 0;
	        inventory_ImageView.setImage(null);
	        
	    }
	    public void inventoryImportBtn() {
	    	System.out.println("Has accessed to importBtn");
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
        
	   
	    
	    public ObservableList<productData> inventoryDataList() {
	       ObservableList<productData> listData = FXCollections.observableArrayList();
	       String sql = "SELECT * FROM product";
	       connect = database.connectDB();
	       try {
	            prepare = connect.prepareStatement(sql);
	            result = prepare.executeQuery();
	            
	            productData prod;
	            
	            while (result.next()) {
	                prod = new productData(result.getInt("id"),
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
	    public ObservableList<productData> inventoryListData;
	    public void inventoryShowData() {
	    	inventoryListData = inventoryDataList();
	    	inventory_col_ID.setCellValueFactory(new PropertyValueFactory<>("productId"));
	        inventory_col_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
	        inventory_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
	        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
	        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
	        inventory_col_note.setCellValueFactory(new PropertyValueFactory<>("note"));
	        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
	        inventory_TableView.setItems(inventoryListData);
	    }
	    public void inventoryUpdateBtn() {
	        
	        if (inventory_productID.getText().isEmpty()
	                || inventory_productName.getText().isEmpty()
	                || inventory_type.getSelectionModel().getSelectedItem() == null
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
	                    + inventory_type.getSelectionModel().getSelectedItem() + "', stock = '"
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
		public void inventoryTypelist() {
				List<String> typeL  = new ArrayList<>();
				for(String data : typeList) {
					typeL.add(data);
				}
			ObservableList listData = FXCollections.observableArrayList(typeL);	
			inventory_type.setItems(listData);
	    }
		
	    public void logout() {
	        try {
	            
	            alert = new Alert(AlertType.CONFIRMATION);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Are you sure you want to logout?");
	            Optional<ButtonType> option = alert.showAndWait();
	            
	            if (option.get().equals(ButtonType.OK)) {
	                // TO HIDE MAIN FORM 
	                logout_btn.getScene().getWindow().hide();
	                // LINK YOUR LOGIN FORM AND SHOW IT 
	                Parent root = FXMLLoader.load(getClass().getResource("logout.fxml"));
	                Stage stage = new Stage();
	                Scene scene = new Scene(root);
	                stage.setTitle("Cafe Shop Management System");
	                stage.setScene(scene);
	                stage.show();
	                
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    public void displayUsername() {
	        String user = data.username;
	        user = user.substring(0, 1).toUpperCase() + user.substring(1);
	        username.setText(user);
	    }
	   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		inventoryTypelist();
		inventoryShowData();
	}
}

