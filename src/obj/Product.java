package obj;

public abstract class Product {
    private int id;
    private String name;
    private double price;
    private String category;
    private String description;
    private int stock;



    public Product(int id, String name, String category,double price, int stock,  String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.description = description;
    }
    public Product(String name, String Category, double price, String description ) {
        this.name = name;
        this.price = price;
        this.category = Category;
    }

    public Product(String name, String category, double price) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return stock;
    }

    public void setQuantity(int quantity) {
        this.stock= quantity;
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

    public double calculateTotalPrice() {
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

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + stock +
                ", description='" + description + '\'' +
                '}';
    }
}
