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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import obj.Employee;
import obj.Product;
import obj.Store;

public class EmployeeController implements Initializable {

    @FXML
    private TextField employee_ID;

    @FXML
    private ImageView employee_ImageView;

    @FXML
    private TextField employee_Name;

    @FXML
    private TableView<Employee> employee_TableView;

    @FXML
    private Button employee_addBtn;

    @FXML
    private TextField employee_base_salary;

    @FXML
    private Button employee_clearBtn;

    @FXML
    private TableColumn<Employee, String> employee_col_ID;

    @FXML
    private TableColumn<Employee, String> employee_col_base_salary;

    @FXML
    private TableColumn<Employee, String> employee_col_name;

    @FXML
    private TableColumn<Employee, String> employee_col_note;

    @FXML
    private TableColumn<Employee, String> employee_col_phone;

    @FXML
    private TableColumn<Employee, String> employee_col_role;

    @FXML
    private Button employee_deleteBtn;

    @FXML
    private Button employee_importBtn;

    @FXML
    private TextArea employee_note;

    @FXML
    private TextField employee_phone;

    @FXML
    private TextField employee_role;

    @FXML
    private Button employee_updateBtn;

    @FXML
    private HBox main_form;
    
    private Alert alert;
    private Image image;
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    
    @FXML
    public void employeeAddBtn()  {
    	  
        if (employee_ID.getText().isEmpty()
                || employee_Name.getText().isEmpty()
                || employee_base_salary.getText().isEmpty() 
                || employee_role.getText().isEmpty()
                || employee_phone.getText().isEmpty()
                || data.path == null) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
           
        } else {
//           String prodType = inventory_type.getText();
//           String prodId = inventory_productID.getText();
//           String prodName = inventory_productName.getText();
//           int stock = Integer.parseInt(inventory_stock.getText());
//           double prodPrice = Double.parseDouble(inventory_price.getText());
//           String note = inventory_note.getText();
           String image = data.path.replace("\\", "\\\\");
//             Date date = new Date();
//             java.sql.Date sqlDate = new java.sql.Date(date.getTime());


            String checkEmployeeID = "SELECT employee_id FROM employee WHERE employee_id = '"
                    + employee_ID.getText() + "'";
            
            connect = database.connectDB();
            
            
            try {
                
                statement = connect.createStatement();
                result = statement.executeQuery(checkEmployeeID);
                
                if (result.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(employee_ID.getText() + " is already taken");
                    alert.showAndWait();
                } else {
                    String insertData = "INSERT INTO employee "
                            + "(employee_id, employee_name, role, base_salary, phone, note, image) "
                            + "VALUES(?,?,?,?,?,?,?)";
                    
                    prepare = connect.prepareStatement(insertData);
                    prepare.setString(1, employee_ID.getText());
                    prepare.setString(2, employee_Name.getText());
                    prepare.setString(3, employee_role.getText());
                    prepare.setString(4, employee_base_salary.getText());
                    prepare.setString(5, employee_phone.getText());
                    prepare.setString(6, (String) employee_note.getText());
                    
                    String path = data.path;
                    path = path.replace("\\", "\\\\");
                    
                    prepare.setString(7, path);
                 
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                    
                    employeeShowData();
                    employeeClearBtn();
                   
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public ObservableList<Employee> EmployeeListData;
    
    
    public ObservableList<Employee> EmployeeDataList() {
        ObservableList<Employee> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employee";
        connect = database.connectDB();
        try {
             prepare = connect.prepareStatement(sql);
             result = prepare.executeQuery();
             
             Employee employee;
             
             while (result.next()) {
                 employee = new Employee(result.getInt("id"),
                         result.getString("employee_id"),
                         result.getString("employee_name"),
                         result.getString("role"),
                         result.getInt("base_salary"),
                         result.getString("phone"),
                         result.getString("note"),
                         result.getString("image")
                         );
        
                 listData.add(employee);
             }
             
         } catch (Exception e) {
             e.printStackTrace();
           }
         
         return listData;
     }
    
    @FXML
    public void employeeShowData() {
        	EmployeeListData = EmployeeDataList();
        	employee_col_ID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        	employee_col_name.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        	employee_col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        	employee_col_base_salary.setCellValueFactory(new PropertyValueFactory<>("base_salary"));
        	employee_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        	employee_col_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        	employee_TableView.setItems(EmployeeListData);
    }
    @FXML
    void employeeClearBtn() {
    	employee_ID.setText("");
    	employee_Name.setText("");
    	employee_role.setText("");
    	employee_base_salary.setText("");
    	employee_phone.setText("");
    	employee_note.setText("");
         data.path = "";
         data.id = 0;
         employee_ImageView.setImage(null);
         
    }

    @FXML
    void employeeDeleteBtn() {
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
              alert.setContentText("Are you sure you want to DELETE Employee ID: " + employee_ID.getText() + "?");
              Optional<ButtonType> option = alert.showAndWait();
              
              if (option.get().equals(ButtonType.OK)) {
                  String deleteData = "DELETE FROM employee WHERE id = " + data.id;
                  
                  
                  try {
                      prepare = connect.prepareStatement(deleteData);
                      prepare.executeUpdate();
                      alert = new Alert(AlertType.ERROR);
                      alert.setTitle("Error Message");
                      alert.setHeaderText(null);
                      alert.setContentText("Successfully Deleted!");
                      alert.showAndWait();

                      // TO UPDATE YOUR TABLE VIEW
                      employeeShowData();
                      // TO CLEAR YOUR FIELDS
                      employeeClearBtn();
                      
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

    @FXML
    void employeeImportBtn() {
    	FileChooser openFile = new FileChooser();
        //openFile.getExtensionFilters().add(new ExtensionFilter("Open Image File", "*png", "*jpg"));
        openFile.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = openFile.showOpenDialog(main_form.getScene().getWindow());
        
        if (file != null) {
            
            data.path = file.getAbsolutePath();
            
            image = new Image(file.toURI().toString(), 186, 150, false, true);   
         
            employee_ImageView.setImage(image);
        }
    }

    @FXML
    void employeeSelectData() {
        System.out.println("Successfully Selected Data");
        Employee employeeData = employee_TableView.getSelectionModel().getSelectedItem();
        int num = employee_TableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < -1 || employeeData == null) {
            return;
        }

        employee_ID.setText(employeeData.getEmployeeId());
        employee_Name.setText(employeeData.getEmployeeName());
        employee_base_salary.setText(String.valueOf(employeeData.getBase_salary()));
        employee_phone.setText(employeeData.getPhone());
        employee_role.setText(employeeData.getRole());
        employee_note.setText(employeeData.getNote());
        
        data.path = employeeData.getImage();
        data.id = employeeData.getId();

        // SỬA: Kiểm tra và load ảnh an toàn
        if (data.path != null && !data.path.isEmpty()) {
            try {
                File imageFile = new File(data.path);
                if (imageFile.exists()) {
                    String path = imageFile.toURI().toString();
                    image = new Image(path, 186, 150, false, true);
                    employee_ImageView.setImage(image);
                } else {
                    // Nếu file không tồn tại, hiển thị ảnh mặc định hoặc để trống
                    employee_ImageView.setImage(null);
                    System.out.println("Image file not found: " + data.path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                employee_ImageView.setImage(null);
            }
        } else {
            employee_ImageView.setImage(null);
        }
    }

    @FXML
    void employeeUpdateBtn() {
        if (employee_ID.getText().isEmpty()
                || employee_Name.getText().isEmpty()
                || employee_role.getText().isEmpty()
                || employee_base_salary.getText().isEmpty()
                || employee_phone.getText().isEmpty()
                || data.path == null || data.path.isEmpty()) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        // Kiểm tra file ảnh có tồn tại không
        File imageFile = new File(data.path);
        if (!imageFile.exists()) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Image file does not exist!");
            alert.showAndWait();
            return;
        }

        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to UPDATE Employee ID: " + employee_ID.getText() + "?");
        Optional<ButtonType> option = alert.showAndWait();
        
        if (option.get().equals(ButtonType.OK)) {
            
            // SỬA: Sử dụng PreparedStatement thay vì String concatenation
            String updateData = "UPDATE employee SET employee_id = ?, employee_name = ?, role = ?, base_salary = ?, phone = ?, note = ?, image = ? WHERE id = ?";
            
            connect = database.connectDB();
            
            try {
                prepare = connect.prepareStatement(updateData);
                prepare.setString(1, employee_ID.getText());
                prepare.setString(2, employee_Name.getText());
                prepare.setString(3, employee_role.getText());
                prepare.setString(4, employee_base_salary.getText());
                prepare.setString(5, employee_phone.getText());
                prepare.setString(6, employee_note.getText());
                prepare.setString(7, data.path); // Không cần replace \\ nữa
                prepare.setInt(8, data.id);
                
                int result = prepare.executeUpdate();
                
                if (result > 0) {
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Updated!");
                    alert.showAndWait();
                    
                    // Update table và clear form
                    employeeShowData();
                    employeeClearBtn();
                } else {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to update employee!");
                    alert.showAndWait();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("Error updating employee: " + e.getMessage());
                alert.showAndWait();
            } finally {
                // Đóng connection
                try {
                    if (prepare != null) prepare.close();
                    if (connect != null) connect.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	employeeShowData();
    }
}
