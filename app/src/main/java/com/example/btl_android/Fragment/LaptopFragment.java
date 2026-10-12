package com.example.btl_android.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_android.Adapter.ProductAdapter;
import com.example.btl_android.CheckConn;
import com.example.btl_android.R;
import com.example.btl_android.Model.ProductItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LaptopFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<ProductItem> productList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laptop, container, false);

        // Khởi tạo RecyclerView cho danh sách sản phẩm
        recyclerViewProducts = view.findViewById(R.id.recyclerView);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Kiểm tra kết nối mạng và lấy dữ liệu
        if (CheckConn.haveNetworkConn(requireContext())) {
            fetchProductData();
        } else {
            CheckConn.showToast(requireContext(), "Không có kết nối mạng");
        }

        return view;
    }

    // Phương thức lấy dữ liệu sản phẩm từ Firebase
    private void fetchProductData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("san_pham");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu sản phẩm từ Firebase
                    String tenSanPham = snapshot.child("ten_san_pham").getValue(String.class);
                    Long gia = snapshot.child("gia").getValue(Long.class);
                    String hinhAnh = snapshot.child("hinh_anh").getValue(String.class);
                    String moTa = snapshot.child("mo_ta").getValue(String.class);
                    int soLuongTonKho = snapshot.child("so_luong_ton_kho").getValue(Integer.class);
                    String loai = snapshot.child("loai").getValue(String.class);

                    // Tạo đối tượng sản phẩm và set các thuộc tính
                    ProductItem product = new ProductItem();
                    product.setTenSanPham(tenSanPham);
                    product.setGia(gia);
                    product.setHinhAnh(hinhAnh);
                    product.setMoTa(moTa);
                    product.setSoLuongTonKho(soLuongTonKho);
                    product.setLoai(loai);

                    // Kiểm tra và thêm sản phẩm nếu loại là "Laptop"
                    if ("Laptop".equals(product.getLoai())) {
                        productList.add(product);
                    }
                }
                // Cập nhật adapter sau khi có dữ liệu
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CheckConn.showToast(requireContext(), "Lỗi: " + error.getMessage());
            }
        });
    }
}
