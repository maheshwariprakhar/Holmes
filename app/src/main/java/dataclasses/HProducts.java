package dataclasses;

public class HProducts {
    public String owner;
    public String title;
    public String price;
    public String description;
    public String userId;


    public String getOwner() {return owner;}

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPrice(String pPrice) {
        this.price = pPrice;
    }

    public void setTitle(String pTitle) {
        this.title = pTitle;
    }

    public void setDescription(String pDescription) {
        this.description = pDescription;
    }

    public void setUserId(String id) {
        this.userId =id;
    }

    public String getUserId() {
        return userId;
    }

    public HProducts() {

    }

    public HProducts(String owner, String title, String price, String description, String id) {
        this.owner = owner;
        this.title = title;
        this.price = price;
        this.description = description;
        this.userId = id;
    }
}
