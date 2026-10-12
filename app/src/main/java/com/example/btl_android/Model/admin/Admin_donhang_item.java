// admin_donhang_item.java
package com.example.btl_android.Model.admin;

// Import thư viện cần thiết
import java.io.Serializable;
import java.util.List;

public class Admin_donhang_item implements Serializable {
    private String maDonHang;
    private String tenKhachHang;
    private String diaChi;
    private String soDienThoai;
    private String ngayDatHang;
    private String trangThai;
    private long tongTien;
    private List<SanPham> sanPham;  // Thêm danh sách sản phẩm

    // Constructor mặc định cho Firebase
    public Admin_donhang_item() {}

    // Constructor đầy đủ có danh sách sản phẩm
    public Admin_donhang_item(String maDonHang, String tenKhachHang, String diaChi, String soDienThoai, String ngayDatHang, String trangThai, long tongTien, List<SanPham> sanPham) {
        this.maDonHang = maDonHang;
        this.tenKhachHang = tenKhachHang;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.ngayDatHang = ngayDatHang;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
        this.sanPham = sanPham;  // Gán giá trị cho biến sanPham
    }

    // Getter và Setter
    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(String ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    // Getter và Setter cho danh sách sản phẩm
    public List<SanPham> getSanPham() {
        return sanPham;
    }

    public void setSanPham(List<SanPham> sanPham) {
        this.sanPham = sanPham;
    }

    // Lớp con để chứa thông tin sản phẩm trong đơn hàng
    public static class SanPham implements Serializable {
        private String tenSanPham;
        private int gia;
        private int soLuong;

        // Constructor mặc định
        public SanPham() {}

        // Constructor đầy đủ
        public SanPham(String tenSanPham, int gia, int soLuong) {
            this.tenSanPham = tenSanPham;
            this.gia = gia;
            this.soLuong = soLuong;
        }

        // Getter và Setter
        public String getTenSanPham() {
            return tenSanPham;
        }

        public void setTenSanPham(String tenSanPham) {
            this.tenSanPham = tenSanPham;
        }

        public int getGia() {
            return gia;
        }

        public void setGia(int gia) {
            this.gia = gia;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(int soLuong) {
            this.soLuong = soLuong;
        }
    }
}
