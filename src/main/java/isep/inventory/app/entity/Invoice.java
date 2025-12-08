package isep.inventory.app.entity;

import java.util.Date;
import java.util.List;

public class Invoice {
    int id;
    List<Product> products;
    Date date;
    String sourceCompany;
    String destinationCompany;

    public Invoice(){}

    public Invoice(int id, List<Product> products, Date date, String sourceCompany, String destinationCompany) {
        this.id = id;
        this.products = products;
        this.date = date;
        this.sourceCompany = sourceCompany;
        this.destinationCompany = destinationCompany;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSourceCompany() {
        return sourceCompany;
    }

    public void setSourceCompany(String sourceCompany) {
        this.sourceCompany = sourceCompany;
    }

    public String getDestinationCompany() {
        return destinationCompany;
    }

    public void setDestinationCompany(String destinationCompany) {
        this.destinationCompany = destinationCompany;
    }
}
