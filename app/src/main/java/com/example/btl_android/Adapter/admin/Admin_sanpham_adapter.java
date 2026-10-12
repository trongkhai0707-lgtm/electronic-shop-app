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
import com.example.btl_android.Model.admin.Admin_sanpham_item;

import java.util.ArrayList;
import java.util.List;

public class Admin_sanpham_adapter extends RecyclerView.Adapter<Admin_sanpham_adapter.ViewHolder> implements Filterable {

    private List<Admin_sanpham_item> sanPhamList;
    private List<Admin_sanpham_item> sanPhamListFiltered;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Admin_sanpham_item sanPham);
    }

    public Admin_sanpham_adapter(List<Admin_sanpham_item> sanPhamList, Context context) {
        this.sanPhamList = sanPhamList != null ? sanPhamList : new ArrayList<>();
        this.sanPhamListFiltered = new ArrayList<>(this.sanPhamList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_sanpham_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Admin_sanpham_item sanPham = sanPhamListFiltered.get(position);

        holder.tvProductName.setText(sanPham.getTenSanPham());
        holder.tvProductPrice.setText("Giá: " + sanPham.getGia() + " VNĐ");
        holder.tvProductCategory.setText("Loại: " + sanPham.getLoai());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(sanPham);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sanPhamListFiltered != null ? sanPhamListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    sanPhamListFiltered = new ArrayList<>(sanPhamList);
                } else {
                    List<Admin_sanpham_item> filteredList = new ArrayList<>();
                    for (Admin_sanpham_item item : sanPhamList) {
                        if (item.getTenSanPham().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    sanPhamListFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = sanPhamListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sanPhamListFiltered = (List<Admin_sanpham_item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
        }
    }
}
