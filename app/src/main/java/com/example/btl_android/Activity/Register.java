package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Settings.DieuKhoan;
import com.example.btl_android.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    EditText userEditText, emailEditText, passEditText, cPassEditText;
    TextInputLayout userLayout, emailLayout, passLayout, cPassLayout;
    Button btn_Login, btn_Register;

    FirebaseAuth auth; // Đối tượng FirebaseAuth để xử lý đăng ký người dùng

    FirebaseDatabase database = FirebaseDatabase.getInstance(); // Khởi tạo FirebaseDatabase
    DatabaseReference dbRef = database.getReference("taikhoan"); // Tham chiếu đến "taikhoan" trong Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Gán layout cho Activity

        auth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        Mapping(); // Gán các thành phần giao diện từ layout

        // Hiển thị mật khẩu cho cả Pass và CPass
        showPass();

        btn_Login.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class); // Tạo intent để chuyển đến màn hình đăng nhập
            startActivity(intent); // Khởi chạy màn hình đăng nhập
        });

        // Đăng ký sự kiện bấm nút đăng ký
        btn_Register.setOnClickListener(view -> {
            // Xử lý sự kiện khi bấm nút đăng ký
            RegisterToLogin();
        });
    }

    protected void Mapping() {
        // Gán các thành phần giao diện từ layout vào các biến tương ứng
        userEditText = findViewById(R.id.et_User);
        emailEditText = findViewById(R.id.et_Email);
        passEditText = findViewById(R.id.et_Pass);
        cPassEditText = findViewById(R.id.et_CPass);
        btn_Login = findViewById(R.id.btn_Login);
        btn_Register = findViewById(R.id.btn_Register);
        userLayout = findViewById(R.id.til_User);
        emailLayout = findViewById(R.id.til_Email);
        passLayout = findViewById(R.id.til_Pass);
        cPassLayout = findViewById(R.id.til_CPass);

    }

    public void RegisterToLogin() {
        // Lấy dữ liệu từ các trường nhập liệu
        String inputUser = userEditText.getText().toString();
        String inputEmail = emailEditText.getText().toString();
        String inputPass = passEditText.getText().toString();
        String inputCPass = cPassEditText.getText().toString();

        // Kiểm tra tính hợp lệ của dữ liệu nhập vào
        if (!checkInput(inputUser, inputEmail, inputPass, inputCPass)) {
            return;  // Dừng lại nếu thông tin đầu vào không hợp lệ
        }

        // Tạo tài khoản người dùng mới với email và mật khẩu
        auth.createUserWithEmailAndPassword(inputEmail, inputPass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        FirebaseUser user = auth.getCurrentUser(); // Lấy thông tin người dùng hiện tại
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            // Lưu thông tin người dùng vào Realtime Database
                                            String userId = user.getUid(); // Lấy UID của người dùng
                                            DatabaseReference userRef = dbRef.child(userId); // Tạo tham chiếu đến người dùng

                                            // Lưu các thông tin của người dùng
                                            userRef.child("uid").setValue(userId);
                                            userRef.child("username").setValue(inputUser);
                                            userRef.child("email").setValue(inputEmail);
                                            userRef.child("diachi").setValue(""); // Địa chỉ mặc định
                                            userRef.child("sdt").setValue(""); // Số điện thoại mặc định
                                            userRef.child("role").setValue("user"); // Sửa vai trò nếu cần
                                            userRef.child("dieukhoan").setValue(false); // Đặt giá trị mặc định là false
                                            userRef.child("status").setValue("enabled"); // Thêm trạng thái mặc định là enabled

                                            Toast.makeText(this, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show();

                                            // Chuyển đến màn hình điều khoản
                                            Intent intent = new Intent(this, DieuKhoan.class);
                                            intent.putExtra("intent_uid", userId);
                                            intent.putExtra("intent_email", inputEmail);
                                            intent.putExtra("intent_pass", inputPass);
                                            intent.putExtra("ACTIVITY", "Register");
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Register.this, "Lỗi gửi email xác thực", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        // Nếu đăng ký thất bại
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        String errorMessage;

                        // Xử lý lỗi theo mã lỗi
                        switch (errorCode) {
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                errorMessage = "Email đã được sử dụng.";
                                break;
                            case "ERROR_OPERATION_NOT_ALLOWED":
                                errorMessage = "Tính năng này bị tắt.";
                                break;
                            default:
                                errorMessage = "Lỗi đăng ký: " + errorCode;
                                break;
                        }

                        // Hiển thị thông báo lỗi
                        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected boolean checkInput(String inputUser, String inputEmail, String inputPass, String inputCPass) {
        boolean isValid = true;

        // Kiểm tra tên đăng nhập
        if (inputUser.isEmpty()) {
            userLayout.setError("Vui lòng nhập tên đăng nhập");
            userEditText.requestFocus();
            isValid = false;
        } else {
            userLayout.setError(null);
        }

        // Kiểm tra email
        if (inputEmail.isEmpty()) {
            emailLayout.setError("Vui lòng nhập email");
            emailEditText.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            emailLayout.setError("Email không hợp lệ");
            emailEditText.requestFocus();
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // Kiểm tra mật khẩu
        if (inputPass.isEmpty()) {
            passLayout.setError("Vui lòng nhập mật khẩu");
            passEditText.requestFocus();
            isValid = false;
        } else if (inputPass.length() < 6) {
            passLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
            passEditText.requestFocus();
            isValid = false;
        } else {
            passLayout.setError(null);
        }

        // Kiểm tra mật khẩu xác nhận
        if (inputCPass.isEmpty()) {
            cPassLayout.setError("Vui lòng xác nhận mật khẩu");
            cPassEditText.requestFocus();
            isValid = false;
        } else if (!inputPass.equals(inputCPass)) {
            cPassLayout.setError("Mật khẩu không khớp");
            cPassEditText.requestFocus();
            isValid = false;
        } else {
            cPassLayout.setError(null);
        }

        return isValid;
    }

    protected void showPass() {
        // Lắng nghe thay đổi ở trạng thái hiển thị mật khẩu của til_Pass
        passLayout.setEndIconOnClickListener(v -> {
            // Lấy trạng thái hiện tại của inputType (nếu là hiển thị mật khẩu)
            int inputType = passEditText.getInputType();

            if (inputType == 129) {
                passEditText.setInputType(1);
                cPassEditText.setInputType(1);
            } else {
                passEditText.setInputType(129);
                cPassEditText.setInputType(129);
            }
        });
    }
}