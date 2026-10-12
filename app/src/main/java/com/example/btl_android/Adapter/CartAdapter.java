package com.example.btl_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.Model.CartItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CartAdapter extends FirebaseRecyclerAdapter<CartItem, CartAdapter.CartViewHolder> {

    private Context context;

    public CartAdapter(@NonNull FirebaseRecyclerOptions<CartItem> options, Context context) {
        super(options);
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartItem model) {
        // Hiển thị thông tin sản phẩm trong giỏ hàng
        holder.productName.setText(model.getName());
        holder.productPrice.setText(String.valueOf(model.getPrice()));
        holder.productQuantity.setText(String.valueOf(model.getQuantity()));

        // Hiển thị hình ảnh sản phẩm (sử dụng URL từ Realtime Database)
        Glide.with(context).load(model.getImageUrl()).into(holder.productImage);

        // Cập nhật số lượng sản phẩm khi nhấn nút tăng
        holder.increaseButton.setOnClickListener(v -> {
            updateQuantity(position, model, model.getQuantity() + 1);
        });

        // Giảm số lượng sản phẩm nếu số lượng lớn hơn 1
        holder.decreaseButton.setOnClickListener(v -> {
            if (model.getQuantity() > 1) {
                updateQuantity(position, model, model.getQuantity() - 1);
            }
        });

        // Xử lý nút xóa sản phẩm
        holder.removeButton.setOnClickListener(v -> {
            // Gọi hàm xóa sản phẩm
            removeItem(position);
        });
    }

    private void removeItem(int position) {
        // Lấy tham chiếu tới phần tử ở vị trí được chỉ định và xóa nó
        getSnapshots().getSnapshot(position).getRef().removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức cập nhật số lượng sản phẩm trong giỏ hàng
    private void updateQuantity(int position, CartItem model, int newQuantity) {
        model.setQuantity(newQuantity);
        getRef(position).setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Lỗi khi cập nhật số lượng sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        Button removeButton, increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            removeButton = itemView.findViewById(R.id.removeButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
        }
    }
}
