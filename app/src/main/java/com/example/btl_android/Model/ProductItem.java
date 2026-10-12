package com.example.btl_android.Model;

public class ProductItem {
    private String tenSanPham;
    private Long gia; // Giá sản phẩm
    private String hinhAnh; // URL hình ảnh
    private String moTa; // Mô tả sản phẩm
    private int soLuongTonKho; // Số lượng tồn kho
    private String loai; // Loại sản phẩm

    public ProductItem() {
        // Constructor mặc định
    }

    // Getters và setters cho tất cả thuộc tính
    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Long getGia() {
        return gia;
    }

    public void setGia(Long gia) {
        this.gia = gia;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSoLuongTonKho() {
        return soLuongTonKho;
    }

    public void setSoLuongTonKho(int soLuongTonKho) {
        this.soLuongTonKho = soLuongTonKho;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }
}
