package com.example.btl_android.Activity.admin.donhang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_donhang_item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminDonHangDetailActivity extends AppCompatActivity {

    private EditText editTextOrderCode, editTextCustomerName, editTextAddress, editTextPhone, editTextOrderDate, editTextOrderStatus, editTextTotal;
    private Button btnUpdateOrder, btnCancelOrder, btnApproveOrder;
    private DatabaseReference databaseReference;
    private String orderCode;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_don_hang_detail);

        // Ánh xạ các View
        editTextOrderCode = findViewById(R.id.editTextOrderCode);
        editTextCustomerName = findViewById(R.id.editTextCustomerName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextOrderDate = findViewById(R.id.editTextOrderDate);
        editTextOrderStatus = findViewById(R.id.editTextOrderStatus);
        editTextTotal = findViewById(R.id.editTextTotal);
        btnUpdateOrder = findViewById(R.id.btn_update_order);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);
        btnApproveOrder = findViewById(R.id.btn_approve_order);
        btnBack = findViewById(R.id.btnBack);

        // Nhận đối tượng AdminDonhangItem từ Intent
        Admin_donhang_item order = (Admin_donhang_item) getIntent().getSerializableExtra("orderItem");

        if (order == null) {
            Toast.makeText(this, "Đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu
        editTextOrderCode.setText(order.getMaDonHang());
        editTextCustomerName.setText(order.getTenKhachHang());
        editTextAddress.setText(order.getDiaChi());
        editTextPhone.setText(order.getSoDienThoai());
        editTextOrderDate.setText(order.getNgayDatHang());
        editTextOrderStatus.setText(order.getTrangThai());
        editTextTotal.setText(String.valueOf(order.getTongTien()));

        // Lưu orderCode
        orderCode = order.getMaDonHang();
        databaseReference = FirebaseDatabase.getInstance().getReference("don_hang").child(orderCode);

        // Xử lý sự kiện
        btnUpdateOrder.setOnClickListener(v -> updateOrder());
        btnCancelOrder.setOnClickListener(v -> cancelOrder());
        btnApproveOrder.setOnClickListener(v -> approveOrder());
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateOrder() {
        String updatedCustomerName = editTextCustomerName.getText().toString().trim();
        String updatedAddress = editTextAddress.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();
        String updatedOrderDate = editTextOrderDate.getText().toString().trim();
        String updatedOrderStatus = editTextOrderStatus.getText().toString().trim();
        int updatedTotal = Integer.parseInt(editTextTotal.getText().toString().trim());

        if (updatedCustomerName.isEmpty() || updatedAddress.isEmpty() || updatedPhone.isEmpty() || updatedOrderDate.isEmpty() || updatedOrderStatus.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Admin_donhang_item updatedOrder = new Admin_donhang_item(orderCode, updatedCustomerName, updatedAddress, updatedPhone, updatedOrderDate, updatedOrderStatus, updatedTotal, null);
        databaseReference.setValue(updatedOrder).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder() {
        // Kiểm tra trạng thái hiện tại trước khi hủy
        databaseReference.child("trangThai").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String currentStatus = task.getResult().getValue(String.class);
                if ("Đã duyệt".equals(currentStatus)) {
                    Toast.makeText(this, "Không thể hủy đơn hàng đã được duyệt", Toast.LENGTH_SHORT).show();
                } else if ("Đã hủy".equals(currentStatus)) {
                    Toast.makeText(this, "Đơn hàng đã bị hủy trước đó", Toast.LENGTH_SHORT).show();
                } else {
                    // Cập nhật trạng thái thành "Đã hủy"
                    databaseReference.child("trangThai").setValue("Đã hủy").addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Không thể kiểm tra trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveOrder() {
        // Kiểm tra trạng thái hiện tại trước khi duyệt
        databaseReference.child("trangThai").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String currentStatus = task.getResult().getValue(String.class);
                if ("Đã hủy".equals(currentStatus)) {
                    Toast.makeText(this, "Không thể duyệt đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                } else if ("Đã duyệt".equals(currentStatus)) {
                    Toast.makeText(this, "Đơn hàng đã được duyệt trước đó", Toast.LENGTH_SHORT).show();
                } else {
                    // Cập nhật trạng thái thành "Đã duyệt"
                    databaseReference.child("trangThai").setValue("Đã duyệt").addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Đơn hàng đã được duyệt", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Duyệt đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Không thể kiểm tra trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
