package com.finalproject.allabouthomeuser.models;

import java.util.List;

public class Order {
    private boolean delivered;
    private String storename;
    private List<Item> items;



    public Order(boolean delivered, String storename, List<Item> items) {
        this.delivered = delivered;
        this.storename = storename;
        this.items = items;
    }


    public Order() {

    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setStatus(Boolean delivered) {
    }
}
