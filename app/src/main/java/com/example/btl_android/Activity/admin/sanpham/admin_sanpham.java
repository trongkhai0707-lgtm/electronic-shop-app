package com.example.btl_android.Activity.admin.sanpham;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Activity.admin.nhacungcap.add_nhacungcap;
import com.example.btl_android.Adapter.admin.Admin_sanpham_adapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_sanpham_item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class admin_sanpham extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Admin_sanpham_adapter sanPhamAdapter;
    private List<Admin_sanpham_item> sanPhamList;
    private DatabaseReference databaseReference;
    private EditText editTextSearchProduct;
    private ValueEventListener valueEventListener;
    private FloatingActionButton btn_add;

    private static final int REQUEST_CODE_UPDATE_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sanpham);

        // Ánh xạ các View
        recyclerView = findViewById(R.id.product_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sanPhamList = new ArrayList<>();
        sanPhamAdapter = new Admin_sanpham_adapter(sanPhamList, this);
        recyclerView.setAdapter(sanPhamAdapter);

        btn_add = findViewById(R.id.btn_add_product);
        btn_add.setOnClickListener(nhacungcap -> {
            Intent intent = new Intent(this, add_sanpham.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
        });

        editTextSearchProduct = findViewById(R.id.editTextSearchProduct);

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("san_pham");

        // Thiết lập giá trị ValueEventListener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sanPhamList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Admin_sanpham_item sanPham = dataSnapshot.getValue(Admin_sanpham_item.class);
                    if (sanPham != null) {
                        sanPham.setUid(dataSnapshot.getKey());  // Gán UID từ Firebase
                        sanPhamList.add(sanPham);
                    }
                }
                sanPhamAdapter.notifyDataSetChanged();
                // Tìm kiếm một ký tự rỗng để hiện tất cả sản phẩm khi dữ liệu được tải
                editTextSearchProduct.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_sanpham.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // Bắt đầu việc tải dữ liệu
        loadSanPhamData();

        // Thiết lập chức năng tìm kiếm
        editTextSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sanPhamAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sử dụng Intent để truyền dữ liệu giữa các Activity
        sanPhamAdapter.setOnItemClickListener(sanPham -> {
            Intent intent = new Intent(admin_sanpham.this, AdminSanphamDetailActivity.class);
            intent.putExtra("productItem", sanPham); // Truyền đối tượng Admin_sanpham_item qua Intent
            startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener); // Gỡ bỏ ValueEventListener
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_DELETE && resultCode == RESULT_OK) {
            // Làm mới dữ liệu khi có kết quả trả về
            loadSanPhamData();
        }
    }

    private void loadSanPhamData() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener); // Gỡ bỏ ValueEventListener cũ
        }
        databaseReference.addValueEventListener(valueEventListener); // Thêm ValueEventListener mới
    }
}
