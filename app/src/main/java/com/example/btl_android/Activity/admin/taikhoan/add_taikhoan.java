package com.example.btl_android.Activity.admin.taikhoan;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.btl_android.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class add_taikhoan extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPhone, editTextAddress;
    private MaterialButton buttonAddAccount;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_taikhoan);

        // Liên kết các View từ XML
        toolbar = findViewById(R.id.toolbar);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonAddAccount = findViewById(R.id.buttonAddAccount);

        // Khởi tạo Firebase Database Reference và Firebase Auth
        databaseReference = FirebaseDatabase.getInstance().getReference("taikhoan");
        auth = FirebaseAuth.getInstance();

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Xử lý sự kiện khi nhấn nút "Back"
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Xử lý sự kiện khi nhấn nút "Thêm tài khoản"
        buttonAddAccount.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            // Kiểm tra nếu các trường dữ liệu không bị rỗng
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                Toast.makeText(add_taikhoan.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Thêm tài khoản vào Firebase
                addNewAccount(username, email, phone, address);
            }
        });
    }

    private void addNewAccount(String username, String email, String phone, String address) {
        // Đặt mật khẩu mặc định cho user
        String password = "12345678";

        // Tạo tài khoản trong Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Gửi email xác minh
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            String userId = user.getUid();

                                            // Tạo một Map để lưu thông tin tài khoản
                                            Map<String, Object> account = new HashMap<>();
                                            account.put("uid", userId);
                                            account.put("username", username);
                                            account.put("email", email);
                                            account.put("sdt", String.valueOf(phone)); // Lưu số điện thoại dưới dạng chuỗi
                                            account.put("diachi", address);
                                            account.put("role", "user"); // Đặt vai trò mặc định là "user"
                                            account.put("dieukhoan", true); // Mặc định đồng ý điều khoản

                                            // Lưu thông tin tài khoản vào Firebase Database
                                            databaseReference.child(userId).setValue(account)
                                                    .addOnCompleteListener(dbTask -> {
                                                        if (dbTask.isSuccessful()) {
                                                            Toast.makeText(add_taikhoan.this, "Thêm tài khoản thành công. Vui lòng kiểm tra email để xác minh.", Toast.LENGTH_LONG).show();
                                                            finish(); // Quay lại màn hình trước đó sau khi thêm thành công
                                                        } else {
                                                            String errorMessage = dbTask.getException() != null ? dbTask.getException().getMessage() : "Lỗi khi lưu thông tin tài khoản";
                                                            Toast.makeText(add_taikhoan.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(add_taikhoan.this, "Lỗi khi gửi email xác minh: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(add_taikhoan.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi khi tạo tài khoản";
                        Toast.makeText(add_taikhoan.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
