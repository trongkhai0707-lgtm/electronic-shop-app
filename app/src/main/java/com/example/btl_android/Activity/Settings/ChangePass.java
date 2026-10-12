package com.example.btl_android.Activity.Settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePass extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;
    private ImageView btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_changepass);

        // Khởi tạo các view
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnback = findViewById(R.id.btn_back);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Xử lý sự kiện click vào nút đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmNewPassword.getText().toString();

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới có khớp nhau không
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ChangePass.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            } else {
                // Thực hiện đổi mật khẩu
                changePassword(currentPassword, newPassword);
            }
        });
    }

    // Hàm đổi mật khẩu
    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Xác thực lại người dùng
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sau khi xác thực lại thành công, đổi mật khẩu
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ChangePass.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            // Có thể điều hướng người dùng sang màn hình khác hoặc thoát
                        } else {
                            // Xử lý lỗi đổi mật khẩu thất bại
                            String errorMessage = updateTask.getException().getMessage();
                            if (errorMessage != null) {
                                // Kiểm tra nếu lỗi liên quan đến yêu cầu của mật khẩu
                                if (errorMessage.contains("Password should be at least")) {
                                    Toast.makeText(ChangePass.this, "Mật khẩu mới không hợp lệ. Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePass.this, "Đổi mật khẩu thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ChangePass.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangePass.this, "Xác thực không thành công, vui lòng kiểm tra lại mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
