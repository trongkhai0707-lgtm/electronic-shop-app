package com.example.btl_android.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Adapter.CartAdapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.CartItem;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    String uid;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Lấy uid từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "UID không hợp lệ, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.cartRecyclerView);

        // Sử dụng try-catch để xử lý các vấn đề về layout
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cập nhật đường dẫn cơ sở dữ liệu với uid
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("cart").child(uid);
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(cartRef, CartItem.class)
                        .build();

        cartAdapter = new CartAdapter(options, this);
        recyclerView.setAdapter(cartAdapter);

        // Đăng ký observer cho adapter để xử lý các thay đổi
        cartAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                recyclerView.getRecycledViewPool().clear();
                cartAdapter.notifyDataSetChanged();
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button btn_mua = findViewById(R.id.btn_mua);
        btn_mua.setOnClickListener(view -> {
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {

                        recyclerView.getRecycledViewPool().clear();
                        cartAdapter.notifyDataSetChanged();

                        Intent intent = new Intent(Cart.this, MuaHang.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Đánh dấu rằng hoạt động này đến từ giỏ hàng
                        intent.putExtra("from_cart", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Cart.this, "Bạn không có mặt hàng nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Cart.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cartAdapter != null) {
            cartAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cartAdapter != null) {
            cartAdapter.stopListening();
        }
    }
}
