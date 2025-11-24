package isep.inventory.app.entity;

public class Product {
    private int id;
    private String name;
    private String description;
    private int stock;
    private boolean available;
    private double price;
    private String category;
    private String sourceCompany;

    public Product(){}

    public Product(int id,
                   String name,
                   String description,
                   int stock,
                   boolean available,
                   double price,
                   String category,
                   String sourceCompany){
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.available = available;
        this.price = price;
        this.category = category;
        this.sourceCompany = sourceCompany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceCompany() {
        return sourceCompany;
    }

    public void setSourceCompany(String sourceCompany) {
        this.sourceCompany = sourceCompany;
    }
}
