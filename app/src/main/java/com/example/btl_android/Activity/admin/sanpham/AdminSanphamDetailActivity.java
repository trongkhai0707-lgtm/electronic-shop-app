package com.example.btl_android.Activity.admin.sanpham;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_sanpham_item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminSanphamDetailActivity extends AppCompatActivity {

    private EditText productNameEditText, productPriceEditText, productDescriptionEditText, productStockEditText, productCategoryEditText;
    private ImageView productImageView;
    private Button btnUpdateProduct, btnDeleteProduct;
    private DatabaseReference databaseReference;
    private String productId;

    private static final String TAG = "AdminSanphamDetailActivity";

    // Chuỗi cố định cho các trường
    private static final String NAME_PREFIX = "Tên sản phẩm: ";
    private static final String PRICE_PREFIX = "Giá: ";
    private static final String DESCRIPTION_PREFIX = "Mô tả: ";
    private static final String STOCK_PREFIX = "Số lượng tồn kho: ";
    private static final String CATEGORY_PREFIX = "Loại: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_sanpham_detail);

        // Liên kết các view
        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        productStockEditText = findViewById(R.id.productStockEditText);
        productCategoryEditText = findViewById(R.id.productCategoryEditText);
        productImageView = findViewById(R.id.productImageView);

        btnUpdateProduct = findViewById(R.id.btn_update_product);
        btnDeleteProduct = findViewById(R.id.btn_delete_product);

        // Thiết lập Toolbar và nút back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Nhận đối tượng Admin_sanpham_item từ Intent
        Admin_sanpham_item sanPham = (Admin_sanpham_item) getIntent().getSerializableExtra("productItem");

        if (sanPham == null) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu trong các EditText và ImageView với tiền tố cố định
        productNameEditText.setText(NAME_PREFIX + sanPham.getTenSanPham());
        productPriceEditText.setText(PRICE_PREFIX + sanPham.getGia());
        productDescriptionEditText.setText(DESCRIPTION_PREFIX + sanPham.getMoTa());
        productStockEditText.setText(STOCK_PREFIX + sanPham.getSoLuongTonKho());
        productCategoryEditText.setText(CATEGORY_PREFIX + sanPham.getLoai());

        // Khóa phần trước dấu hai chấm trong EditText
        lockPrefixInEditText(productNameEditText, NAME_PREFIX);
        lockPrefixInEditText(productPriceEditText, PRICE_PREFIX);
        lockPrefixInEditText(productDescriptionEditText, DESCRIPTION_PREFIX);
        lockPrefixInEditText(productStockEditText, STOCK_PREFIX);
        lockPrefixInEditText(productCategoryEditText, CATEGORY_PREFIX);


        // Lưu productId để sử dụng cho các thao tác cập nhật và xóa
        productId = sanPham.getUid();  // Sử dụng UID của sản phẩm từ Firebase

        // Load ảnh sản phẩm bằng Glide
        String productImageUrl = sanPham.getHinhAnh();
        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(productImageUrl)
                    .placeholder(R.drawable.ic_imagnot)
                    .error(R.drawable.ic_error)
                    .into(productImageView);
        }

        // Khởi tạo DatabaseReference với productId
        databaseReference = FirebaseDatabase.getInstance().getReference("san_pham").child(productId);

        // Xử lý sự kiện nhấn nút Cập nhật
        btnUpdateProduct.setOnClickListener(v -> updateProduct());

        // Xử lý sự kiện nhấn nút Xóa
        btnDeleteProduct.setOnClickListener(v -> deleteProduct());
    }

    // Phương thức khóa phần trước dấu hai chấm
    private void lockPrefixInEditText(EditText editText, String prefix) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                // Kiểm tra nếu phần đầu không khớp với prefix thì đặt lại
                if (!s.toString().startsWith(prefix)) {
                    editText.setText(prefix);
                    editText.setSelection(prefix.length());  // Di chuyển con trỏ sau prefix
                } else {
                    // Ngăn người dùng chỉnh sửa phần prefix
                    if (s.length() < prefix.length()) {
                        editText.setText(prefix);
                        editText.setSelection(prefix.length());
                    }
                }

                isEditing = false;
            }
        });
    }

    // Phương thức để chỉ cập nhật phần sau dấu hai chấm
    private void updateProduct() {
        String updatedName = extractValueAfterColon(productNameEditText.getText().toString().trim());
        long updatedPrice; // Đổi từ int sang long
        String updatedDescription = extractValueAfterColon(productDescriptionEditText.getText().toString().trim());
        String updatedCategory = extractValueAfterColon(productCategoryEditText.getText().toString().trim());
        long updatedStock; // Đổi từ int sang long

        try {
            updatedPrice = Long.parseLong(extractValueAfterColon(productPriceEditText.getText().toString().trim())); // Sử dụng long
            updatedStock = Long.parseLong(extractValueAfterColon(productStockEditText.getText().toString().trim())); // Sử dụng long
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá và số lượng tồn kho không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Map để lưu các thay đổi
        Map<String, Object> updates = new HashMap<>();
        updates.put("ten_san_pham", updatedName);
        updates.put("gia", updatedPrice); // Cập nhật giá với kiểu long
        updates.put("mo_ta", updatedDescription);
        updates.put("so_luong_ton_kho", updatedStock); // Cập nhật số lượng tồn kho với kiểu long
        updates.put("loai", updatedCategory);

        // Cập nhật dữ liệu vào Firebase
        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminSanphamDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);  // Gửi kết quả thành công
                finish();  // Kết thúc Activity và quay lại Activity trước
            } else {
                Toast.makeText(AdminSanphamDetailActivity.this, "Cập nhật thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Hàm phụ để trích xuất giá trị sau dấu hai chấm
    private String extractValueAfterColon(String text) {
        int colonIndex = text.indexOf(":");
        if (colonIndex != -1) {
            return text.substring(colonIndex + 1).trim();
        } else {
            return text;  // Nếu không có dấu hai chấm, trả về toàn bộ text
        }
    }

    private void deleteProduct() {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminSanphamDetailActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);  // Gửi kết quả thành công
                finish();  // Kết thúc Activity và quay lại Activity trước
            } else {
                Toast.makeText(AdminSanphamDetailActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

