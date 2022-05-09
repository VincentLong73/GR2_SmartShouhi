package com.dl.smartshouhi.model;

public class UserModel {

    private int id;
    private String email;
    private String userName;
    private String fullName;
    private boolean isAdmin;

    public UserModel(int id, String email, String userName, String fullName, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
