package obj;

public class CartItem {
    private Products product;
    private int quantity;
    
    private String name;
    private double price;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CartItem(Products product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getId() {
        return product.getId();
    }

    public String getName() {
        return product.getName();
    }

    // Getter cho cả Product (nếu cần)
    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    // Sửa lại các method này để trả về giá trị thay vì void
    public String getCategory() {
        return product.getCategory();
    }

    public String getDescription() {
        return product.getDescription();
    }

    public float getPrice() {
        return product.getPrice(); // Chuyển từ float sang double
    }

    public int getStock() {
        return product.getQuantity();
    }

    // Getter/Setter cho quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
        }
    }

    public float getTotalPrice() {
        return product.getPrice() * quantity;
    }
}