package isep.inventory.app.entity;

public class Company {
    int id;
    String name;
    String city;
    String street;
    String postAddress;

    public Company(int id, String name, String city, String street, String postAddress) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.street = street;
        this.postAddress = postAddress;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
