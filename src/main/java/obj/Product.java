package obj;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private int price;
    private String category;
    private String description;
    private int stock;
    private String note; // For AdminPart compatibility
    private String imgPath; // For AdminPart compatibility
    
    private static int nbProduct = 0;

    // Default constructor
    public Product() {
        this.id = 0;
        this.name = "";
        this.price = 0;
        this.category = "";
        this.description = "";
        this.stock = 0;
        this.note = "";
        this.imgPath = "";
    }

    // Constructor with all parameters (from Products)
    public Product(String name, int price, String description, String category, int stock) {
        this.id = ++nbProduct;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.note = "";
        this.imgPath = "";
    }

    // Constructor with id (from original Product)
    public Product(int id, String name, String category, int price, int stock, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.description = description;
        this.note = "";
        this.imgPath = "";
    }

    // Constructor for AdminPart compatibility (with all fields including note and imgPath)
    public Product(int id, String name, String category, int price, int stock, String description, String imgPath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.description = description;
        this.note = "";
        this.imgPath = imgPath;
    }

    // Constructor with id but no stock (from original Product)
    public Product(int id, String name, String category, int price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.stock = 0; // Default stock
        this.note = "";
        this.imgPath = "";
    }

    // Simple constructor (from original Product)
    public Product(String name, String category, int price, String description) {
        this.id = ++nbProduct;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.stock = 0; // Default stock
        this.note = "";
        this.imgPath = "";
    }

    // Minimal constructor (from original Product)
    public Product(String name, String category, int price) {
        this.id = ++nbProduct;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = "";
        this.stock = 0;
        this.note = "";
        this.imgPath = "";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return stock;
    }

    public void setQuantity(int quantity) {
        this.stock = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // AdminPart compatibility getters
    public int getProductId() {
        return id;
    }

    public String getProductName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    // Business logic methods
    public int calculateTotalPrice() {
        return price * stock;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public boolean reduceStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return true;
        }
        return false;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    // Match method from Products class
    public boolean isMatch(String name) {
        return this.name.toLowerCase().contains(name.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + stock +
                ", description='" + description + '\'' +
                '}';
    }
}
