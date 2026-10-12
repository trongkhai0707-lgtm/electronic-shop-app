package com.example.btl_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Activity.OrderDetailActivity;
import com.example.btl_android.R;
import com.example.btl_android.Model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderItem> orderList;

    public OrderAdapter(Context context, List<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.tvOrderNumber.setText("Đơn hàng " + (position + 1));
        holder.tvTotalAmount.setText("Tổng tiền: " + order.getTongTien() + " VND");
        holder.tvOrderDate.setText("Ngày đặt hàng: " + order.getNgayDatHang());

        // Hiển thị trạng thái đơn hàng
        holder.tvOrderStatus.setText("Trạng thái: " + order.getTrangThai());

        holder.itemView.setOnClickListener(v -> {
            // Chuyển sang Activity chi tiết đơn hàng
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getMaDonHang());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvTotalAmount, tvOrderDate, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);  // Thêm trường này
        }
    }
}
