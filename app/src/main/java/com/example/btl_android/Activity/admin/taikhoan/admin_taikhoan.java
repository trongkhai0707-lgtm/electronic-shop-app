package com.example.btl_android.Activity.admin.taikhoan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Adapter.admin.Admin_taikhoan_adapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_taikhoan_item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class admin_taikhoan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Admin_taikhoan_adapter taiKhoanAdapter;
    private List<Admin_taikhoan_item> taiKhoanList;
    private DatabaseReference databaseReference;
    private FloatingActionButton btn_add;
    private EditText editTextSearch;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_taikhoan);

        recyclerView = findViewById(R.id.card_view_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taiKhoanList = new ArrayList<>();
        taiKhoanAdapter = new Admin_taikhoan_adapter(taiKhoanList, this);
        recyclerView.setAdapter(taiKhoanAdapter);

        editTextSearch = findViewById(R.id.editTextSearch);

        // Lấy dữ liệu từ Firebase (bảng Admin_taikhoan)
        databaseReference = FirebaseDatabase.getInstance().getReference("taikhoan");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taiKhoanList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    // Chỉ thêm vào danh sách nếu role là "user"
                    if ("user".equals(role)) {
                        Admin_taikhoan_item taiKhoan = new Admin_taikhoan_item();

                        // Thử lấy trường 'sdt' dưới dạng Long hoặc String
                        Object sdtObject = dataSnapshot.child("sdt").getValue();
                        String sdt;
                        if (sdtObject instanceof Long) {
                            sdt = sdtObject.toString();
                        } else if (sdtObject instanceof String) {
                            sdt = (String) sdtObject;
                        } else {
                            sdt = "";
                        }

                        // Gán các giá trị khác
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String diachi = dataSnapshot.child("diachi").getValue(String.class);
                        String uid = dataSnapshot.child("uid").getValue(String.class);

                        // Đặt giá trị vào đối tượng taiKhoan
                        taiKhoan.setSdt(sdt);
                        taiKhoan.setUsername(username);
                        taiKhoan.setEmail(email);
                        taiKhoan.setDiachi(diachi);
                        taiKhoan.setUid(uid);
                        taiKhoan.setRole(role);

                        // Thêm đối tượng vào danh sách
                        taiKhoanList.add(taiKhoan);
                    }
                }

                taiKhoanAdapter.notifyDataSetChanged(); // Thông báo adapter về thay đổi dữ liệu

                // Tự động tìm kiếm một khoảng trắng khi dữ liệu được tải
                editTextSearch.setText(" ");
                editTextSearch.setSelection(editTextSearch.getText().length());
                taiKhoanAdapter.getFilter().filter(" ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_taikhoan.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập chức năng tìm kiếm
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taiKhoanAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(view -> {
            // Tạo PopupMenu
            PopupMenu popupMenu = new PopupMenu(this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_admin_taikhoan, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_add_taikhoan) {
                    // Chuyển đến màn hình thêm tài khoản
                    Intent addAccountIntent = new Intent(this, add_taikhoan.class);
                    startActivity(addAccountIntent);
                    return true;
                } else if (id == R.id.action_change_password) {
                    // Chuyển đến màn hình đổi mật khẩu
                    Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
                    startActivity(changePasswordIntent);
                    return true;
                } else {
                    return false;
                }
            });

            // Hiển thị menu
            popupMenu.show();
        });

    }
}
