package com.example.btl_android.Activity.admin.donhang;

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

import com.example.btl_android.Adapter.admin.Admin_donhang_adapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_donhang_item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class admin_donhang extends AppCompatActivity {
    //Khai báo hiển thị danh sách đơn hàng
    private RecyclerView recyclerView;
    //liên kết dữ liệu với RecyclerView.
    private Admin_donhang_adapter donhangAdapter;
    //Khai báo danh sách đơn hàng sẽ hiển thị trong RecyclerView
    private List<Admin_donhang_item> donhangList;
    //Khai báo đối tượng chiếu tới fbase
    private DatabaseReference databaseReference;
    private EditText editTextSearchOrder;
    private FloatingActionButton btnAddOrder;

    private ValueEventListener valueEventListener;
    private static final int REQUEST_CODE_UPDATE_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //thiết lập đường dẫn tới layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donhang);

        // Ánh xạ các View
        recyclerView = findViewById(R.id.order_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donhangList = new ArrayList<>();
        donhangAdapter = new Admin_donhang_adapter(donhangList, this);
        recyclerView.setAdapter(donhangAdapter);

        editTextSearchOrder = findViewById(R.id.editTextSearchOrder);

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("don_hang");

        // Thiết lập giá trị ValueEventListener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donhangList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Admin_donhang_item donHang = orderSnapshot.getValue(Admin_donhang_item.class);
                    if (donHang != null) {
                        donhangList.add(donHang);
                    }
                }
                donhangAdapter.notifyDataSetChanged();
                // Tìm kiếm với ký tự trống để hiện tất cả đơn hàng khi dữ liệu được tải
                editTextSearchOrder.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_donhang.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // Tải dữ liệu đơn hàng
        loadDonhangData();

        // Thiết lập tìm kiếm đơn hàng
        editTextSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donhangAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện click item đơn hàng
        donhangAdapter.setOnItemClickListener(donHang -> {
            Intent intent = new Intent(admin_donhang.this, AdminDonHangDetailActivity.class);
            intent.putExtra("orderItem", donHang);
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
            loadDonhangData();
        }
    }

    private void loadDonhangData() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener); // Gỡ bỏ ValueEventListener cũ
        }
        databaseReference.addValueEventListener(valueEventListener); // Thêm ValueEventListener mới
    }
}
