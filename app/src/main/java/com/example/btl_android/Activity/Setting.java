package com.example.btl_android.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Settings.CongDong;
import com.example.btl_android.Activity.Settings.DieuKhoan;
import com.example.btl_android.Activity.Settings.GioiThieu;
import com.example.btl_android.Activity.Settings.HoTro;
import com.example.btl_android.Activity.Settings.NgonNgu;
import com.example.btl_android.Activity.Settings.TKvaBM;
import com.example.btl_android.Activity.Settings.ThongBao;
import com.example.btl_android.R;
import com.google.firebase.auth.FirebaseAuth;

public class Setting extends AppCompatActivity {

    ImageView btnBack;
    Button btnThongBao, btnTaiKhoanBaoMat, btnNgonNgu, btnHoTro,
            btnCongDong, btnDieuKhoan, btnGioiThieu, btnDangXuat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Ánh xạ các thành phần giao diện
        Mapping();

        // Đăng ký sự kiện nhấn
        LoadActivity();


        btnDangXuat.setOnClickListener(v -> {
            // Xóa thông tin uid và thông tin đăng nhập khỏi SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("uid"); // Xóa uid
            editor.remove("email"); // Xóa email
            editor.remove("password"); // Xóa mật khẩu
            editor.remove("remember"); // Xóa trạng thái "Remember Me"
            editor.apply(); // Áp dụng thay đổi

            // Đăng xuất FirebaseAuth nếu sử dụng
            FirebaseAuth.getInstance().signOut(); // Đăng xuất FirebaseAuth

            // Tạo intent chuyển về màn hình Login và xóa tất cả các Activity khác
            Intent intent = new Intent(this, Login.class);
            intent.putExtra("REMEMBER", "false"); // Đặt flag "REMEMBER" là false
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa hết các Activity trước đó
            startActivity(intent);
            finish(); // Đóng màn hình hiện tại
        });
    }

    // Ánh xạ các thành phần giao diện
    private void Mapping() {
        btnBack =  findViewById(R.id.ivBack);
        btnThongBao = findViewById(R.id.btnThongBao);
        btnTaiKhoanBaoMat = findViewById(R.id.btnTkBm);
        btnNgonNgu = findViewById(R.id.btnNgonNgu);
        btnHoTro = findViewById(R.id.btnHoTro);
        btnCongDong = findViewById(R.id.btnCongDong);
        btnDieuKhoan = findViewById(R.id.btnDieuKhoan);
        btnGioiThieu = findViewById(R.id.btnGioiThieu);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }

    private void LoadActivity() {
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });
        btnThongBao.setOnClickListener(view -> {
            Intent intent = new Intent(this, ThongBao.class);
            startActivity(intent);
        });
        btnTaiKhoanBaoMat.setOnClickListener(view -> {
            Intent intent = new Intent(this, TKvaBM.class);
            startActivity(intent);
        });
        btnNgonNgu.setOnClickListener(view -> {
            Intent intent = new Intent(this, NgonNgu.class);
            startActivity(intent);
        });
        btnHoTro.setOnClickListener(view -> {
            Intent intent = new Intent(this, HoTro.class);
            startActivity(intent);
        });
        btnCongDong.setOnClickListener(view -> {
            Intent intent = new Intent(this, CongDong.class);
            startActivity(intent);
        });
        btnDieuKhoan.setOnClickListener(view -> {
            Intent intent = new Intent(this, DieuKhoan.class);
            intent.putExtra("ACTIVITY", "Setting");
            startActivity(intent);
        });
        btnGioiThieu.setOnClickListener(view -> {
            Intent intent = new Intent(this, GioiThieu.class);
            startActivity(intent);
        });
    }
}