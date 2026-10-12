package com.example.btl_android.Activity.Settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Login;
import com.example.btl_android.Activity.Setting;
import com.example.btl_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TKvaBM extends AppCompatActivity {

    // Khai báo các view
    TextView viewUser, viewEmail, viewPhone;
    Button btnChange, btnProfile, btnDelAcc;
    ImageView btnBack;

    // Khai báo database của Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("taikhoan");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Kích hoạt EdgeToEdge
        setContentView(R.layout.setting_tkvabm); // Set layout cho activity

        Mapping(); // Liên kết các thành phần giao diện với mã

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút đổi mật khẩu
        btnChange.setOnClickListener(v -> {
            Intent intent = new Intent(TKvaBM.this, ChangePass.class);
            startActivity(intent);
        });

        // Nút xem hồ sơ
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(TKvaBM.this, myProfile.class);
            startActivity(intent);
        });

        // Nút xóa tài khoản
        btnDelAcc.setOnClickListener(v -> showConfirm());

        // Hiển thị thông tin người dùng
        showInfo();
    }

    // Phương thức để liên kết các thành phần giao diện với mã (Mapping)
    private void Mapping() {
        viewUser = findViewById(R.id.tv_user);
        viewEmail = findViewById(R.id.tv_email);
        viewPhone = findViewById(R.id.tv_numPhone);
        btnProfile = findViewById(R.id.btnProfile);
        btnChange = findViewById(R.id.btnChange);
        btnDelAcc = findViewById(R.id.btnDelAccount);
        btnBack = findViewById(R.id.ivBack);
    }

    // Phương thức hiển thị thông tin người dùng từ Firebase
    private void showInfo() {
        // Lấy uid từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null); // null nếu không tìm thấy

        if (uid != null) {
            // Kết nối Firebase và lấy thông tin người dùng theo uid
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("taikhoan").child(uid);

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Lấy dữ liệu từ Firebase
                    String user = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String sdt = snapshot.child("sdt").getValue(String.class);

                    // Kiểm tra và hiển thị thông tin
                    if (user != null && email != null && sdt != null) {
                        viewUser.setHint(user);
                        viewEmail.setHint(email);
                        viewPhone.setHint(sdt);
                    } else {
                        Toast.makeText(TKvaBM.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi khi không truy vấn được dữ liệu
                    Toast.makeText(TKvaBM.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Trường hợp không tìm thấy uid trong SharedPreferences
            Toast.makeText(TKvaBM.this, "Không tìm thấy UID người dùng, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị dialog xác nhận xóa tài khoản
    public void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Thiết lập tiêu đề và nội dung
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn chắc chắn muốn xóa tài khoản không?");

        // Nút "Xóa"
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            DeleteAccount();
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        // Nút "Không"
        builder.setNegativeButton("Không", (dialog, which) -> {
            dialog.dismiss();
        });

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Phương thức xóa tài khoản người dùng
    private void DeleteAccount() {
        // Lấy uid từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        // Lấy instance của Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Kiểm tra nếu người dùng hiện tại tồn tại và uid trùng khớp
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getUid().equals(uid)) {
            // Xóa tài khoản của người dùng
            auth.getCurrentUser().delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Thông báo tài khoản đã được xóa thành công
                            Toast.makeText(TKvaBM.this, "Tài khoản đã được xóa thành công", Toast.LENGTH_SHORT).show();

                            // Xóa tài khoản khỏi Firebase Database
                            dbRef.child(uid).removeValue();

                            // Xóa Uid khỏi SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();  // Xóa tất cả dữ liệu
                            editor.apply();

                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(TKvaBM.this, Login.class);
                            startActivity(intent);
                        } else {
                            // Xử lý lỗi nếu không thể xóa tài khoản
                            Toast.makeText(TKvaBM.this, "Không thể xóa tài khoản: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Nếu không tìm thấy người dùng hiện tại hoặc uid không trùng khớp
            Toast.makeText(TKvaBM.this, "Không tìm thấy người dùng hiện tại", Toast.LENGTH_SHORT).show();
        }
    }
}