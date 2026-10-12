package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.Model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    TextView tvOrderNumber, tvOrderDate, tvTotalAmount, tvOrderStatus, tvOrderItems;
    String orderId;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ view
        tvOrderNumber = findViewById(R.id.tv_order_number);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvOrderItems = findViewById(R.id.tv_order_items);
        btn_back = findViewById(R.id.ivBack);

        // Xử lý sự kiện khi nhấn nút back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        // Hiển thị thông tin đơn hàng
        if (orderId != null) {
            loadOrderDetails(orderId);
        }
    }

    private void loadOrderDetails(String orderId) {
        // Truy cập trực tiếp vào đơn hàng trong node 'don_hang' dựa trên orderId
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("don_hang").child(orderId);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OrderItem order = dataSnapshot.getValue(OrderItem.class);
                if (order != null) {
                    // Hiển thị các thông tin đơn hàng
                    tvOrderNumber.setText("Mã Đơn Hàng: " + order.getMaDonHang());
                    tvOrderDate.setText("• Ngày đặt hàng: " + order.getNgayDatHang());
                    tvTotalAmount.setText("• Tổng tiền: " + order.getTongTien() + " VND");
                    tvOrderStatus.setText("• Trạng thái: " + order.getTrangThai());

                    // Hiển thị danh sách sản phẩm
                    StringBuilder itemsBuilder = new StringBuilder();

                    // Đảm bảo rằng 'sanPham' tồn tại và không null
                    if (order.getSanPham() != null) {
                        for (Map<String, Object> item : order.getSanPham()) {
                            // Lấy thông tin sản phẩm từ HashMap
                            String tenSanPham = (String) item.get("tenSanPham");
                            Long soLuong = (Long) item.get("soLuong");
                            Long gia = (Long) item.get("gia");

                            // Kiểm tra và định dạng thông tin sản phẩm
                            if (tenSanPham != null && soLuong != null && gia != null) {
                                itemsBuilder.append("• ")
                                        .append(tenSanPham)
                                        .append(" - ")
                                        .append(soLuong)
                                        .append(" x ")
                                        .append(gia)
                                        .append(" VND\n");
                            }
                        }
                    }

                    // Đặt chuỗi đã xây dựng vào TextView
                    tvOrderItems.setText(itemsBuilder.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi truy cập Firebase
            }
        });
    }

}
