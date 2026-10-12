package com.example.btl_android.Adapter.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Activity.admin.taikhoan.AdminAccountDetailActivity;
import com.example.btl_android.R;
import com.example.btl_android.Model.admin.Admin_taikhoan_item;

import java.util.ArrayList;
import java.util.List;

public class Admin_taikhoan_adapter extends RecyclerView.Adapter<Admin_taikhoan_adapter.ViewHolder> implements Filterable {

    private List<Admin_taikhoan_item> taiKhoanList;
    private List<Admin_taikhoan_item> taiKhoanListFiltered;
    private Context context;

    public Admin_taikhoan_adapter(List<Admin_taikhoan_item> taiKhoanList, Context context) {
        this.taiKhoanList = taiKhoanList;
        this.taiKhoanListFiltered = new ArrayList<>(taiKhoanList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_taikhoan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Admin_taikhoan_item taiKhoan = taiKhoanListFiltered.get(position);
        holder.tvName.setText("name: " + taiKhoan.getUsername());
        holder.tvEmail.setText("email: " + taiKhoan.getEmail());
        holder.tvRole.setText("role: " + taiKhoan.getRole());

        // Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent để mở AccountDetailActivity
            Intent intent = new Intent(context, AdminAccountDetailActivity.class);
            // Truyền dữ liệu thông qua intent
            intent.putExtra("username", taiKhoan.getUsername());
            intent.putExtra("email", taiKhoan.getEmail());
            // Chuyển Long thành String để truyền qua Intent
            intent.putExtra("phone", taiKhoan.getSdt());
            intent.putExtra("address", taiKhoan.getDiachi());
            intent.putExtra("role", taiKhoan.getRole());
            intent.putExtra("uid", taiKhoan.getUid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taiKhoanListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    taiKhoanListFiltered = new ArrayList<>(taiKhoanList);
                } else {
                    List<Admin_taikhoan_item> filteredList = new ArrayList<>();
                    for (Admin_taikhoan_item item : taiKhoanList) {
                        if (item.getUsername().toLowerCase().contains(filterPattern) ||
                                item.getEmail().toLowerCase().contains(filterPattern) ||
                                item.getSdt().toLowerCase().contains(filterPattern) ||
                                item.getDiachi().toLowerCase().contains(filterPattern) ||
                                item.getRole().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    taiKhoanListFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = taiKhoanListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                taiKhoanListFiltered = (List<Admin_taikhoan_item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
        }
    }
}
