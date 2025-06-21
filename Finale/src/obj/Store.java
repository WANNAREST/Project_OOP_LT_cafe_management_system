package obj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Store {
    private ObservableList<Product> itemsInStore;

    public Store() {
        this.itemsInStore = FXCollections.observableArrayList();
        loadProductsFromDatabase();
    }

    // ✅ THÊM - Load sản phẩm từ database
    private void loadProductsFromDatabase() {
        try (java.sql.Connection conn = database.connectDB();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("price"),
                    rs.getInt("stock"),
                    rs.getString("img_path"), // ✅ SỬA từ "image_path"
                    rs.getString("note")
                );
                itemsInStore.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading products from database: " + e.getMessage());
        }
    }

    // ✅ THÊM SẢN PHẨM
    public void addProduct(Product product) {
        if (product != null && !itemsInStore.contains(product)) {
            itemsInStore.add(product);
            System.out.println("✅ Added product: " + product.getProductName());
        }
    }

    // ✅ XÓA SẢN PHẨM
    public boolean removeProduct(int productId) {
        return itemsInStore.removeIf(product -> product.getProductId() == productId);
    }

    // ✅ CẬP NHẬT SẢN PHẨM
    public void updateProduct(int productId, String name, String category, int price, int stock, String imagePath, String note) {
        for (Product product : itemsInStore) {
            if (product.getProductId() == productId) {
                product.setProductName(name);
                product.setCategory(category);
                product.setPrice(price);
                product.setStock(stock);
                product.setImgPath(imagePath);
                product.setNote(note);
                break;
            }
        }
    }

    // ✅ TÌM SẢN PHẨM THEO ID
    public Product findProductById(int productId) {
        return itemsInStore.stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst()
                .orElse(null);
    }

    // ✅ TÌM SẢN PHẨM THEO TÊN
    public ObservableList<Product> searchProductsByName(String name) {
        ObservableList<Product> results = FXCollections.observableArrayList();
        for (Product product : itemsInStore) {
            if (product.getProductName().toLowerCase().contains(name.toLowerCase())) {
                results.add(product);
            }
        }
        return results;
    }

    // ✅ LẤY SẢN PHẨM THEO CATEGORY
    public ObservableList<Product> getProductsByCategory(String category) {
        ObservableList<Product> results = FXCollections.observableArrayList();
        for (Product product : itemsInStore) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                results.add(product);
            }
        }
        return results;
    }

    // Getter
    public ObservableList<Product> getItemsInStore() {
        return itemsInStore;
    }

    // ✅ THỐNG KÊ
    public int getTotalProducts() {
        return itemsInStore.size();
    }

    public int getTotalStock() {
        return itemsInStore.stream().mapToInt(Product::getStock).sum();
    }

    public long getTotalInventoryValue() {
        return itemsInStore.stream()
                .mapToLong(product -> (long) product.getPrice() * product.getStock())
                .sum();
    }
}
