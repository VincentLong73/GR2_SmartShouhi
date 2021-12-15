package com.dl.smartshouhi.model;

public class User {
    private int id;
    private String email;
    private String userName;
    private long totalUser;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(long totalUser) {
        this.totalUser = totalUser;
    }
}
