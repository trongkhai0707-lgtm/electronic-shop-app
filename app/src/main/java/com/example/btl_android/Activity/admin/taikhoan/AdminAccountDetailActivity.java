package com.example.btl_android.Activity.admin.taikhoan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAccountDetailActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPhone, editTextAddress, editTextRole;
    private TextView textViewUid;
    private Button btnUpdate, btnBlock;
    private ImageButton btnBack;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String uid;
    private boolean isAccountDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        // Liên kết các view
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextRole = findViewById(R.id.editTextRole);
        textViewUid = findViewById(R.id.TextViewUid);
        btnUpdate = findViewById(R.id.btnSua);
        btnBlock = findViewById(R.id.btnKhoa);
        btnBack = findViewById(R.id.btnBack);

        firebaseAuth = FirebaseAuth.getInstance();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        String role = intent.getStringExtra("role");
        uid = intent.getStringExtra("uid");

        // Hiển thị thông tin
        editTextUsername.setText(username);
        editTextEmail.setText(email);
        editTextPhone.setText(phone);
        editTextAddress.setText(address);
        editTextRole.setText(role);
        textViewUid.setText("UID: " + uid);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("taikhoan").child(uid);

        // Kiểm tra trạng thái tài khoản
        checkAccountStatus();

        // Cập nhật
        btnUpdate.setOnClickListener(v -> updateAccount());

        // Khóa/mở tài khoản
        btnBlock.setOnClickListener(v -> toggleAccountStatus());

        // Quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkAccountStatus() {
        databaseReference.child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isAccountDisabled = "disabled".equals(task.getResult().getValue(String.class));
                updateBlockButton();
            }
        });
    }

    private void toggleAccountStatus() {
        String newStatus = isAccountDisabled ? "enabled" : "disabled";
        databaseReference.child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isAccountDisabled = !isAccountDisabled;
                updateBlockButton();
                Toast.makeText(this, isAccountDisabled ? "Tài khoản đã bị khóa" : "Tài khoản đã mở khóa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thay đổi trạng thái thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBlockButton() {
        btnBlock.setText(isAccountDisabled ? "Mở khóa" : "Khóa");
    }

    private void updateAccount() {
        String updatedUsername = editTextUsername.getText().toString().trim();
        String updatedEmail = editTextEmail.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();
        String updatedAddress = editTextAddress.getText().toString().trim();
        String updatedRole = editTextRole.getText().toString().trim();

        databaseReference.child("username").setValue(updatedUsername);
        databaseReference.child("email").setValue(updatedEmail);
        databaseReference.child("sdt").setValue(updatedPhone);
        databaseReference.child("diachi").setValue(updatedAddress);
        databaseReference.child("role").setValue(updatedRole)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
