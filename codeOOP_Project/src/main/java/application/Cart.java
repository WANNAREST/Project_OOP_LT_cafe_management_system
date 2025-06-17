package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import application.LimitExceededException;


public class Cart {
    public static final int MAX_NUMBERS_ORDERED = 50;

    private ObservableList<CartItem> itemsOrdered = FXCollections.observableArrayList();

    private FilteredList<CartItem> filteredItemsOrdered = new FilteredList<>(itemsOrdered, p -> true);

    public void addProduct(Product product, int quantity) throws LimitExceededException {
        CartItem item = findCartItemByProduct(product);
        if (item != null){
            item.setQuantity(item.getQuantity() + quantity);
            System.out.println("Updated quantity for " + product.getName() +
                    ". New quantity: " + item.getQuantity());

        }else{
            if (itemsOrdered.size() < MAX_NUMBERS_ORDERED) {
                CartItem newItem = new CartItem(product, quantity);
                itemsOrdered.add(newItem);
                System.out.println("Added item " + product.getName() + " to the cart");
            }else{
                throw new LimitExceededException("You can only order up to 50 items");
            }
        }
    }

    public void removeProduct(Product product) throws LimitExceededException {
        CartItem item = findCartItemByProduct(product);

        if(item != null){
            itemsOrdered.remove(item);
            System.out.println("Removed item " + product.getName() + " from the cart");
        }else{
            System.out.println("Cannot find item " + product.getName() + " in the cart");
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


    public int totalCost() {
        int total = 0;
        for (CartItem item : itemsOrdered) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public CartItem findCartItemByProduct(Product product) {
        for (CartItem item : itemsOrdered) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }


    public void clearCart() {
        itemsOrdered.clear();
        System.out.println("Cart is empty!");
    }

    public ObservableList<CartItem> getItemsOrdered() {
        return itemsOrdered;
    }

    public void increaseQuantity(Product product) {
        CartItem item = findCartItemByProduct(product);
        if (item != null) {
            item.increaseQuantity();
            System.out.println("Increased quantity for " + product.getName() +
                    ". New quantity: " + item.getQuantity());
        } else {
            System.out.println("The cart has no item " + product.getName());
        }
    }

    public void decreaseQuantity(Product product) throws LimitExceededException {
        CartItem item = findCartItemByProduct(product);
        if (item != null) {
            if (item.getQuantity() <= 1) {
                removeProduct(product);
            } else {
                item.decreaseQuantity();
                System.out.println("Decreased quantity for " + product.getName() +
                        ". New quantity: " + item.getQuantity());
            }
        } else {
            System.out.println("The cart has no item " + product.getName());
        }
    }


    public FilteredList<CartItem> getFilteredItemsOrdered() {
        return filteredItemsOrdered;
    }

    public void setFilteredItemsOrdered(FilteredList<CartItem> filteredItemsOrdered) {
        this.filteredItemsOrdered = filteredItemsOrdered;
    }
}
