package com.example.btl_android.Model.admin;

public class Admin_taikhoan_item {
    private String username;
    private String email;
    private String role;
    private String sdt; // Add phone number
    private String diachi; // Add address
    private String uid; // Add uid

    public Admin_taikhoan_item() {
        // Firebase requires an empty constructor.
    }

    public Admin_taikhoan_item(String username, String email, String role, String sdt, String diachi, String uid) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.sdt = sdt;
        this.diachi = diachi;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
