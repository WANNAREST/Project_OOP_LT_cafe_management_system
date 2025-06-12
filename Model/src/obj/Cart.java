package obj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javax.naming.LimitExceededException;
import java.util.Collections;


public class Cart {
    public static final int MAX_NUMBERS_ORDERED = 50;

    private ObservableList<Product> itemsOrdered = FXCollections.observableArrayList();

    private FilteredList<Product> filteredItemsOrdered = new FilteredList<>(itemsOrdered, p -> true);

    public void addProduct(Product product) throws LimitExceededException {
        if (itemsOrdered.size() < MAX_NUMBERS_ORDERED) {
            itemsOrdered.add(product);
            System.out.println("Added item " + product.getProductName() + " to the cart");

        } else {
            throw new LimitExceededException("ERROR : The cart has reached the maximum number of items");
        }
    }

    public void removeProduct(Product product) {
        if (itemsOrdered.contains(product)) {
            itemsOrdered.remove(product);
            System.out.println("Removed item " + product.getProductName() + " from the cart");
        } else {
            System.out.println("The cart has no item " + product.getProductName());
        }
    }


    public void printCart() {
        System.out.println("***********************CART***********************\n");
        for (int i = 0; i < itemsOrdered.size(); i++) {
            System.out.println((i + 1) + ". " + itemsOrdered.get(i).toString());
        }
        System.out.println("Total cost : " + totalCost() + "\n");
        System.out.println("***********************CART***********************\n");

    }


    public float totalCost() {
        float total = 0;
        for (Product product : itemsOrdered) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }


    public void emptyCart() {
        itemsOrdered.clear();
        System.out.println("Cart is empty!");
    }

    public ObservableList<Product> getItemsOrdered() {
        return itemsOrdered;
    }


    public FilteredList<Product> getFilteredItemsOrdered() {
        return filteredItemsOrdered;
    }

    public void setFilteredItemsOrdered(FilteredList<Product> filteredItemsOrdered) {
        this.filteredItemsOrdered = filteredItemsOrdered;
    }
}


