package com.example.btl_android.Activity.admin.nhacungcap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class add_nhacungcap extends AppCompatActivity {

    private EditText editTextSupplierCode, editTextSupplierName, editTextSupplierAddress, editTextSupplierPhone, editTextSupplierEmail;
    private Button btnAddSupplier;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nhacungcap);

        // Ánh xạ các View
        editTextSupplierCode = findViewById(R.id.editTextSupplierCode);
        editTextSupplierName = findViewById(R.id.editTextSupplierName);
        editTextSupplierAddress = findViewById(R.id.editTextSupplierAddress);
        editTextSupplierPhone = findViewById(R.id.editTextSupplierPhone);
        editTextSupplierEmail = findViewById(R.id.editTextSupplierEmail);
        btnAddSupplier = findViewById(R.id.btnAddSupplier);

        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("nha_cung_cap");

        // Xử lý sự kiện nhấn nút "Thêm nhà cung cấp"
        btnAddSupplier.setOnClickListener(v -> checkAndAddSupplier());
    }

    private void checkAndAddSupplier() {
        String code = editTextSupplierCode.getText().toString().trim();
        String name = editTextSupplierName.getText().toString().trim();
        String address = editTextSupplierAddress.getText().toString().trim();
        String phone = editTextSupplierPhone.getText().toString().trim();
        String email = editTextSupplierEmail.getText().toString().trim();

        // Kiểm tra các trường nhập không được để trống
        if (code.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mã nhà cung cấp đã tồn tại hay chưa
        databaseReference.orderByChild("ma_nha_cung_cap").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mã nhà cung cấp đã tồn tại
                    Toast.makeText(add_nhacungcap.this, "Mã nhà cung cấp đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    // Thêm nhà cung cấp
                    addSupplier(code, name, address, phone, email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(add_nhacungcap.this, "Lỗi khi kiểm tra mã nhà cung cấp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSupplier(String code, String name, String address, String phone, String email) {
        // Tạo Map để lưu thông tin nhà cung cấp
        Map<String, Object> supplierData = new HashMap<>();
        supplierData.put("ma_nha_cung_cap", code);
        supplierData.put("ten_nha_cung_cap", name);
        supplierData.put("dia_chi", address);
        supplierData.put("so_dien_thoai", phone);
        supplierData.put("email", email);

        // Thêm dữ liệu vào Firebase với mã nhà cung cấp làm key
        databaseReference.child(code).setValue(supplierData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(add_nhacungcap.this, "Thêm nhà cung cấp thành công", Toast.LENGTH_SHORT).show();
                finish();  // Quay lại màn hình trước đó
            } else {
                Toast.makeText(add_nhacungcap.this, "Thêm nhà cung cấp thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
