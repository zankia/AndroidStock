package fr.zankia.stock.model;

public class Product {
    private String name;
    private int quantity;
    private float price;

    public Product() {
        this("");
    }

    public Product(String name) {
        this(name, 0);
    }

    public Product(String name, int quantity) {
        this(name, quantity, 0);
    }

    public Product(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public void setPrice(float price) {
        this.price = price;
    }
}
