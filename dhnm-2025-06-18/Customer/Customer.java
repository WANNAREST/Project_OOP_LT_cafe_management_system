package Customer;

import java.util.ArrayList;
import Cart_CoffeeShop.Order_CoffeeShop;

public class Customer {
    private String name;
    private String phoneNumber;
    private String address;
    private ArrayList<Order_CoffeeShop> orderHistory;

    public Customer(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.orderHistory = new ArrayList<>();
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Order_CoffeeShop> getOrderHistory() {
        return orderHistory;
    }

    // Method to add order to history
    public void addOrder(Order_CoffeeShop order) {
        orderHistory.add(order);
    }

}