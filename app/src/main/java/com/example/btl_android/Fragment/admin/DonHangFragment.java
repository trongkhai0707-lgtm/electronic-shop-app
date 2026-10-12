package com.example.btl_android.Fragment.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.btl_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;


public class DonHangFragment extends Fragment {

    TableLayout tableLayout;
    DatabaseReference databaseReference;

    public DonHangFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_donhang, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);

        // Khởi tạo tham chiếu Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("don_hang");

        // Lấy dữ liệu sản phẩm từ Firebase
        fetchOrderData();

        return view;
    }

    private void fetchOrderData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xóa bảng trước khi thêm hàng mới
                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String maSanPham = productSnapshot.child("maDonHang").getValue(String.class);
                    String ngayDatHang = productSnapshot.child("ngayDatHang").getValue(String.class);
                    long tongTien = productSnapshot.child("tongTien").getValue(Long.class);
                    String tenKhachHang = productSnapshot.child("tenKhachHang").getValue(String.class);
                    String trangThai = productSnapshot.child("trangThai").getValue(String.class);

                    // Xử lý giá tiền
                    String gia = new DecimalFormat("#,###").format(tongTien) + " VNĐ";

                    // Tạo hàng mới cho mỗi sản phẩm
                    TableRow row = new TableRow(getContext());

                    // Thêm cột vào hàng
                    addCellToRow(row, maSanPham, 1);
                    addCellToRow(row, tenKhachHang, 2);
                    addCellToRow(row, gia, 1);
                    addCellToRow(row, ngayDatHang, 1);
                    addCellToRow(row, trangThai, 1);

                    // Thêm hàng vào bảng
                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    private void addCellToRow(TableRow row, String text, int weight) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setMinWidth(50);
        textView.setMaxWidth(200);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END); // Thêm dấu "..." nếu quá dài
        row.addView(textView);
    }
}