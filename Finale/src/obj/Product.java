package obj;

import javafx.beans.property.*;

public class Product {
    private IntegerProperty productId;
    private StringProperty productName;
    private StringProperty category;
    private IntegerProperty price;
    private IntegerProperty stock;
    private StringProperty imgPath;
    private StringProperty note;
    // ✅ THÊM - quantity cho cart
    private IntegerProperty quantity;

    public Product() {
        this.productId = new SimpleIntegerProperty();
        this.productName = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.price = new SimpleIntegerProperty();
        this.stock = new SimpleIntegerProperty();
        this.imgPath = new SimpleStringProperty();
        this.note = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty(1); // ✅ Default quantity = 1
    }

    public Product(int productId, String productName, String category, int price, int stock, String imgPath, String note) {
        this();
        setProductId(productId);
        setProductName(productName);
        setCategory(category);
        setPrice(price);
        setStock(stock);
        setImgPath(imgPath);
        setNote(note);
        setQuantity(1); // ✅ Default quantity = 1
    }

    // ✅ CONSTRUCTOR FOR COFFEE COMPATIBILITY
    public Product(String productName, String category, double price) {
        this();
        setProductId(0); // Auto-generate hoặc set sau
        setProductName(productName);
        setCategory(category);
        setPrice((int) price);
        setStock(100); // Default stock
        setImgPath("");
        setNote("");
        setQuantity(1); // ✅ Default quantity = 1
    }

    // ✅ CONSTRUCTOR CHO COFFEE VỚI STRING PRODUCT ID
    public Product(String productId, String productName, int price) {
        this();
        try {
            setProductId(Integer.parseInt(productId.replaceAll("[^0-9]", "")));
        } catch (NumberFormatException e) {
            setProductId(0);
        }
        setProductName(productName);
        setCategory("Coffee");
        setPrice(price);
        setStock(100); // Default stock
        setImgPath("");
        setNote("");
        setQuantity(1); // ✅ Default quantity = 1
    }

    // Getters
    public int getProductId() { return productId.get(); }
    public String getProductName() { return productName.get(); }
    public String getCategory() { return category.get(); }
    public int getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }
    public String getImgPath() { return imgPath.get(); }
    public String getNote() { return note.get(); }
    public int getQuantity() { return quantity.get(); } // ✅ THÊM

    // Setters
    public void setProductId(int value) { this.productId.set(value); }
    public void setProductName(String value) { this.productName.set(value); }
    public void setCategory(String value) { this.category.set(value); }
    public void setPrice(int value) { this.price.set(value); }
    public void setStock(int value) { this.stock.set(value); }
    public void setImgPath(String value) { this.imgPath.set(value); }
    public void setNote(String value) { this.note.set(value); }
    public void setQuantity(int value) { this.quantity.set(value); } // ✅ THÊM

    // Properties
    public IntegerProperty productIdProperty() { return productId; }
    public StringProperty productNameProperty() { return productName; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty priceProperty() { return price; }
    public IntegerProperty stockProperty() { return stock; }
    public StringProperty imgPathProperty() { return imgPath; }
    public StringProperty noteProperty() { return note; }
    public IntegerProperty quantityProperty() { return quantity; } // ✅ THÊM

    // Formatted getters
    public String getFormattedPrice() {
        return String.format("%,d VND", price.get());
    }

    // ✅ TOTAL PRICE CHO CART
    public long getTotalPrice() {
        return (long) price.get() * quantity.get();
    }

    public String getFormattedTotalPrice() {
        return String.format("%,d VND", getTotalPrice());
    }

    // ✅ STOCK MANAGEMENT
    public boolean isInStock() {
        return stock.get() > 0;
    }

    public boolean canSell(int requestedQuantity) {
        return stock.get() >= requestedQuantity;
    }

    public void reduceStock(int soldQuantity) {
        if (canSell(soldQuantity)) {
            setStock(stock.get() - soldQuantity);
        }
    }

    public void addStock(int addedQuantity) {
        setStock(stock.get() + addedQuantity);
    }

    // ✅ QUANTITY MANAGEMENT CHO CART
    public void increaseQuantity() {
        setQuantity(quantity.get() + 1);
    }

    public void decreaseQuantity() {
        if (quantity.get() > 1) {
            setQuantity(quantity.get() - 1);
        }
    }

    public void increaseQuantity(int amount) {
        setQuantity(quantity.get() + amount);
    }

    public void decreaseQuantity(int amount) {
        int newQuantity = quantity.get() - amount;
        setQuantity(Math.max(1, newQuantity)); // Không cho quantity < 1
    }

    // Compatibility methods (để tương thích với code cũ)
    @Deprecated
    public String getType() { return getCategory(); }
    @Deprecated
    public void setType(String type) { setCategory(type); }
    @Deprecated
    public String getImage() { return getImgPath(); }
    @Deprecated
    public void setImage(String image) { setImgPath(image); }
    @Deprecated
    public java.util.Date getDate() { return new java.util.Date(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productId.get() == product.productId.get();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productId.get());
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%d, stock=%d, quantity=%d}", 
                           productId.get(), productName.get(), category.get(), price.get(), stock.get(), quantity.get());
    }
}
