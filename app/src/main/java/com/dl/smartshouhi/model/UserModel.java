package com.dl.smartshouhi.model;

public class UserModel {

    private int id;
    private String email;
    private String userName;
    private String fullName;
    private String dob;
    private String phone;
    private String password;
    private boolean isAdmin;
    private boolean isActive;

    public UserModel(int id, String email, String userName, String fullName, String dob, String phone, String password, boolean isAdmin, boolean isActive) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.dob = dob;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    //    public UserModel(int id, String email, String userName, String fullName,String phone, boolean isAdmin) {
//        this.id = id;
//        this.email = email;
//        this.userName = userName;
//        this.fullName = fullName;
//        this.phone = phone;
//        this.isAdmin = isAdmin;
//    }
//
//    public UserModel(String email, String userName, String dob, String password) {
//        this.email = email;
//        this.userName = userName;
//        Dob = dob;
//        this.password = password;
//    }

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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
