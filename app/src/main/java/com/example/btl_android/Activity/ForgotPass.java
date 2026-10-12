package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;
import java.util.Map;

public class ForgotPass extends AppCompatActivity {

    // Khai báo các phần tử
    EditText mailEditText, passEditText, cPassEditText;
    Button btnSend, btnConfirm;
    TextInputLayout mailInputLayout, passInputLayout, cPassInputLayout;
    ImageView ivBack;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpass);

        Mapping();

        btnSend.setOnClickListener(view -> {
            String email = mailEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                checkEmailExists(email);
            } else {
                mailInputLayout.setError("Vui lòng nhập email!");
            }
        });

        ivBack.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPass.this, Login.class);
            startActivity(intent);
        });
    }

    private void Mapping() {
        mailEditText = findViewById(R.id.et_Email);
        btnSend = findViewById(R.id.btn_send);
        mailInputLayout = findViewById(R.id.til_Email);
        ivBack = findViewById(R.id.ivBack);
    }

    private void checkEmailExists(String email) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();

                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                // Email đã tồn tại
                                SendMailResetPass(email);
                            } else {
                                // Email không tồn tại
                                mailInputLayout.setError("Email không tồn tại trong hệ thống!");
                            }
                        } else {
                            Toast.makeText(ForgotPass.this, "Kiểm tra email thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void SendMailResetPass(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Gửi email thành công
                            Toast.makeText(ForgotPass.this, "Email xác nhận đã được gửi!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Gửi email thất bại
                            Toast.makeText(ForgotPass.this, "Gửi email xác nhận thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}