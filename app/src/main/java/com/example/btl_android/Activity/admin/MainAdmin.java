package com.example.btl_android.Activity.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_android.Activity.Login;
import com.example.btl_android.Activity.admin.ThongKe.ThongKe;
import com.example.btl_android.Activity.admin.donhang.admin_donhang;
import com.example.btl_android.Activity.admin.nhacungcap.admin_nhacungcap;
import com.example.btl_android.Activity.admin.sanpham.admin_sanpham;
import com.example.btl_android.Activity.admin.taikhoan.admin_taikhoan;
import com.example.btl_android.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainAdmin extends AppCompatActivity {

    Button btnDangXuat, btnTaiKhoan, btnNhaCungCap, btnSanPham, btnThongKe, btnDonHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các thành phần giao diện
        Mapping();

        // sự kiện đăng xuất
        btnDangXuat.setOnClickListener(v -> {
            dangxuat();
        });
        // sự kiện tài khoản
        btnTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, admin_taikhoan.class);
            startActivity(intent);

        });
        // sự kiện tài khoản
        btnSanPham.setOnClickListener(v -> {
            Intent intent = new Intent(this, admin_sanpham.class);
            startActivity(intent);

        });

        btnThongKe.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThongKe.class);
            startActivity(intent);
        });


        btnNhaCungCap.setOnClickListener(v -> {
            Intent intent = new Intent(this, admin_nhacungcap.class);
            startActivity(intent);
        });

        btnDonHang.setOnClickListener(v -> {
           Intent intent = new Intent(this, admin_donhang.class);
           startActivity(intent);

        });

    }

    // Ánh xạ các thành phần giao diện
    private void Mapping() {

        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnTaiKhoan = findViewById(R.id.btnQuanLyTaiKhoan);
        btnNhaCungCap= findViewById(R.id.btnQuanLyNhaCungCap);
        btnSanPham = findViewById(R.id.btnQuanLySanPham);
        btnThongKe= findViewById(R.id.btnThongKeBaoCao);
        btnDonHang = findViewById(R.id.btnQuanLyDonHang);

    }

    private void dangxuat(){
        // Xóa thông tin uid đã lưu trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("uid"); // Xóa uid
        editor.apply(); // Áp dụng thay đổi

        // Đăng xuất FirebaseAuth nếu sử dụng
        FirebaseAuth.getInstance().signOut(); // Đăng xuất FirebaseAuth

        // Tạo intent chuyển về màn hình Login và xóa tất cả các Activity khác
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa hết các Activity trước đó
        startActivity(intent);
        finish(); // Đóng màn hình hiện tại
    }
}