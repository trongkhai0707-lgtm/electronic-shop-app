package com.example.btl_android.Fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.btl_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class TonKhoFragment extends Fragment {

    TableLayout tableLayout;
    DatabaseReference databaseReference;

    public TonKhoFragment() {
        // Constructor mặc định
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_tonkho, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);

        // Khởi tạo tham chiếu Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("san_pham");

        // Lấy dữ liệu sản phẩm từ Firebase
        fetchProductData();

        return view;
    }

    private void fetchProductData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xóa bảng trước khi thêm hàng mới
                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String maSanPham = productSnapshot.child("ma_san_pham").getValue(String.class);
                    String tenSanPham = productSnapshot.child("ten_san_pham").getValue(String.class);
                    long giaSanPham = productSnapshot.child("gia").getValue(Long.class);
                    String soLuongTonKho = productSnapshot.child("so_luong_ton_kho").getValue(Long.class).toString();
                    String nhaCungCap = productSnapshot.child("nha_cung_cap").getValue(String.class);

                    // Xử lý giá tiền
                    String gia = new DecimalFormat("#,###").format(giaSanPham) + " VNĐ";

                    // Tạo hàng mới cho mỗi sản phẩm
                    TableRow row = new TableRow(getContext());

                    // Thêm cột vào hàng
                    addCellToRow(row, maSanPham, 1);
                    addCellToRow(row, tenSanPham, 2);
                    addCellToRow(row, gia, 1);
                    addCellToRow(row, soLuongTonKho, 1);
                    addCellToRow(row, nhaCungCap, 1);

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
