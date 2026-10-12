package com.example.btl_android.Activity.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Homepage;
import com.example.btl_android.Activity.Login;
import com.example.btl_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DieuKhoan extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    CheckBox cbAgree;
    Button btnContinue;
    TextView tvTerms;
    ImageView btnBack;
    private static final String FILE_NAME = "dieu_khoan.txt"; // Tên file điều khoản

    // Firebase database để lưu và lấy dữ liệu tài khoản
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("taikhoan");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_dieukhoan);

        // Liên kết các thành phần giao diện với code
        cbAgree = findViewById(R.id.cbAgree);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.ivBack);

        // Sự kiện khi nhấn nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Hiển thị nội dung điều khoản từ file assets
        tvTerms = findViewById(R.id.tvTerms);
        String fileContent = readFromAssets(); // Đọc nội dung file điều khoản
        tvTerms.setText(fileContent); // Hiển thị nội dung điều khoản
        tvTerms.setTextColor(getResources().getColor(R.color.black)); // Đặt màu chữ cho điều khoản

        XuLyDieuKhoan();
    }

    private void XuLyDieuKhoan() {
        Intent in = getIntent();
        String activity = in.getStringExtra("ACTIVITY");

        if ("Setting".equals(activity)) {
            // Lấy thông tin UID từ SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String uid = sharedPreferences.getString("uid", null);

            // Kiểm tra nếu UID không rỗng
            if (uid != null) {
                // Lắng nghe sự thay đổi dữ liệu điều khoản trong Firebase
                dbRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy giá trị điều khoản từ Firebase
                        Boolean dieukhoan = snapshot.child(uid).child("dieukhoan").getValue(Boolean.class);

                        // Nếu người dùng đã đồng ý điều khoản trước đó
                        if (dieukhoan != null && dieukhoan) {
                            btnContinue.setVisibility(View.GONE); // Ẩn nút tiếp tục
                            cbAgree.setChecked(true); // Đánh dấu checkbox là đã đồng ý
                            cbAgree.setEnabled(false); // Không cho phép thay đổi checkbox
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Thông báo lỗi khi tải dữ liệu Firebase thất bại
                        Toast.makeText(DieuKhoan.this, "Lỗi khi tải dữ liệu điều khoản: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Nếu UID rỗng, yêu cầu người dùng đăng nhập lại
                Toast.makeText(DieuKhoan.this, "Không tìm thấy người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Login.class); // Chuyển hướng đến màn hình đăng nhập
                startActivity(intent);
                finish(); // Kết thúc Activity hiện tại
            }
        }
        else if ("Register".equals(activity)) {
            btnBack.setVisibility(View.GONE); // Ẩn nút quay lại
            // Xử lý sự kiện khi nhấn nút tiếp tục
            btnContinue.setOnClickListener(view -> {
                // Kiểm tra xem người dùng đã đồng ý với điều khoản chưa
                if (cbAgree.isChecked()) {
                    String intent_uid = getIntent().getStringExtra("intent_uid");

                    // Kiểm tra nếu intent_uid không rỗng
                    if (intent_uid != null) {
                        dbRef.child(intent_uid).child("dieukhoan").setValue(true); // Lưu trạng thái đồng ý điều khoản vào Firebase

                        Intent intent = new Intent(this, Login.class); // Chuyển hướng đến màn hình đăng nhập
                        intent.putExtra("email", getIntent().getStringExtra("intent_email"));
                        intent.putExtra("pass", getIntent().getStringExtra("intent_pass"));
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại
                    }
                } else {
                    // Thông báo nếu người dùng chưa đồng ý với điều khoản
                    Toast.makeText(DieuKhoan.this, "Vui lòng đồng ý với các điều khoản để tiếp tục", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if ("Login".equals(activity)) {
            btnBack.setVisibility(View.GONE); // Ẩn nút quay lại
            btnContinue.setOnClickListener(v -> {
                if (cbAgree.isChecked()) {
                    String intent_uid = getIntent().getStringExtra("intent_uid");

                    // Kiểm tra nếu intent_uid không rỗng
                    if (intent_uid != null) {
                        dbRef.child(intent_uid).child("dieukhoan").setValue(true); // Lưu trạng thái đồng ý điều khoản vào Firebase

                        Intent intent = new Intent(this, Homepage.class); // Chuyển hướng đến màn hình đăng nhập
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại
                    }
                } else {
                    // Thông báo nếu người dùng chưa đồng ý với điều khoản
                    Toast.makeText(DieuKhoan.this, "Vui lòng đồng ý với các điều khoản để tiếp tục", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Phương thức để đọc nội dung file điều khoản từ assets
    private String readFromAssets() {
        StringBuilder text = new StringBuilder();

        try {
            InputStream is = getAssets().open(FILE_NAME); // Mở file từ thư mục assets
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n"); // Đọc từng dòng và thêm vào chuỗi text
            }

            reader.close(); // Đóng reader
            is.close(); // Đóng InputStream
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString(); // Trả về nội dung file dưới dạng chuỗi
    }
}