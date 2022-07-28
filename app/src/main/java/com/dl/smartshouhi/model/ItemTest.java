package com.dl.smartshouhi.model;

public class ItemTest {

    private String item_name;
    private float cost_item;

    public ItemTest(String item_name, float cost_item) {
        this.item_name = item_name;
        this.cost_item = cost_item;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public float getCost_item() {
        return cost_item;
    }

    public void setCost_item(float cost_item) {
        this.cost_item = cost_item;
    }
}
