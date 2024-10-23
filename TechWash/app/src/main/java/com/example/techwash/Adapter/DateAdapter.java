package com.example.techwash.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<String> dateList;
    private OnDateClickListener listener;
    private int selectedPosition = -1; // Vị trí được chọn mặc định

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public DateAdapter(List<String> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
        // Khởi tạo vị trí của ngày hôm nay
        setTodayAsSelected();
    }

    // Tìm và đặt ngày hôm nay làm ngày được chọn
    private void setTodayAsSelected() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        for (int i = 0; i < dateList.size(); i++) {
            if (dateList.get(i).equals(today)) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String date = dateList.get(position);

        // Định dạng ngày cho giao diện người dùng
        String displayDate = formatDisplayDate(date);
        holder.dateTextView.setText(displayDate);

        // Highlight ngày được chọn
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.rounded_date_background_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.rounded_date_background);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position; // Cập nhật vị trí đã chọn
            notifyDataSetChanged(); // Cập nhật RecyclerView để làm mới giao diện
            listener.onDateClick(date); // Gọi callback khi chọn ngày
        });
    }

    // Chuyển đổi định dạng ngày để hiển thị cho người dùng
    private String formatDisplayDate(String date) {
        try {
            SimpleDateFormat firestoreFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM - EEE", Locale.getDefault());
            Date parsedDate = firestoreFormat.parse(date);
            return displayFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date; // Nếu xảy ra lỗi, trả về ngày ban đầu
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text);
        }
    }
}
