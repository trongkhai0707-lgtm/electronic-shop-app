package com.example.btl_android.Activity.admin.donhang;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_donhang_item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminDonHangDetailActivity extends AppCompatActivity {

    // Các trạng thái của đơn hàng (khớp với dữ liệu đang có trên Firebase)
    private static final String ST_PLACED = "Đã đặt hàng";
    private static final String ST_APPROVED = "Đã duyệt";
    private static final String ST_SHIPPING = "Đang giao";
    private static final String ST_DELIVERED = "Đã giao hàng";
    private static final String ST_CANCELLED = "Đã hủy";

    private EditText editTextOrderCode, editTextCustomerName, editTextAddress, editTextPhone, editTextOrderDate, editTextOrderStatus, editTextTotal;
    private Button btnUpdateOrder, btnCancelOrder, btnApproveOrder;
    private DatabaseReference databaseReference;
    private String orderCode;
    private String currentStatus;
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
        editTextTotal.setText(String.valueOf(order.getTongTien()));

        // Ô trạng thái chỉ để xem, đổi trạng thái bằng các nút bên dưới
        editTextOrderStatus.setKeyListener(null);
        editTextOrderStatus.setFocusable(false);
        currentStatus = order.getTrangThai();
        refreshStatusUi();

        // Lưu orderCode
        orderCode = order.getMaDonHang();
        databaseReference = FirebaseDatabase.getInstance().getReference("don_hang").child(orderCode);

        // Xử lý sự kiện
        btnUpdateOrder.setOnClickListener(v -> updateOrder());
        btnCancelOrder.setOnClickListener(v -> cancelOrder());
        btnApproveOrder.setOnClickListener(v -> advanceStatus());
        btnBack.setOnClickListener(v -> finish());
    }

    // ===== Luồng trạng thái =====

    private String getNextStatus(String status) {
        if (ST_PLACED.equals(status)) return ST_APPROVED;
        if (ST_APPROVED.equals(status)) return ST_SHIPPING;
        if (ST_SHIPPING.equals(status)) return ST_DELIVERED;
        return null; // Đã giao hàng, Đã hủy hoặc trạng thái lạ
    }

    private boolean canCancel(String status) {
        return ST_PLACED.equals(status) || ST_APPROVED.equals(status);
    }

    private String getNextActionLabel(String status) {
        if (ST_PLACED.equals(status)) return "Duyệt đơn";
        if (ST_APPROVED.equals(status)) return "Bắt đầu giao hàng";
        return "Xác nhận đã giao";
    }

    // Cập nhật ô trạng thái và hiện/ẩn các nút theo trạng thái hiện tại
    private void refreshStatusUi() {
        editTextOrderStatus.setText(currentStatus);

        if (getNextStatus(currentStatus) == null) {
            btnApproveOrder.setVisibility(View.GONE);
        } else {
            btnApproveOrder.setVisibility(View.VISIBLE);
            btnApproveOrder.setText(getNextActionLabel(currentStatus));
        }
        btnCancelOrder.setVisibility(canCancel(currentStatus) ? View.VISIBLE : View.GONE);
    }

    private void setBusy(boolean busy) {
        btnApproveOrder.setEnabled(!busy);
        btnCancelOrder.setEnabled(!busy);
    }

    // Chuyển sang trạng thái kế tiếp
    private void advanceStatus() {
        String expected = currentStatus;
        String next = getNextStatus(expected);
        if (next == null) {
            Toast.makeText(this, "Đơn hàng không thể chuyển tiếp", Toast.LENGTH_SHORT).show();
            return;
        }

        setBusy(true);
        databaseReference.child("trangThai").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String st = currentData.getValue(String.class);
                if (st == null) {
                    // Chưa có dữ liệu cục bộ: để Firebase chạy lại với giá trị thật
                    return Transaction.success(currentData);
                }
                if (!expected.equals(st)) {
                    return Transaction.abort(); // trạng thái đã bị đổi ở nơi khác
                }
                currentData.setValue(next);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                setBusy(false);
                if (error != null) {
                    Toast.makeText(AdminDonHangDetailActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                } else if (committed && snapshot != null && next.equals(snapshot.getValue(String.class))) {
                    currentStatus = next;
                    refreshStatusUi();
                    Toast.makeText(AdminDonHangDetailActivity.this, "Đã chuyển sang: " + next, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminDonHangDetailActivity.this, "Trạng thái đơn đã thay đổi, vui lòng mở lại đơn", Toast.LENGTH_SHORT).show();
                    if (snapshot != null && snapshot.getValue(String.class) != null) {
                        currentStatus = snapshot.getValue(String.class);
                        refreshStatusUi();
                    }
                }
            }
        });
    }

    // Hủy đơn (chỉ khi chưa giao), hủy xong thì cộng lại tồn kho
    private void cancelOrder() {
        if (!canCancel(currentStatus)) {
            Toast.makeText(this, "Chỉ hủy được đơn hàng chưa giao", Toast.LENGTH_SHORT).show();
            return;
        }

        setBusy(true);
        databaseReference.child("trangThai").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String st = currentData.getValue(String.class);
                if (st == null) {
                    return Transaction.success(currentData);
                }
                if (!canCancel(st)) {
                    return Transaction.abort();
                }
                currentData.setValue(ST_CANCELLED);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                setBusy(false);
                if (error != null) {
                    Toast.makeText(AdminDonHangDetailActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                } else if (committed && snapshot != null && ST_CANCELLED.equals(snapshot.getValue(String.class))) {
                    currentStatus = ST_CANCELLED;
                    refreshStatusUi();
                    Toast.makeText(AdminDonHangDetailActivity.this, "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                    restoreStock(); // chỉ chạy khi hủy thành công, và chỉ một lần
                } else {
                    Toast.makeText(AdminDonHangDetailActivity.this, "Không thể hủy: đơn đã giao hoặc đã hủy trước đó", Toast.LENGTH_SHORT).show();
                    if (snapshot != null && snapshot.getValue(String.class) != null) {
                        currentStatus = snapshot.getValue(String.class);
                        refreshStatusUi();
                    }
                }
            }
        });
    }

    // Đọc danh sách sản phẩm của đơn rồi cộng lại tồn kho từng sản phẩm
    private void restoreStock() {
        databaseReference.child("sanPham").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(this, "Đã hủy đơn nhưng chưa hoàn được kho, vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
                return;
            }
            for (DataSnapshot item : task.getResult().getChildren()) {
                String name = item.child("tenSanPham").getValue(String.class);
                Long qty = item.child("soLuong").getValue(Long.class);
                if (name == null || qty == null || qty <= 0) continue;
                addStockBack(name, qty);
            }
        });
    }

    private void addStockBack(String name, long qty) {
        FirebaseDatabase.getInstance().getReference("san_pham")
                .orderByChild("ten_san_pham").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return; // sản phẩm đã bị xóa
                        DatabaseReference stockRef = snapshot.getChildren().iterator().next()
                                .getRef().child("so_luong_ton_kho");
                        stockRef.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                Long stock = currentData.getValue(Long.class);
                                if (stock == null) {
                                    return Transaction.success(currentData);
                                }
                                currentData.setValue(stock + qty);
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminDonHangDetailActivity.this,
                                "Lỗi hoàn kho sản phẩm: " + name, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Cập nhật thông tin đơn (không còn sửa trạng thái ở đây)
    private void updateOrder() {
        String updatedCustomerName = editTextCustomerName.getText().toString().trim();
        String updatedAddress = editTextAddress.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();
        String updatedOrderDate = editTextOrderDate.getText().toString().trim();

        long updatedTotal;
        try {
            updatedTotal = Long.parseLong(editTextTotal.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Tổng tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updatedCustomerName.isEmpty() || updatedAddress.isEmpty() || updatedPhone.isEmpty() || updatedOrderDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("tenKhachHang", updatedCustomerName);
        updates.put("diaChi", updatedAddress);
        updates.put("soDienThoai", updatedPhone);
        updates.put("ngayDatHang", updatedOrderDate);
        updates.put("tongTien", updatedTotal);

        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}