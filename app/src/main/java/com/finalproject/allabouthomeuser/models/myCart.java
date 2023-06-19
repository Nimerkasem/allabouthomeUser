package com.finalproject.allabouthomeuser.models;



public class myCart extends Lamp {
    public myCart(String name, String description, int price, String adminName, int quantity, String image, double watt, int shade, String type) {
        super(name, description, price, adminName, quantity, image, watt, shade, type);
    }

    public myCart() {
        super();
    }

    public myCart(double watt, String name) {
        super(watt, name);
    }


//    public long getQuantityAsLong() {
//        return Long.parseLong(quantity);
//    }
}
