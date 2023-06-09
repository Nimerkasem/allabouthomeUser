package com.finalproject.allabouthomeuser.models;

import android.graphics.Bitmap;

public class Item {
    private String name;
    private String description;
    private String price;
    private String adminName;
    private String quantity;
    private Bitmap image;
    public Item(String name){
        this.name=name;
    }

    public Item(String name, String description, String price, String adminName, String quantity, Bitmap image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.adminName = adminName;
        this.quantity = quantity;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }



    public String getAdminName() {
        return adminName;
    }

    public String getQuantity() {
        return quantity;
    }

    public Bitmap getImage() {
        return image;
    }
}

