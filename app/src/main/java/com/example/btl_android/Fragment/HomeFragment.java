package com.example.btl_android.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.example.btl_android.Adapter.NewProductAdapter;
import com.example.btl_android.CheckConn;
import com.example.btl_android.Model.SanPham;
import com.example.btl_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<String> imageAdvertise;
    ViewFlipper viewFlipper;
    RecyclerView recyclerView;
    ArrayList<SanPham> mangSanPham;
    NewProductAdapter adapter;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("Adv");

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("san_pham");

    private ValueEventListener valueEventListener; // Khai báo listener để hủy đúng cách

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewFlipper = view.findViewById(R.id.viewFlipper);
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        mangSanPham = new ArrayList<>();
        adapter = new NewProductAdapter(requireContext(), mangSanPham);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        if (CheckConn.haveNetworkConn(requireContext())) {
            ActionViewFlipper();
            NewProduct();
        } else {
            CheckConn.showToast(requireContext(), "Không có kết nối mạng");
        }

        return view;
    }

    private void ActionViewFlipper() {
        imageAdvertise = new ArrayList<>();
        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        // Lấy URL của mỗi ảnh
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            imageAdvertise.add(imageUrl);

                            if (isAdded()) { // Kiểm tra Fragment còn gắn với Context
                                ImageView imageView = new ImageView(requireContext());
                                Picasso.get().load(imageUrl).into(imageView);
                                // Thay đổi scaleType để tránh méo ảnh
                                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                viewFlipper.addView(imageView);
                            }
                        });
                    }
                });
        if (isAdded()) { // Kiểm tra trước khi thiết lập animation
            viewFlipper.setFlipInterval(5000);
            viewFlipper.setAutoStart(true);
            Animation animationIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right);
            Animation animationOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right);
            viewFlipper.setInAnimation(animationIn);
            viewFlipper.setOutAnimation(animationOut);
        }
    }

    private void NewProduct() {
        valueEventListener = dbRef.orderByKey().limitToLast(10).addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mangSanPham.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ten = snapshot.child("ten_san_pham").getValue(String.class);
                    Integer gia = snapshot.child("gia").getValue(Integer.class);
                    String anh = snapshot.child("hinh_anh").getValue(String.class);
                    String loai = snapshot.child("loai").getValue(String.class);
                    Integer soLuong = snapshot.child("so_luong_ton_kho").getValue(Integer.class);
                    String moTa = snapshot.child("mo_ta").getValue(String.class);
                    mangSanPham.add(new SanPham(ten, gia, anh, loai, soLuong, moTa));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) { // Kiểm tra Fragment còn gắn với Activity
                    CheckConn.showToast(requireContext(), "Lỗi: " + error.getMessage());
                } else {
                    Log.w("HomeFragment", "Fragment not attached to context. Error: " + error.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbRef != null && valueEventListener != null) {
            dbRef.removeEventListener(valueEventListener); // Hủy listener Firebase
        }
    }
}
