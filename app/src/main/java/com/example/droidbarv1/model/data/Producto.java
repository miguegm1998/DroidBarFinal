package com.example.droidbarv1.model.data;

public class Producto {

    private long id;
    private String name;
    private double price;
    private int target;

    public Producto() {
        this(0, "", 0.0,0);
    }

    public Producto(long id, String name, double price, int target) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.target = target;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", target=" + target +
                '}';
    }
}
