package com.dl.smartshouhi.model;

public class UserModel {

    private int id;
    private String email;
    private final String userName;
    private final String fullName;
    private final String dob;
    private final String phone;
    private final boolean isAdmin;
    private boolean isActive;

    public UserModel(int id, String email, String userName, String fullName, String phone, boolean isAdmin, boolean isActive, String dob) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.dob = dob;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
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

    public String getFullName() {
        return fullName;
    }

    public String getDob() {
        return dob;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
