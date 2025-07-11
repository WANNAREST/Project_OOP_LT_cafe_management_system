package obj;


import java.util.ArrayList;
import java.util.List;

public class Store {
    private ArrayList<Product> itemsInStore = new ArrayList<Product>();

    public void addProduct(Product product) {
        if (!itemsInStore.contains(product)) {
            itemsInStore.add(product);
            System.out.println("Added Product: " + product);
        }
    }

    public void removeProduct(Product product) {
        if (itemsInStore.remove(product)) {
            System.out.println("The product with name " + product.getName() + " has been removed from the store");
        } else {
            System.out.println("Cannot find the product with name " + product.getName());
        }
    }

    // AdminPart compatibility method
    public void updateProduct(int id, String name, String category, int price, int stock, String description, String imgPath) {
        for (Product product : itemsInStore) {
            if (product.getId() == id) {
                product.setName(name);
                product.setCategory(category);
                product.setPrice(price);
                product.setQuantity(stock);
                product.setDescription(description);
                product.setImgPath(imgPath);
                System.out.println("Updated Product: " + product);
                return;
            }
        }
        System.out.println("Cannot find product with ID: " + id);
    }

    public void displayStore() {
        System.out.println("**********************STORE***********************\n");
        if (itemsInStore.isEmpty()) {
            System.out.println("There are no products in the store");
        } else {
            for (int i = 0; i < itemsInStore.size(); i++) {
                int j = i + 1;
                System.out.println(j + ". " + itemsInStore.get(i).toString());
            }
        }
    }

    // Getter and Setter
    public ArrayList<Product> getItemsInStore() {

        return itemsInStore;
    }

    public void setItemsInStore(List<Product> itemsInStore) {
        this.itemsInStore = (ArrayList<Product>) itemsInStore;
    }

    public Product findProductByName(String name) {
        for (Product product : itemsInStore) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

}
