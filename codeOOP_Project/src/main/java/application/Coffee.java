package application;

public class Coffee extends Product {
    public static int nbCoffee = 0;
    public boolean isHot = false;

    public void setNbCoffee(int nbCoffee) {
        Coffee.nbCoffee = nbCoffee;
    }

    public Coffee(int id, String name, String category, int price, String description) {
        super(id, name, category, price, description);
    }

    public Coffee(String name, String category, int price, boolean isHot) {
        super(name,category,price);
        this.isHot = isHot;
    }

    @Override
    public String toString() {
        return "Coffee{" +
                "name='" + this.getName() + '\'' +
                ", category='" + this.getCategory() + '\'' +
                ", price=" + this.getPrice() +
                ", quantity=" + this.getQuantity() +

                ", description='" +this.getDescription() + '\'' +
                '}';
    }
}
