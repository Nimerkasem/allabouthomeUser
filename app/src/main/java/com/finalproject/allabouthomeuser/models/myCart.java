package com.finalproject.allabouthomeuser.models;



public class myCart {
    private String name;
    private String description;
    private String price;
    private String adminName;
    private String quantity;
    private String image;
    private double watt;
    private int shade; //4000,3000,6000
    private String type;//   שולחן שקוע צמוד תלוי

    public myCart(){}

    public myCart(String name, String description, String price, String adminName, String quantity, String image, double watt, int shade, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.adminName = adminName;
        this.quantity = quantity;
        this.image = image;
        this.watt = watt;
        this.shade = shade;
        this.type = type;
    }

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = String.valueOf(quantity != null ? quantity.intValue() : 0);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getWatt() {
        return watt;
    }

    public void setWatt(double watt) {
        this.watt = watt;
    }

    public int getShade() {
        return shade;
    }

    public void setShade(int shade) {
        this.shade = shade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public long getQuantityAsLong() {
        return Long.parseLong(quantity);
    }
}
