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

import com.example.techwash.Model.Auto;
import com.example.techwash.Model.Booked;
import com.example.techwash.Model.TimeSlot;
import com.example.techwash.Notification.NotificationService;
import com.example.techwash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            // Ẩn nút nếu đã được đặt
            holder.btnDatLich.setVisibility(View.GONE);
            holder.trangthai.setText("Lich Đã Bị Đặt"); // Hiển thị trạng thái "Đã Đặt"
        } else {
            // Hiển thị nút nếu slot còn trống
            holder.btnDatLich.setVisibility(View.VISIBLE);
            holder.btnDatLich.setText("Đặt Lịch");
            holder.trangthai.setText("");
            holder.btnDatLich.setEnabled(true); // Cho phép đặt lịch

            // Thiết lập sự kiện click cho nút
            holder.btnDatLich.setOnClickListener(v -> {
                updateSlotStatus(timeSlot.getId(), TimeSlot.SlotStatus.BOOKED); // Cập nhật trạng thái
            });
        }
    }

    private void updateSlotStatus(String slotId, TimeSlot.SlotStatus newStatus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cập nhật trạng thái của TimeSlot
        db.collection("TimeSlots")
                .document(slotId)
                .update("status", newStatus.toString())
                .addOnSuccessListener(aVoid -> {
                    // Sau khi cập nhật thành công, thêm vào bảng 'Booked'
                    createBookedRecord(slotId);

                    Toast.makeText(context, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật trạng thái trên danh sách RecyclerView
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

    // Hàm tạo bản ghi mới trong bảng 'Booked'
    private void createBookedRecord(String slotId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser(); // Lấy thông tin người dùng hiện tại

        if (user == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để đặt lịch!", Toast.LENGTH_SHORT).show();
            return; // Ngăn không cho tiếp tục nếu chưa đăng nhập
        }

        String userId = user.getUid(); // Lấy userId từ Firebase Authentication
        TimeSlot timeSlot = getTimeSlotById(slotId); // Tìm slot theo ID
        NotificationService.saveFCMToken(userId);
        if (timeSlot == null) {
            Toast.makeText(context, "Không tìm thấy lịch!", Toast.LENGTH_SHORT).show();
            return;
        }
        NotificationService.sendNotification(timeSlot.getUserId());
        // Tạo đối tượng Booked với thông tin cần thiết
        Booked booked = new Booked(timeSlot.getAutoId(), userId, slotId);

        // Lưu bản ghi vào Firestore
        db.collection("Booked")
                .add(booked)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Lịch đặt đã được lưu với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu lịch đặt", e);
                });
    }

    // Hàm tìm kiếm TimeSlot theo ID
    private TimeSlot getTimeSlotById(String slotId) {
        for (TimeSlot slot : timeSlotList) {
            if (slot.getId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView time, status, trangthai;
        Button btnDatLich;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            btnDatLich = itemView.findViewById(R.id.btn_datlich);
            trangthai = itemView.findViewById(R.id.trangthai);
        }
    }
}
