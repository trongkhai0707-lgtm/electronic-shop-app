package com.example.btl_android.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Adapter.OrderAdapter;
import com.example.btl_android.R;
import com.example.btl_android.Model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonHang extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderList = new ArrayList<>();
    private ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang);

        btn_back = findViewById(R.id.ivBack);

        // Xử lý sự kiện khi nhấn nút back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo RecyclerView
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu từ Firebase
        loadOrdersFromFirebase();
    }

    private void loadOrdersFromFirebase() {
        String uid = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("uid", null);
        if (uid != null) {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("don_hang");
            Query query = ordersRef.orderByChild("uid").equalTo(uid);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    orderList.clear(); // Xóa dữ liệu cũ
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderItem order = orderSnapshot.getValue(OrderItem.class);
                        if (order != null) {
                            orderList.add(order);
                        }
                    }

                    // Sắp xếp danh sách theo ngày đặt hàng giảm dần
                    Collections.sort(orderList, new Comparator<OrderItem>() {
                        @Override
                        public int compare(OrderItem o1, OrderItem o2) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            try {
                                Date date1 = sdf.parse(o1.getNgayDatHang());
                                Date date2 = sdf.parse(o2.getNgayDatHang());
                                return date2.compareTo(date1); // So sánh để sắp xếp giảm dần
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    // Cập nhật dữ liệu cho Adapter
                    orderAdapter = new OrderAdapter(DonHang.this, orderList);
                    recyclerViewOrders.setAdapter(orderAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }
}
