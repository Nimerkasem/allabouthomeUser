package com.finalproject.allabouthomeuser.models;



public class Lamp extends Item {
    private double watt;
    private int shade; // 4000, 3000, 6000


   public Lamp(String uid,String adminuid, String name, String description, int price, String adminName, int quantity, String image, double watt, int shade) {
        super(uid,adminuid, name, description, price, adminName, quantity, image);
        this.watt = watt;
        this.shade = shade;
    }

    public Lamp() {
    }


    public Lamp(double watt, String name, double shade) {
        super(name );
        this.watt=watt;
        this.shade= (int) shade;
    }

    public double getWatt() {
        return watt;
    }



    public int getShade() {
        return shade;
    }


}
