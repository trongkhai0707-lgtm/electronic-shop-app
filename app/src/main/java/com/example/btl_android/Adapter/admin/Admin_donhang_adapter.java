// Admin_donhang_adapter.java
package com.example.btl_android.Adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_donhang_item;

import java.util.ArrayList;
import java.util.List;

public class Admin_donhang_adapter extends RecyclerView.Adapter<Admin_donhang_adapter.ViewHolder> implements Filterable {

    private List<Admin_donhang_item> donHangList;
    private List<Admin_donhang_item> donHangListFiltered;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Admin_donhang_item donHang);
    }

    public Admin_donhang_adapter(List<Admin_donhang_item> donHangList, Context context) {
        this.donHangList = donHangList != null ? donHangList : new ArrayList<>();
        this.donHangListFiltered = new ArrayList<>(this.donHangList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_donhang_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Admin_donhang_item donHang = donHangListFiltered.get(position);

        holder.tvOrderCode.setText("Mã ĐH: " + donHang.getMaDonHang());
        holder.tvCustomerName.setText("Khách hàng: " + donHang.getTenKhachHang());
        holder.tvOrderTotal.setText("Tổng tiền: " + donHang.getTongTien() + " VND");
        holder.tvOrderDate.setText("Ngày đặt: " + donHang.getNgayDatHang());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(donHang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donHangListFiltered != null ? donHangListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    donHangListFiltered = new ArrayList<>(donHangList);
                } else {
                    List<Admin_donhang_item> filteredList = new ArrayList<>();
                    for (Admin_donhang_item item : donHangList) {
                        if (item.getTenKhachHang().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    donHangListFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = donHangListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                donHangListFiltered = (List<Admin_donhang_item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvCustomerName, tvOrderTotal, tvOrderDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
        }
    }
}
