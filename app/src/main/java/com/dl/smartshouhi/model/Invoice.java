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

    public Invoice() {
    }

    public Invoice(String seller, String address, String timestamp, float totalCost) {
        this.seller = seller;
        this.address = address;
        this.timestamp = timestamp;
        this.totalCost = totalCost;
    }

    public Invoice(int id, String seller, String address, String timestamp, float totalCost) {
        this.id = id;
        this.seller = seller;
        this.address = address;
        this.timestamp = timestamp;
        this.totalCost = totalCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("seller", seller);
        return result;
    }
}
