package com.example.btl_android.Fragment.admin;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.DatePickerDialog;

import com.example.btl_android.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DanhThuFragment extends Fragment {

    LineChart lineChart;
    ScrollView scrollView;
    LinearLayout revenueListLayout;
    Button btnFrom, btnTo;
    Calendar fromDateCalendar, toDateCalendar;
    SimpleDateFormat dateFormat;

    public DanhThuFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_danhthu, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        scrollView = view.findViewById(R.id.scrollView);
        revenueListLayout = view.findViewById(R.id.revenueListLayout);
        btnFrom = view.findViewById(R.id.btnFromDate);
        btnTo = view.findViewById(R.id.btnToDate);

        fromDateCalendar = Calendar.getInstance();
        toDateCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Thiết lập ngày mặc định
        toDateCalendar.setTime(new Date());
        fromDateCalendar.add(Calendar.MONTH, -1);

        btnTo.setText(dateFormat.format(toDateCalendar.getTime()));
        btnFrom.setText(dateFormat.format(fromDateCalendar.getTime()));

        // Gọi hàm để lấy dữ liệu mặc định
        generateRevenueData(fromDateCalendar.getTime(), toDateCalendar.getTime());

        // Cài đặt sự kiện click để chọn ngày
        btnFrom.setOnClickListener(v -> showDatePicker(fromDateCalendar, btnFrom));
        btnTo.setOnClickListener(v -> showDatePicker(toDateCalendar, btnTo));

        return view;
    }

    // Hiển thị DatePicker để chọn ngày
    private void showDatePicker(final Calendar calendar, final Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            Date selectedDate = calendar.getTime();
            Date today = new Date();

            if (selectedDate.after(today)) {
                calendar.setTime(today);
            }

            button.setText(dateFormat.format(calendar.getTime()));
            generateRevenueData(fromDateCalendar.getTime(), toDateCalendar.getTime());
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Lớp lưu trữ dữ liệu doanh thu
    static class RevenueEntry {
        Date date;
        long revenue;

        public RevenueEntry(Date date, long revenue) {
            this.date = date;
            this.revenue = revenue;
        }

        public Date getDate() {
            return date;
        }

        public long getRevenue() {
            return revenue;
        }
    }

    // Hàm lấy dữ liệu từ Firebase và lọc theo khoảng thời gian
    private void generateRevenueData(Date fromDate, Date toDate) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("don_hang");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RevenueEntry> revenueEntries = new ArrayList<>();

                for (DataSnapshot donHangSnapshot : snapshot.getChildren()) {
                    String ngayDatHangStr = donHangSnapshot.child("ngayDatHang").getValue(String.class);
                    Long tongTien = donHangSnapshot.child("tongTien").getValue(Long.class);

                    try {
                        Date ngayDatHang = dateFormat.parse(ngayDatHangStr);
                        if (ngayDatHang != null && ngayDatHang.compareTo(fromDate) >= 0 && ngayDatHang.compareTo(toDate) <= 0) {
                            if (tongTien != null) {
                                revenueEntries.add(new RevenueEntry(ngayDatHang, tongTien));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                // Sắp xếp theo ngày
                revenueEntries.sort((entry1, entry2) -> entry2.getDate().compareTo(entry1.getDate()));

                // Cập nhật giao diện
                updateRevenueUI(revenueEntries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    // Cập nhật giao diện cho ScrollView và biểu đồ
    private void updateRevenueUI(List<RevenueEntry> revenueEntries) {
        List<Entry> chartEntries = new ArrayList<>();
        revenueListLayout.removeAllViews();  // Xóa các view cũ

        Date today = new Date();
        int index = 0;

        // Sắp xếp lại revenueEntries từ trước đến nay
        revenueEntries.sort((entry1, entry2) -> entry1.getDate().compareTo(entry2.getDate()));

        // Duyệt qua các mục doanh thu
        for (RevenueEntry revenueEntry : revenueEntries) {
            String formattedRevenue = new DecimalFormat("#,###").format(revenueEntry.getRevenue()) + " VNĐ";
            String dateStr = dateFormat.format(revenueEntry.getDate());

            // Thêm dữ liệu vào biểu đồ
            chartEntries.add(new Entry(index++, revenueEntry.getRevenue()));

            // Chỉ thêm mục vào ScrollView nếu ngày <= ngày hiện tại
            if (revenueEntry.getDate().compareTo(today) <= 0) {
                // Tạo một hàng cho mỗi mục trong ScrollView
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                // TextView hiển thị ngày
                TextView dateTextView = new TextView(getContext());
                dateTextView.setText(dateStr);
                dateTextView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                dateTextView.setGravity(Gravity.CENTER);

                // TextView hiển thị doanh thu
                TextView revenueTextView = new TextView(getContext());
                revenueTextView.setText(formattedRevenue);
                revenueTextView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                revenueTextView.setGravity(Gravity.CENTER);

                // Thêm TextView vào rowLayout
                rowLayout.addView(dateTextView);
                rowLayout.addView(revenueTextView);

                // Thêm rowLayout vào revenueListLayout
                revenueListLayout.addView(rowLayout);
            }
        }

        // Cập nhật biểu đồ
        LineDataSet dataSet = new LineDataSet(chartEntries, "Doanh thu");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        // Tạo lớp ValueFormatter tùy chỉnh cho trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int intValue = (int) value; // Ép kiểu float thành int
                if (intValue >= 0 && intValue < revenueEntries.size()) {
                    return dateFormat.format(revenueEntries.get(intValue).getDate());
                }
                return "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Đảm bảo trục X không bị đảo ngược
        xAxis.setGranularity(1f); // Đảm bảo các giá trị trên trục X được cách đều
        xAxis.setGranularityEnabled(true);

        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
    }

}
