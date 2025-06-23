package Controller;

import java.sql.Date;

public class productData {
    private Integer id;
    private String productId;
    private String productName;
    private Integer stock;
    private Double price;
    private String note;
    private String image;
    private Date date;
    private String type;
    private Integer quantity;
    
    public productData(Integer id, String productId,
            String productName, String type, Integer stock,
            Double price, String note, String image, Date date) {
       this.id = id;
       this.productId = productId;
       this.productName = productName;
       this.type = type;
       this.stock = stock;
       this.price = price;
       this.note = note;
       this.image = image;
       this.date = date;
    }
    
    public productData(Integer id, String productId, String productName, 
            String type, Integer quantity, String note, Double price, String image, Date date){
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.price = price;
        this.image = image;
        this.note = note;
        this.date = date;
        this.quantity = quantity;
    }
    
    
    public Integer getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
    
    public Integer getStock() {
        return stock;
    }

    public Double getPrice() {
        return price;
    }

    public String getNote() {
        return note;
    }

    public String getImage() {
        return image;
    }

    public Date getDate() {
        return date;
    }

	public String getType() {
		return type;
	}

	public Integer getQuantity() {
		return quantity;
	}



   
}
