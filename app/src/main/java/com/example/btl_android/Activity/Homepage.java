package com.example.btl_android.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.btl_android.Adapter.ProductAdapter;
import com.example.btl_android.R;
import com.example.btl_android.Adapter.ViewPagerAdapter;
import com.example.btl_android.Model.ProductItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ViewPager viewPager;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProductAdapter productAdapter;
    Toolbar toolbar;
    FrameLayout cartContainer;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("taikhoan");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        requestNotificationPermission();

        GetTokenDevice();

        viewPager = findViewById(R.id.view_pager);
        recyclerView = findViewById(R.id.recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        cartContainer = findViewById(R.id.cart_container);

        setSupportActionBar(toolbar);

        ShowInfor();

        ShowViewPager();
        ShowRecyclerView();

        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        CheckCart();

        cartContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, Cart.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    private void ShowInfor() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");

        dbRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ten = snapshot.child("username").getValue(String.class);
                String anh = snapshot.child("avatarUrl").getValue(String.class);

                // Hiển thị thông tin lên giao diện
                View headerView = navigationView.getHeaderView(0);
                ImageView avatar = headerView.findViewById(R.id.avatar);
                TextView txt_name = headerView.findViewById(R.id.txt_name);

                txt_name.setText(ten);
                if (anh != null) {
                    Glide.with(Homepage.this)
                            .load(anh)
                            .circleCrop()
                            .into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.nav_homepage).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.nav_phone).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.nav_laptop).setChecked(true);
                        break;
                    case 3:
                        navigationView.getMenu().findItem(R.id.nav_pc).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void ShowRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        productAdapter = new ProductAdapter(this, GetListProduct());
        recyclerView.setAdapter(productAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private List<ProductItem> GetListProduct() {
        List<ProductItem> list = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("san_pham");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
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

                    // Thêm sản phẩm vào danh sách
                    list.add(product);
                }
                // Cập nhật adapter sau khi có dữ liệu
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        // Set up SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý tìm kiếm khi nhấn Enter
                productAdapter.getFilter().filter(query);
                // Bạn có thể thực hiện hành động tìm kiếm thực tế ở đây
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng nhập văn bản vào ô tìm kiếm
                if (newText.isEmpty()) {
                    viewPager.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    viewPager.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    productAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    private void CheckCart() {
        // Kiểm tra số lượng sản phẩm trong giỏ hàng
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(uid);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartItemCount = (int) snapshot.getChildrenCount();

                TextView cartBadge = findViewById(R.id.cart_badge);

                if (cartItemCount > 0) {
                    cartBadge.setText(String.valueOf(cartItemCount));
                    cartBadge.setVisibility(View.VISIBLE);
                } else {
                    cartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_homepage) {
            viewPager.setCurrentItem(0);
        }
        if (item.getItemId() == R.id.nav_phone) {
            viewPager.setCurrentItem(1);
        }
        if (item.getItemId() == R.id.nav_laptop) {
            viewPager.setCurrentItem(2);
        }
        if (item.getItemId() == R.id.nav_pc) {
            viewPager.setCurrentItem(3);
        }
        if (item.getItemId() == R.id.nav_order) {
            Intent intent = new Intent(this, DonHang.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_setting) {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Quyền bị từ chối
                // Thông báo cho người dùng và hướng dẫn họ cấp quyền
                new AlertDialog.Builder(this)
                        .setTitle("Quyền bị từ chối")
                        .setMessage("Để gửi thông báo, bạn cần cấp quyền cho ứng dụng. Vui lòng vào cài đặt để cấp quyền.")
                        .setPositiveButton("Cài đặt", (dialog, which) -> {
                            // Mở cài đặt ứng dụng để người dùng cấp quyền
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        }
    }

    private void GetTokenDevice() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    Log.d("TokenDevice", "GetTokenDevice: " + token);
                });
    }


}
