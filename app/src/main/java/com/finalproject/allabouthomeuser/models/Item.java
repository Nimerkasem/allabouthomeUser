package com.finalproject.allabouthomeuser.models;

import java.util.ArrayList;
public class Item {
    private String uid;
    private String name;
    private String description;
    private static int price;
    private String adminName;
    private int quantity;
    private String image;
    private static String adminuid;
    private static ArrayList<String> categories;

    public Item(String name, ArrayList<String> categories){
        this.name = name;
        this.categories = categories;
    }

    public Item(String uid, String adminuid, String name, String description, int price, String adminName, int quantity, String image) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.adminName = adminName;
        this.quantity = quantity;
        this.image = image;
        this.adminuid = adminuid;
    }



    public Item(ArrayList<String> categories, String uid, String adminuid, String name, String description, int price, String adminName, int quantity, String image) {
        this.categories = categories;
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.adminName = adminName;
        this.quantity = quantity;
        this.image = image;
        this.adminuid = adminuid;
    }

    public Item() {
    }



    public static ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
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

    public static int getPrice() {
        return price;
    }

    public static String getAdminuid() {
        return adminuid;
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
