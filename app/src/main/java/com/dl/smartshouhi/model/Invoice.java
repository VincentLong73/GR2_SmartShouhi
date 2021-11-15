package com.dl.smartshouhi.model;

import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private int id;
    private String seller;
    private String address;
    private String timestamp;
    private String totalCost;

    public Invoice() {
    }

    public Invoice(String seller, String address, String timestamp, String totalCost) {
        this.seller = seller;
        this.address = address;
        this.timestamp = timestamp;
        this.totalCost = totalCost;
    }

    public Invoice(int id, String seller, String address, String timestamp, String totalCost) {
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

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("seller", seller);
        return result;
    }
}
