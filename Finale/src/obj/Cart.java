package obj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javax.naming.LimitExceededException;

public class Cart {
    public static final int MAX_NUMBERS_ORDERED = 50;

    private ObservableList<Product> itemsOrdered = FXCollections.observableArrayList();
    private FilteredList<Product> filteredItemsOrdered = new FilteredList<>(itemsOrdered, p -> true);

    // ✅ SỬA - addProduct method
    public void addProduct(Product product) throws LimitExceededException {
        if (itemsOrdered.size() < MAX_NUMBERS_ORDERED) {

            // Kiểm tra xem sản phẩm đã có trong cart chưa
            Product existingProduct = findProductInCart(product.getProductId());

            if (existingProduct != null) {
                // Nếu đã có, tăng quantity
                existingProduct.increaseQuantity();
                System.out.println("Updated quantity of " + product.getProductName() + " in cart. New quantity: " + existingProduct.getQuantity());
            } else {
                // Nếu chưa có, thêm mới
                Product cartProduct = createCartProduct(product);
                itemsOrdered.add(cartProduct);
                System.out.println("Added item " + product.getProductName() + " to the cart");
            }

        } else {
            throw new LimitExceededException("ERROR: The cart has reached the maximum number of items");
        }
    }

    // ✅ THÊM - Tìm sản phẩm trong cart
    private Product findProductInCart(int productId) {
        return itemsOrdered.stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst()
                .orElse(null);
    }

    // ✅ THÊM - Tạo copy sản phẩm cho cart
    private Product createCartProduct(Product original) {
        Product cartProduct = new Product(
                original.getProductId(),
                original.getProductName(),
                original.getCategory(),
                original.getPrice(),
                original.getStock(),
                original.getImgPath(),
                original.getNote()
        );
        cartProduct.setQuantity(1); // Default quantity = 1
        return cartProduct;
    }

    // ✅ SỬA - removeProduct method
    public void removeProduct(Product product) {
        if (itemsOrdered.contains(product)) {
            itemsOrdered.remove(product);
            System.out.println("Removed item " + product.getProductName() + " from the cart");
        } else {
            System.out.println("The cart has no item " + product.getProductName());
        }
    }

    // ✅ THÊM - Xóa sản phẩm theo ID
    public void removeProductById(int productId) {
        Product productToRemove = findProductInCart(productId);
        if (productToRemove != null) {
            removeProduct(productToRemove);
        }
    }

    // ✅ THÊM - Tăng/giảm quantity của sản phẩm trong cart
    public void increaseProductQuantity(int productId) {
        Product product = findProductInCart(productId);
        if (product != null) {
            product.increaseQuantity();
            System.out.println("Increased quantity of " + product.getProductName() + ". New quantity: " + product.getQuantity());
        }
    }

    public void decreaseProductQuantity(int productId) {
        Product product = findProductInCart(productId);
        if (product != null) {
            if (product.getQuantity() > 1) {
                product.decreaseQuantity();
                System.out.println("Decreased quantity of " + product.getProductName() + ". New quantity: " + product.getQuantity());
            } else {
                removeProduct(product);
                System.out.println("Removed " + product.getProductName() + " from cart (quantity was 1)");
            }
        }
    }

    // ✅ SỬA - printCart method
    public void printCart() {
        System.out.println("***********************CART***********************\n");
        for (int i = 0; i < itemsOrdered.size(); i++) {
            Product product = itemsOrdered.get(i);
            System.out.println((i + 1) + ". " + product.getProductName() +
                    " - " + product.getCategory() +
                    " - Price: " + product.getFormattedPrice() +
                    " - Quantity: " + product.getQuantity() +
                    " - Total: " + product.getFormattedTotalPrice());
        }
        System.out.println("Total cost: " + getFormattedTotalCost() + "\n");
        System.out.println("***********************CART***********************\n");
    }

    // ✅ SỬA - totalCost method (return long thay vì float)
    public long totalCost() {
        long total = 0;
        for (Product product : itemsOrdered) {
            total += product.getTotalPrice();
        }
        return total;
    }

    // ✅ THÊM - Formatted total cost
    public String getFormattedTotalCost() {
        return String.format("%,d VND", totalCost());
    }

    // ✅ SỬA - emptyCart method
    public void emptyCart() {
        itemsOrdered.clear();
        System.out.println("Cart is empty!");
    }

    // ✅ THÊM - Các method tiện ích
    public int getTotalItems() {
        return itemsOrdered.stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return itemsOrdered.isEmpty();
    }

    public boolean hasProduct(int productId) {
        return findProductInCart(productId) != null;
    }

    // Getters và Setters
    public ObservableList<Product> getItemsOrdered() {
        return itemsOrdered;
    }

    public FilteredList<Product> getFilteredItemsOrdered() {
        return filteredItemsOrdered;
    }

    public void setFilteredItemsOrdered(FilteredList<Product> filteredItemsOrdered) {
        this.filteredItemsOrdered = filteredItemsOrdered;
    }

    // ✅ THÊM - toString cho debug
    @Override
    public String toString() {
        return String.format("Cart{items=%d, totalCost=%s}",
                itemsOrdered.size(), getFormattedTotalCost());
    }
}


