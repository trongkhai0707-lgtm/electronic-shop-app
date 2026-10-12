package com.example.btl_android.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.btl_android.Activity.ProductDetailActivity;
import com.example.btl_android.R;
import com.example.btl_android.Model.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    Context context;
    List<ProductItem> productList;
    List<ProductItem> productListOld;

    // Constructor
    public ProductAdapter(Context context, List<ProductItem> productList) {
        this.context = context;
        this.productList = productList;
        this.productListOld = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem product = productList.get(position);
        holder.textViewName.setText(product.getTenSanPham());
        holder.textViewPrice.setText(String.format("%,dđ", product.getGia())); // Định dạng giá

        // Hiển thị số lượng tồn kho
        holder.textViewQuantity.setText(String.format("Kho: %d", product.getSoLuongTonKho()));

        // Sử dụng Glide để tải ảnh
        Glide.with(context)
                .load(product.getHinhAnh())
                .override(400, 400)
                .fitCenter()
                .into(holder.imageViewProduct);

        // Xử lý khi người dùng nhấn vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // Truyền dữ liệu sản phẩm qua Intent
            intent.putExtra("ten_san_pham", product.getTenSanPham());
            intent.putExtra("gia", product.getGia());
            intent.putExtra("hinh_anh", product.getHinhAnh());
            intent.putExtra("mo_ta", product.getMoTa());
            intent.putExtra("so_luong_ton_kho", product.getSoLuongTonKho());
            intent.putExtra("loai_san_pham", product.getLoai());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString().toLowerCase();

                if (searchText.isEmpty()) {
                    productList = productListOld;
                } else {
                    List<ProductItem> listS = new ArrayList<>();
                    for (ProductItem product : productListOld) {
                        if (product.getTenSanPham().toLowerCase().contains(searchText)) {
                            listS.add(product);
                        }
                    }

                    productList = listS;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productList;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productList = (List<ProductItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewName, textViewPrice, textViewQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.iv_product_image);
            textViewName = itemView.findViewById(R.id.tv_product_name);
            textViewPrice = itemView.findViewById(R.id.tv_product_price);
            textViewQuantity = itemView.findViewById(R.id.tv_product_quantity);
        }
    }

}
