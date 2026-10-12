package com.example.btl_android.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.Model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductDetailActivity extends AppCompatActivity {

    // Các thành phần giao diện
    ImageView productImageView;
    TextView productNameTextView, productPriceTextView, productDescriptionTextView, productStockTextView, productCategoryTextView;
    Button buttonBuy, buttonAddToCart;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Kích hoạt nút "Back" trong Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Khởi tạo FirebaseAuth và DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Khởi tạo các thành phần giao diện
        productImageView = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productStockTextView = findViewById(R.id.productStockTextView);
        productCategoryTextView = findViewById(R.id.productCategoryTextView);
        buttonBuy = findViewById(R.id.buttonBuy);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String productName = intent.getStringExtra("ten_san_pham");
        long productPrice = intent.getLongExtra("gia", 0);
        String productImage = intent.getStringExtra("hinh_anh");
        String productDescription = intent.getStringExtra("mo_ta");
        int productStock = intent.getIntExtra("so_luong_ton_kho", 0);
        String productCategory = intent.getStringExtra("loai_san_pham");

        // Hiển thị thông tin chi tiết sản phẩm
        productNameTextView.setText(productName);
        productPriceTextView.setText(String.format("%,d VND", productPrice));
        productDescriptionTextView.setText(productDescription);
        productStockTextView.setText("Còn lại: " + productStock);
        productCategoryTextView.setText("Loại: " + productCategory);

        // Sử dụng Glide để tải và hiển thị ảnh sản phẩm
        Glide.with(this).load(productImage).into(productImageView);

        // Xử lý sự kiện khi nhấn nút "Mua"
        buttonBuy.setOnClickListener(v -> handleBuyProduct());


        // Xử lý sự kiện khi nhấn nút "Thêm vào giỏ hàng"
        buttonAddToCart.setOnClickListener(v -> addToCart());
    }

    // Hàm riêng để xử lý sự kiện mua sản phẩm
    private void handleBuyProduct() {
        Intent intent = new Intent(ProductDetailActivity.this, MuaHang.class);

        // Truyền dữ liệu sản phẩm qua Intent
        intent.putExtra("ten_san_pham", productNameTextView.getText().toString());
        intent.putExtra("gia", getIntent().getLongExtra("gia", 0));
        intent.putExtra("hinh_anh", getIntent().getStringExtra("hinh_anh"));
        intent.putExtra("mo_ta", getIntent().getStringExtra("mo_ta"));
        intent.putExtra("so_luong_ton_kho", getIntent().getIntExtra("so_luong_ton_kho", 0));
        intent.putExtra("loai_san_pham", getIntent().getStringExtra("loai_san_pham"));

        // Đánh dấu rằng hoạt động này đến từ ProductDetailActivity
        intent.putExtra("from_cart", false);

        // Chuyển tới màn hình MuaHang
        startActivity(intent);
    }



    // Hàm thêm sản phẩm vào giỏ hàng
    private void addToCart() {
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String productName = intent.getStringExtra("ten_san_pham");
        long productPrice = intent.getLongExtra("gia", 0);
        String productImage = intent.getStringExtra("hinh_anh");

        // Lấy ID người dùng hiện tại
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        databaseReference.child("cart").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean productExists = false;

                // Kiểm tra từng sản phẩm trong giỏ hàng
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    CartItem existingCartItem = snapshot.getValue(CartItem.class);

                    if (existingCartItem != null && existingCartItem.getName().equals(productName)) {
                        // Nếu sản phẩm đã tồn tại, tăng số lượng
                        int currentQuantity = existingCartItem.getQuantity();
                        snapshot.getRef().child("quantity").setValue(currentQuantity + 1);
                        Toast.makeText(ProductDetailActivity.this, "Đã tăng số lượng sản phẩm!", Toast.LENGTH_SHORT).show();
                        productExists = true;
                        break;
                    }
                }

                if (!productExists) {
                    // Nếu sản phẩm chưa có trong giỏ, thêm sản phẩm mới
                    // Sử dụng push() để tạo ID tự động và duy nhất cho mỗi sản phẩm
                    DatabaseReference newCartItemRef = databaseReference.child("cart").child(userId).push();
                    CartItem newCartItem = new CartItem(productName, productPrice, 1, productImage);
                    newCartItemRef.setValue(newCartItem)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thất bại.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(ProductDetailActivity.this, "Không thể truy cập giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý khi người dùng nhấn nút "Back" trên Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
