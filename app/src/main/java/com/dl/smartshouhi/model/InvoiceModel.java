package com.dl.smartshouhi.model;

public class InvoiceModel {

    private int id;
    private final String seller;
    private final String address;
    private final String timestamp;
    private final String total_cost;

    public InvoiceModel(String seller, String address, String timestamp, String total_cost) {
        this.seller = seller;
        this.address = address;
        this.timestamp = timestamp;
        this.total_cost = total_cost;
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

    public String getAddress() {
        return address;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTotal_cost() {
        return total_cost;
    }
}
