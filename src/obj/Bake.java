package obj;

public class Bake extends Product{
    public static int nbBake = 0;

    public void setNbBake(int nbBake) {
        Bake.nbBake = nbBake
        ;
    }

    public Bake(int id, String name, String category, int price, String description) {
        super(id, name, category, price, description);
    }

    public Bake(String name, String category, int price) {
        super(name, category, price);
    }


    @Override
    public String toString() {
        return "Product{" +
                "name='" + this.getName() + '\'' +
                ", category='" + this.getCategory() + '\'' +



                ", price=" + this.getPrice() +

                ", quantity=" + this.getQuantity() +

                ", description='" +this.getDescription() + '\'' +
                '}';
    }
}
