package com.finalproject.allabouthomeuser.models;

import android.graphics.Bitmap;

public class Lamp extends Item {
    private double watt;
    private int shade; // 4000, 3000, 6000
    private String type; // שולחן שקוע צמוד תלוי

    public Lamp(String uid,String adminuid, String name, String description, int price, String adminName, int quantity, String image, double watt, int shade) {
        super(uid,adminuid, name, description, price, adminName, quantity, image);
        this.watt = watt;
        this.shade = shade;
    }

    public Lamp() {
    }

    public Lamp(double watt, String name) {
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
}
