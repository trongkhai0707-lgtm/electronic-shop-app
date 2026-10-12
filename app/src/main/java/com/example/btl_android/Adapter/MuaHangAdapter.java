package com.example.btl_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.Model.CartItem;

import java.util.List;

public class MuaHangAdapter extends RecyclerView.Adapter<MuaHangAdapter.MuaHangViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public MuaHangAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MuaHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new MuaHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuaHangViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        // Kiểm tra null
        if (cartItem != null) {
            if (holder.tvProductName != null) {
                holder.tvProductName.setText(cartItem.getName());
            }
            if (holder.tvProductPrice != null) {
                holder.tvProductPrice.setText(String.valueOf(cartItem.getPrice()));
            }
            if (holder.tvProductQuantity != null) {
                holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
            }
            if (holder.ivProductImage != null) {
                Glide.with(context)
                        .load(cartItem.getImageUrl())
                        .placeholder(R.drawable.avt)
                        .error(R.drawable.load)
                        .into(holder.ivProductImage);
            }
        } else {

        }
    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class MuaHangViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductPrice, tvProductQuantity;
        ImageView ivProductImage;

        public MuaHangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);

            // Kiểm tra các view có được ánh xạ không
            if (tvProductName == null || tvProductPrice == null || tvProductQuantity == null || ivProductImage == null) {

            }
        }
    }
}
