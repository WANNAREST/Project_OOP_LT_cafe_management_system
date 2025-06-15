package obj;

public class Coffee extends Product{
    public static int nbCoffee = 0;

    public void setNbCoffee(int nbCoffee) {
        Coffee.nbCoffee = nbCoffee;
    }

//    public Coffee(String name, String category, double price, String description) {
//        super(name, category, price, description);
//    }

    public Coffee(String name, String type, double price) {
        super(name,type,price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + this.getProductName() + '\'' +
                ", category='" + this.getType() + '\'' +
                ", price=" + this.getPrice() +
                ", quantity=" + this.getQuantity() +

                ", description='" +this.getType() + '\'' +
                '}';
    }
}
