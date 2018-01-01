package fr.zankia.stock.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Product> products;

    public Category(String name) {
        this(name, new ArrayList<Product>());
    }

    public Category(String name, ArrayList<Product> products) {
        this.name = name;
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct(String name) {
        for(Product p : products) {
            if(p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void addProduct(String name) {
        products.add(new Product(name));
    }

    public void removeProduct(String name) {
        products.remove(getProduct(name));
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
