package com.finalproject.allabouthomeuser.models;

import android.graphics.Bitmap;

public class Item {
    private String uid;
    private String name;
    private String description;
    private int price;
    private String adminName;
    private int quantity;
    private String image;
    public Item(String name){
        this.name=name;
    }

    public Item(String uid,String name, String description, int price, String adminName, int quantity, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.adminName = adminName;
        this.quantity = quantity;
        this.image = image;
        this.uid=uid;
    }

    public Item() {
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }



    public String getAdminName() {
        return adminName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public void setQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }
}

