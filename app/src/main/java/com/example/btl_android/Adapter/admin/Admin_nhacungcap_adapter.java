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
import com.example.btl_android.Model.admin.Admin_nhacungcap_item;

import java.util.ArrayList;
import java.util.List;

public class Admin_nhacungcap_adapter extends RecyclerView.Adapter<Admin_nhacungcap_adapter.ViewHolder> implements Filterable {

    private List<Admin_nhacungcap_item> nhaCungCapList;
    private List<Admin_nhacungcap_item> nhaCungCapListFiltered;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Admin_nhacungcap_item nhaCungCap);
    }

    public Admin_nhacungcap_adapter(List<Admin_nhacungcap_item> nhaCungCapList, Context context) {
        this.nhaCungCapList = nhaCungCapList != null ? nhaCungCapList : new ArrayList<>();
        this.nhaCungCapListFiltered = new ArrayList<>(this.nhaCungCapList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_nhacungcap_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Admin_nhacungcap_item nhaCungCap = nhaCungCapListFiltered.get(position);

        holder.tvSupplierName.setText(nhaCungCap.getTenNhaCungCap());
        holder.tvSupplierPhone.setText("Số điện thoại: " + nhaCungCap.getSoDienThoai());
        holder.tvSupplierEmail.setText("Email: " + nhaCungCap.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(nhaCungCap); // Đảm bảo truyền đúng đối tượng
            }
        });
    }

    @Override
    public int getItemCount() {
        return nhaCungCapListFiltered != null ? nhaCungCapListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    nhaCungCapListFiltered = new ArrayList<>(nhaCungCapList);
                } else {
                    List<Admin_nhacungcap_item> filteredList = new ArrayList<>();
                    for (Admin_nhacungcap_item item : nhaCungCapList) {
                        if (item.getTenNhaCungCap().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    nhaCungCapListFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = nhaCungCapListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                nhaCungCapListFiltered = (List<Admin_nhacungcap_item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSupplierName, tvSupplierPhone, tvSupplierEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSupplierName = itemView.findViewById(R.id.tv_supplier_name);
            tvSupplierPhone = itemView.findViewById(R.id.tv_supplier_phone);
            tvSupplierEmail = itemView.findViewById(R.id.tv_supplier_email);
        }
    }
}
