package com.dl.smartshouhi.model;

import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private int id;
    private String seller;
    private String address;
    private String timestamp;
    private float totalCost;
    private int dayOfWeek;
    private int month;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeller() {
        return seller;
    }

    public String getAddress() {
        return address;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public float getTotalCost() {
        return totalCost;
    }


    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("seller", seller);
        return result;
    }
}
