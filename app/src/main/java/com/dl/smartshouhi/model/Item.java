package com.dl.smartshouhi.model;

public class Item {

    private int id;
    private String itemName;
    private float itemCost;
    private int invoiceId;

    public Item(String itemName, float itemCost) {
        this.itemName = itemName;
        this.itemCost = itemCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
