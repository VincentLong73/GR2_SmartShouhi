package com.dl.smartshouhi.model;

public class InvoiceTest {

    private String seller;
    private String address;
    private String timestamp;
    private String total_cost;

    public InvoiceTest(String seller, String address, String timestamp, String total_cost) {
        this.seller = seller;
        this.address = address;
        this.timestamp = timestamp;
        this.total_cost = total_cost;
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

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }
}
