package com.example.btl_android.Activity.admin.sanpham;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_android.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class add_sanpham extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextProductName, editTextProductCode, editTextProductPrice, editTextProductImageUrl, editTextProductStock, editTextProductDescription, editTextProductSupplier;
    private Spinner spinnerProductType;
    private MaterialButton buttonAddProduct;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_sanpham);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Liên kết các View từ XML
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductCode = findViewById(R.id.editTextProductCode);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductImageUrl = findViewById(R.id.editTextProductImageUrl);
        editTextProductStock = findViewById(R.id.editTextProductStock);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductSupplier = findViewById(R.id.editTextProductSupplier);
        spinnerProductType = findViewById(R.id.spinnerProductType);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        // Khởi tạo Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("san_pham");

        // Xử lý sự kiện khi nhấn nút "Thêm sản phẩm"
        buttonAddProduct.setOnClickListener(v -> {
            String productName = editTextProductName.getText().toString().trim();
            String productCode = editTextProductCode.getText().toString().trim();
            String productPrice = editTextProductPrice.getText().toString().trim();
            String productImageUrl = editTextProductImageUrl.getText().toString().trim();
            String productStock = editTextProductStock.getText().toString().trim();
            String productDescription = editTextProductDescription.getText().toString().trim();
            String productSupplier = editTextProductSupplier.getText().toString().trim();
            String productType = spinnerProductType.getSelectedItem().toString();

            // Kiểm tra nếu các trường dữ liệu không bị rỗng
            if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productStock)) {
                Toast.makeText(add_sanpham.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Thêm sản phẩm vào Firebase
                addNewProduct(productName, productCode, productPrice, productImageUrl, productStock, productDescription, productSupplier, productType);
            }
        });
    }

    private void addNewProduct(String name, String code, String price, String imageUrl, String stock, String description, String supplier, String type) {
        // Tạo một Map để lưu thông tin sản phẩm
        Map<String, Object> product = new HashMap<>();
        product.put("ten_san_pham", name);
        product.put("ma_san_pham", code);

        try {
            // Chuyển đổi giá và số lượng tồn kho sang long để xử lý số lớn
            if (TextUtils.isDigitsOnly(price)) {
                product.put("gia", Long.parseLong(price)); // Sử dụng long
            } else {
                Toast.makeText(this, "Giá phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isDigitsOnly(stock)) {
                product.put("so_luong_ton_kho", Long.parseLong(stock)); // Sử dụng long
            } else {
                Toast.makeText(this, "Số lượng tồn kho phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá hoặc số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        product.put("hinh_anh", imageUrl);
        product.put("mo_ta", description);
        product.put("nha_cung_cap", supplier);
        product.put("loai", type);

        // Lưu thông tin sản phẩm vào Firebase Database
        String productId = databaseReference.push().getKey();
        if (productId != null) {
            databaseReference.child(productId).setValue(product)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(add_sanpham.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại màn hình trước đó sau khi thêm thành công
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi khi lưu thông tin sản phẩm";
                            Toast.makeText(add_sanpham.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(add_sanpham.this, "Lỗi khi tạo sản phẩm mới", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks
        if (item.getItemId() == android.R.id.home) {
            // Handle the home button (back button) press
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
