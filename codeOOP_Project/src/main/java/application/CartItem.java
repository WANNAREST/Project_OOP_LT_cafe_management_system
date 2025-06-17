package application;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
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
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Sửa lại các method này để trả về giá trị thay vì void
    public String getCategory() {
        return product.getCategory();
    }

    public String getDescription() {
        return product.getDescription();
    }

    public int getPrice() {
        return product.getPrice();
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

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }

    public int getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
