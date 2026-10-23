package com.example.btl_android.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Settings.DieuKhoan;
import com.example.btl_android.Activity.Settings.ThongBao;
import com.example.btl_android.Activity.admin.MainAdmin;
import com.example.btl_android.LanguageHelper;
import com.example.btl_android.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText mailEditText, passEditText;
    TextInputLayout mailInputLayout, passInputLayout;
    CheckBox rememberMeCheckBox;
    Button btn_Login, btn_Register, btn_FgPass, btn_Language;
    RelativeLayout loaderLayout;

    FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("taikhoan");
    String languageCode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        LanguageHelper.loadLocale(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        Mapping();
        RegisterToLogin();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        languageCode = prefs.getString("language", "vi");
        btn_Language.setText(languageCode.equals("vi") ? getString(R.string.VI) : getString(R.string.EN));

        rememberMeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("remember", isChecked);
            editor.apply();
        });

        setupRememberMe();

        btn_FgPass.setOnClickListener(view -> {
            Intent intent = new Intent(this, ForgotPass.class);
            startActivity(intent);
        });

        btn_Login.setOnClickListener(view -> {
            showLoader(); // Hiển thị loader khi bắt đầu đăng nhập
            LoginToHomepage();
        });

        btn_Register.setOnClickListener(view -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        btn_Language.setOnClickListener(view -> {
            SharedPreferences.Editor editor = prefs.edit();
            languageCode = languageCode.equals("vi") ? "en" : "vi";
            btn_Language.setText(languageCode.equals("vi") ? getString(R.string.VI) : getString(R.string.EN));
            editor.putString("language", languageCode);
            editor.apply();

            LanguageHelper.changeLocale(this, languageCode);
            recreate();
        });

        setSwitchNotif();
    }

    private void Mapping() {
        mailEditText = findViewById(R.id.et_mail);
        passEditText = findViewById(R.id.et_Pass);
        mailInputLayout = findViewById(R.id.til_Email);
        passInputLayout = findViewById(R.id.til_Pass);
        rememberMeCheckBox = findViewById(R.id.cb_remember);
        btn_FgPass = findViewById(R.id.tv_FgPass);
        btn_Login = findViewById(R.id.btn_Login);
        btn_Register = findViewById(R.id.btn_Register);
        btn_Language = findViewById(R.id.btn_Language);
        loaderLayout = findViewById(R.id.loader_layout);
    }

    private void showLoader() {
        loaderLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loaderLayout.setVisibility(View.GONE);
    }

    private void LoginToHomepage() {
        String inputEmail = mailEditText.getText().toString();
        String inputPass = passEditText.getText().toString();

        if (!checkInput(inputEmail, inputPass)) {
            hideLoader(); // Ẩn loader nếu input không hợp lệ
            return;
        }

        auth.signInWithEmailAndPassword(inputEmail, inputPass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            if (user.getEmail().equals("laptrinhmobile777@gmail.com")) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("taikhoan").child(uid);
                                userRef.child("role").setValue("admin");
                            }
                            saveLoginInfo(uid, inputEmail, inputPass);
                            checkUserRole(uid, user);
                        }
                    } else {
                        handleLoginError(task.getException());
                        hideLoader(); // Ẩn loader khi đăng nhập thất bại
                    }
                });
    }
    private void checkUserRole(String uid, FirebaseUser user) {
        dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);

                    if (status != null && "disabled".equals(status)) {
                        Toast.makeText(Login.this, "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.", Toast.LENGTH_LONG).show();
                        auth.signOut(); // Đăng xuất nếu tài khoản bị khóa
                        return;
                    }

                    if (role != null) {
                        if ("admin".equals(role)) {
                            Intent intent = new Intent(Login.this, MainAdmin.class);
                            intent.putExtra("uid", uid);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            if (user.isEmailVerified()) {
                                Boolean dieukhoan = snapshot.child("dieukhoan").getValue(Boolean.class);
                                if (Boolean.TRUE.equals(dieukhoan)) {
                                    Intent intent = new Intent(Login.this, Homepage.class);
                                    intent.putExtra("ACTIVITY", "Login");
                                    intent.putExtra("intent_uid", uid);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(Login.this, DieuKhoan.class);
                                    intent.putExtra("ACTIVITY", "Login");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(Login.this, "Vui lòng xác thực email trước khi đăng nhập.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "Không tìm thấy thông tin tài khoản.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Lỗi khi lấy thông tin tài khoản: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo(String uid, String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", uid);
        editor.putString("email", email);
        editor.putString("password", password); // Mã hóa trước khi lưu nếu cần
        editor.apply();
    }

    private void handleLoginError(Exception e) {
        String errorMessage = "Đăng nhập không thành công. Vui lòng thử lại.";
        if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            errorMessage = getFirebaseAuthErrorMessage(errorCode);
        } else if (e != null) {
            errorMessage = e.getLocalizedMessage();
        }
        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getFirebaseAuthErrorMessage(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                mailInputLayout.setError("Email không đúng định dạng");
                mailEditText.requestFocus();
                return "Email không đúng định dạng.";
            case "ERROR_WRONG_PASSWORD":
                passInputLayout.setError("Mật khẩu không chính xác");
                passEditText.requestFocus();
                return "Mật khẩu không chính xác.";
            case "ERROR_USER_NOT_FOUND":
                return "Người dùng không tồn tại.";
            case "ERROR_USER_DISABLED":
                return "Tài khoản đã bị vô hiệu hóa.";
            case "ERROR_TOO_MANY_REQUESTS":
                return "Quá nhiều yêu cầu đăng nhập. Vui lòng thử lại sau.";
            case "ERROR_NETWORK_REQUEST_FAILED":
                return "Lỗi mạng. Vui lòng kiểm tra kết nối.";
            case "ERROR_INVALID_CREDENTIAL": // Bổ sung xử lý lỗi này
                return "Thông tin tài khoản không hợp lệ. Vui lòng thử lại.";
            default:
                return "Lỗi không xác định: " + errorCode;
        }
    }



    private boolean checkInput(String inputEmail, String inputPass) {
        boolean isValid = true;

        if (inputEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            mailInputLayout.setError("Email không hợp lệ");
            mailEditText.requestFocus();
            isValid = false;
        } else {
            mailInputLayout.setError(null);
        }

        if (inputPass.isEmpty() || inputPass.length() < 6) {
            passInputLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
            passEditText.requestFocus();
            isValid = false;
        } else {
            passInputLayout.setError(null);
        }

        return isValid;
    }

    private void setupRememberMe() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        rememberMeCheckBox.setChecked(isRemembered);

        if (isRemembered) {
            mailEditText.setText(sharedPreferences.getString("email", ""));
            passEditText.setText(sharedPreferences.getString("password", ""));
        }
    }

    private void RegisterToLogin() {
        Intent in = getIntent();
        String email = in.getStringExtra("email");
        String pass = in.getStringExtra("pass");

        if (email != null || pass != null) {
            mailEditText.setText(email);
            passEditText.setText(pass);
        }
    }

    private void setSwitchNotif() {
        SharedPreferences sharedPreferences = getSharedPreferences(ThongBao.PREFS_NAME, MODE_PRIVATE);
        if (!sharedPreferences.contains(ThongBao.KEY_APP_SWITCH)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ThongBao.KEY_APP_SWITCH, true);
            editor.apply();
        }
    }
}
