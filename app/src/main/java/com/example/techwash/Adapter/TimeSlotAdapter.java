package com.example.techwash.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Model.TimeSlot;
import com.example.techwash.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlotList;
    private Context context;

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeslot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlotList.get(position);

        // Gán dữ liệu vào các view
        holder.time.setText(timeSlot.getStartTime() + " - " + timeSlot.getEndTime());
        holder.status.setText(timeSlot.getStatus().name());


        if (timeSlot.getStatus() == TimeSlot.SlotStatus.BOOKED) {
            holder.btnDatLich.setEnabled(false); // Vô hiệu hóa nút nếu đã được đặt
            holder.btnDatLich.setText("Đã Đặt");
            holder.status.setText("BOOKED");
        } else {
            holder.btnDatLich.setEnabled(true); // Cho phép đặt lịch nếu AVAILABLE
        }


            holder.btnDatLich.setOnClickListener(v -> {
                updateSlotStatus(timeSlot.getId(), TimeSlot.SlotStatus.BOOKED);  // Truyền slotId chính xác
                Log.e("TimeSlotAdapter", timeSlot.getId());
            });


    }

    private void updateSlotStatus(String slotId, TimeSlot.SlotStatus newStatus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TimeSlots")
                .document(slotId) // Cập nhật slot theo ID
                .update("status", newStatus.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại item trên danh sách hiện tại của RecyclerView
                    for (TimeSlot slot : timeSlotList) {
                        if (slot.getId().equals(slotId)) {
                            slot.setStatus(newStatus); // Cập nhật trạng thái của slot
                            break;
                        }
                    }
                    notifyDataSetChanged(); // Refresh lại RecyclerView
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi đặt lịch!", Toast.LENGTH_SHORT).show();
                });
    }




    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView time, status;
        Button btnDatLich;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            btnDatLich = itemView.findViewById(R.id.btn_datlich);
        }
    }
}
