package com.finalproject.allabouthomeuser.models;



public class myCart extends Lamp {
    public myCart(String uid,String adminuid,String name, String description, int price, String adminName, int quantity, String image, double watt, int shade, String type) {
        super(uid ,adminuid,name, description, price, adminName, quantity, image, watt, shade);
    }

    public myCart() {
        super();
    }




//    public long getQuantityAsLong() {
//        return Long.parseLong(quantity);
//    }
}
