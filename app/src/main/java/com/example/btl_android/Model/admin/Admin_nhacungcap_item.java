package com.example.btl_android.Model.admin;

import java.io.Serializable;
import com.google.firebase.database.PropertyName;

public class Admin_nhacungcap_item implements Serializable {
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String diaChi;
    private String email;
    private String soDienThoai;

    // Constructor mặc định cần thiết cho Firebase
    public Admin_nhacungcap_item() {}

    // Constructor có đối số
    public Admin_nhacungcap_item(String maNhaCungCap, String tenNhaCungCap, String diaChi, String email, String soDienThoai) {
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.diaChi = diaChi;
        this.email = email;
        this.soDienThoai = soDienThoai;
    }

    @PropertyName("ma_nha_cung_cap")
    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    @PropertyName("ma_nha_cung_cap")
    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    @PropertyName("ten_nha_cung_cap")
    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    @PropertyName("ten_nha_cung_cap")
    public void setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
    }

    @PropertyName("dia_chi")
    public String getDiaChi() {
        return diaChi;
    }

    @PropertyName("dia_chi")
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("so_dien_thoai")
    public String getSoDienThoai() {
        return soDienThoai;
    }

    @PropertyName("so_dien_thoai")
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
