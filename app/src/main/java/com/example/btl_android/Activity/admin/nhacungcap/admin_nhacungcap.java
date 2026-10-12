package com.example.btl_android.Activity.admin.nhacungcap;

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

import com.example.btl_android.Adapter.admin.Admin_nhacungcap_adapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_nhacungcap_item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class admin_nhacungcap extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Admin_nhacungcap_adapter nhacungcapAdapter;
    private List<Admin_nhacungcap_item> nhacungcapList;
    private DatabaseReference databaseReference;
    private EditText editTextSearchSupplier;
    private FloatingActionButton btn_add;

    private ValueEventListener valueEventListener;

    private static final int REQUEST_CODE_UPDATE_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nhacungcap);

        // Ánh xạ các View
        recyclerView = findViewById(R.id.supplier_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nhacungcapList = new ArrayList<>();
        nhacungcapAdapter = new Admin_nhacungcap_adapter(nhacungcapList, this);
        recyclerView.setAdapter(nhacungcapAdapter);

        editTextSearchSupplier = findViewById(R.id.editTextSearchSupplier);
        btn_add = findViewById(R.id.btn_add_supplier);
        btn_add.setOnClickListener(nhacungcap -> {
            Intent intent = new Intent(this, add_nhacungcap.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
        });


        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("nha_cung_cap");

        // Thiết lập giá trị ValueEventListener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nhacungcapList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Admin_nhacungcap_item nhacungcap = dataSnapshot.getValue(Admin_nhacungcap_item.class);
                    if (nhacungcap != null) {
                        nhacungcapList.add(nhacungcap);
                    }
                }
                nhacungcapAdapter.notifyDataSetChanged();
                // Tìm kiếm với ký tự rỗng để hiện tất cả nhà cung cấp khi dữ liệu được tải
                editTextSearchSupplier.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_nhacungcap.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // Tải dữ liệu nhà cung cấp
        loadNhaCungCapData();

        // Thiết lập tìm kiếm nhà cung cấp
        editTextSearchSupplier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nhacungcapAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện click item nhà cung cấp
        nhacungcapAdapter.setOnItemClickListener(nhacungcap -> {
            Intent intent = new Intent(admin_nhacungcap.this, AdminNhacungcapDetailActivity.class);
            intent.putExtra("supplierItem", nhacungcap);
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
            loadNhaCungCapData();
        }
    }

    private void loadNhaCungCapData() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener); // Gỡ bỏ ValueEventListener cũ
        }
        databaseReference.addValueEventListener(valueEventListener); // Thêm ValueEventListener mới
    }
}
