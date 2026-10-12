package com.example.btl_android.Model;

import java.util.List;
import java.util.Map;

public class OrderItem {
    private String maDonHang;
    private String ngayDatHang;
    private long tongTien;
    private String trangThai; // Trạng thái đơn hàng
    private List<Map<String, Object>> sanPham; // Danh sách sản phẩm

    // Constructor mặc định cho Firebase
    public OrderItem() {
    }

    public OrderItem(String maDonHang, String ngayDatHang, long tongTien, String trangThai, List<Map<String, Object>> sanPham) {
        this.maDonHang = maDonHang;
        this.ngayDatHang = ngayDatHang;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.sanPham = sanPham;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(String ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public List<Map<String, Object>> getSanPham() {
        return sanPham;
    }

    public void setSanPham(List<Map<String, Object>> sanPham) {
        this.sanPham = sanPham;
    }
}
