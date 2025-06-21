package obj;

public class Coffee extends Product {
    public static int nbCoffee = 0;

    public void setNbCoffee(int nbCoffee) {
        Coffee.nbCoffee = nbCoffee;
    }

    // ✅ SỬA - Constructor tương thích với Product mới
    public Coffee(String name, String type, double price) {
        super(name, type, price); // Gọi constructor Product(String, String, double)
        nbCoffee++;
    }

    // ✅ THÊM - Constructor với product ID
    public Coffee(String productId, String name, String type, double price) {
        super(productId, name, (int) price);
        setCategory(type); // Override category
        nbCoffee++;
    }

    // ✅ THÊM - Constructor đầy đủ
    public Coffee(int productId, String name, String type, int price, int stock) {
        super(productId, name, type, price, stock, "", "Coffee drink");
        nbCoffee++;
    }

    // ✅ GETTER cho nbCoffee
    public static int getNbCoffee() {
        return nbCoffee;
    }

    // ✅ SỬA - toString method
    @Override
    public String toString() {
        return "Coffee{" +
                "id=" + getProductId() +
                ", name='" + getProductName() + '\'' +
                ", category='" + getCategory() + '\'' +
                ", price=" + getPrice() +
                ", stock=" + getStock() +
                ", quantity=" + getQuantity() +
                ", description='" + getNote() + '\'' +
                '}';
    }

    // ✅ THÊM - Specialized methods cho Coffee
    public boolean isCoffeeType() {
        return "Coffee".equalsIgnoreCase(getCategory());
    }

    public String getCoffeeInfo() {
        return String.format("%s - %s (%,d VND)", 
                           getProductName(), getCategory(), getPrice());
    }
}
