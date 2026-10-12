package com.example.btl_android.Activity.admin.nhacungcap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_nhacungcap_item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNhacungcapDetailActivity extends AppCompatActivity {

    private EditText editTextSupplierName, editTextSupplierAddress, editTextSupplierEmail, editTextSupplierPhone;
    private Button btnUpdateSupplier;
    private DatabaseReference databaseReference;
    private String supplierId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nhacungcap_detail);

        // Ánh xạ các View
        editTextSupplierName = findViewById(R.id.editTextSupplierName);
        editTextSupplierAddress = findViewById(R.id.editTextSupplierAddress);
        editTextSupplierEmail = findViewById(R.id.editTextSupplierEmail);
        editTextSupplierPhone = findViewById(R.id.editTextSupplierPhone);
        btnUpdateSupplier = findViewById(R.id.btn_update_supplier);
        btnBack = findViewById(R.id.btnBack);


        // Nhận đối tượng Admin_nhacungcap_item từ Intent
        Admin_nhacungcap_item supplier = (Admin_nhacungcap_item) getIntent().getSerializableExtra("supplierItem");

        if (supplier == null) {
            Toast.makeText(this, "Nhà cung cấp không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu trong các EditText
        editTextSupplierName.setText(supplier.getTenNhaCungCap());
        editTextSupplierAddress.setText(supplier.getDiaChi());
        editTextSupplierEmail.setText(supplier.getEmail());
        editTextSupplierPhone.setText(supplier.getSoDienThoai());

        // Lưu supplierId để sử dụng cho các thao tác cập nhật và xóa
        supplierId = supplier.getMaNhaCungCap();
        databaseReference = FirebaseDatabase.getInstance().getReference("nha_cung_cap").child(supplierId);

        // Xử lý sự kiện nhấn nút Cập nhật
        btnUpdateSupplier.setOnClickListener(v -> updateSupplier());


        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateSupplier() {
        String updatedName = editTextSupplierName.getText().toString().trim();
        String updatedAddress = editTextSupplierAddress.getText().toString().trim();
        String updatedEmail = editTextSupplierEmail.getText().toString().trim();
        String updatedPhone = editTextSupplierPhone.getText().toString().trim();

        // Kiểm tra các trường không được để trống
        if (updatedName.isEmpty() || updatedAddress.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Admin_nhacungcap_item updatedSupplier = new Admin_nhacungcap_item(supplierId, updatedName, updatedAddress, updatedEmail, updatedPhone);

        // Cập nhật thông tin lên Firebase dựa trên supplierId
        databaseReference.setValue(updatedSupplier).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminNhacungcapDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AdminNhacungcapDetailActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void deleteSupplier() {
        // Xóa nhà cung cấp từ Firebase dựa trên supplierId
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminNhacungcapDetailActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AdminNhacungcapDetailActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
