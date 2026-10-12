package com.example.btl_android.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.Activity.ProductDetailActivity;
import com.example.btl_android.Model.SanPham;
import com.example.btl_android.R;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ItemHolder> {

    Context context;
    ArrayList<SanPham> mangSanPham;

    public NewProductAdapter(Context context, ArrayList<SanPham> mangSanPham) {
        this.context = context;
        this.mangSanPham = mangSanPham;
    }

    @androidx.annotation.NonNull
    @Override
    public NewProductAdapter.ItemHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newproduct, null);
        return new NewProductAdapter.ItemHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewProductAdapter.ItemHolder holder, int position) {
        SanPham sanPham = mangSanPham.get(position);
        holder.nameSP.setText(sanPham.getTen_san_pham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.priceSP.setText("Giá: " + decimalFormat.format(sanPham.getGia()) + "đ");

        holder.quantity.setText("Kho: " + sanPham.getSo_luong_ton_kho());

        Glide.with(context).load(sanPham.getHinh_anh())
                .placeholder(R.drawable.ic_imagnot)
                .error(R.drawable.ic_imageerror)
                .into(holder.hinhSP);

        // Xử lý khi người dùng nhấn vào sản phẩm
        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // Truyền dữ liệu sản phẩm qua Intent
            intent.putExtra("ten_san_pham", sanPham.getTen_san_pham());
            intent.putExtra("gia", sanPham.getGia());
            intent.putExtra("hinh_anh", sanPham.getHinh_anh());
            intent.putExtra("mo_ta", sanPham.getMo_ta());
            intent.putExtra("so_luong_ton_kho", sanPham.getSo_luong_ton_kho());
            intent.putExtra("loai_san_pham", sanPham.getLoai());
            Log.d("DetailProduct", sanPham.getTen_san_pham() + sanPham.getGia() + sanPham.getHinh_anh() + sanPham.getMo_ta() + sanPham.getSo_luong_ton_kho() + sanPham.getLoai());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {return mangSanPham.size();}

    public static class ItemHolder extends RecyclerView.ViewHolder {
        public ImageView hinhSP;
        public TextView nameSP, priceSP, quantity;
        public Button btnDetail;

        public ItemHolder(@android.support.annotation.NonNull View itemView) {
            super(itemView);
            hinhSP = itemView.findViewById(R.id.iv_sanpham);
            nameSP = itemView.findViewById(R.id.tv_name);
            priceSP = itemView.findViewById(R.id.tv_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }
}
