package obj;

import java.util.Objects;

public class Products {

	private int id;
	private String name;
	private float price;
	private String description;
	private String category;
    private int stock;
    
    private static int nbProduct = 0;

	public Products(String name, float price, String description,  String category, int stock) {
		
		this.id = ++nbProduct;
		this.name = name;
		this.price = price;
		this.description = description;
		this.category = category;
		this.stock = stock;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
        this.name = name;
    }

	public float getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
        this.price = price;
    }

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
        this.description = description;
    }

	public boolean isAvailable() {

        return stock > 0;
    }

	public String getCategory() {
		return category;
	}
	
	public int getQuantity() {
        return stock;
    }

    public void setQuantity(int quantity) {
        this.stock= quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }
	
	public boolean isMatch(String name) {
	    return this.name.toLowerCase().contains(name.toLowerCase());
	}
	
	public double calculateTotalPrice() {
        return price * stock;
    }
	
	public boolean reduceStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return true;
        }
        return false;
    }

    public void increaseStock(int quantity) {

        stock += quantity;
    }
	
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Products product = (Products) obj;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
