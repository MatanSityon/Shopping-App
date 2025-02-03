package com.example.shoppingapp.models;

public class Item {
    private String name;
    private String description;
    private int quantity;
    private int imageResource; // Resource ID for the image
    private double price;

    public Item(String name, String description, int quantity, int imageResource, double price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.imageResource = imageResource;
        this.price = price;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}


