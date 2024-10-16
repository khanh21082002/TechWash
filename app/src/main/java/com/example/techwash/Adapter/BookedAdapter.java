package com.example.techwash.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Model.Booked;
import com.example.techwash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookedAdapter extends RecyclerView.Adapter<BookedAdapter.BookedViewHolder> {

    private List<Booked> bookedList;
    private Context context;
    private FirebaseFirestore db;

    public BookedAdapter(Context context, List<Booked> bookedList) {
        this.context = context;
        this.bookedList = new ArrayList<>();
        this.db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
        loadBooked(); // Tải dữ liệu từ Firestore
    }

    @NonNull
    @Override
    public BookedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booked, parent, false);
        return new BookedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedViewHolder holder, int position) {
        Booked booked = bookedList.get(position);

        // Lấy thông tin chi tiết Auto và TimeSlot từ Firestore để hiển thị
        loadAutoDetails(booked.getAutoId(), holder.tvAutoName, holder.tvPrice);
        loadTimeSlotDetails(booked.getTimeSlotId(), holder);
    }

    @Override
    public int getItemCount() {
        return bookedList.size();
    }

    public static class BookedViewHolder extends RecyclerView.ViewHolder {
        TextView tvAutoName, tvBookedDate, tvStartTime, tvEndTime, tvPrice;

        public BookedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAutoName = itemView.findViewById(R.id.tvAutoName);
            tvBookedDate = itemView.findViewById(R.id.tvBookedDate);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }

    private void loadBooked() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser(); // Lấy thông tin người dùng hiện tại

        if (user == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để đặt lịch!", Toast.LENGTH_SHORT).show();
            return; // Ngăn không cho tiếp tục nếu chưa đăng nhập
        }

        String userId = user.getUid();

        db.collection("Booked")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booked booked = document.toObject(Booked.class);
                            bookedList.add(booked);
                        }
                        notifyDataSetChanged(); // Refresh lại danh sách
                    }
                });
    }

    private void loadAutoDetails(String autoId, TextView tvAutoName, TextView tvPrice) {
        // Lấy thông tin Auto từ Firestore
        db.collection("Auto").document(autoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String autoName = documentSnapshot.getString("autoName");
                        Long price = documentSnapshot.getLong("price");
                        tvAutoName.setText(autoName);
                        tvPrice.setText("Giá: " + price + " VND");
                    } else {
                        tvAutoName.setText("Auto không xác định");
                        tvPrice.setText("");
                    }
                })
                .addOnFailureListener(e -> tvAutoName.setText("Lỗi tải Auto"));
    }

    private void loadTimeSlotDetails(String timeSlotId, BookedViewHolder holder) {
        // Lấy thông tin TimeSlot từ Firestore
        db.collection("TimeSlots").document(timeSlotId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String date = documentSnapshot.getString("date");
                        String startTime = documentSnapshot.getString("startTime");
                        String endTime = documentSnapshot.getString("endTime");

                        holder.tvBookedDate.setText("Ngày hẹn: " + date);
                        holder.tvStartTime.setText("Bắt đầu: " + startTime);
                        holder.tvEndTime.setText("Kết thúc: " + endTime);

                    } else {
                        holder.tvBookedDate.setText("Không xác định");
                        holder.tvStartTime.setText("");
                        holder.tvEndTime.setText("");

                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvBookedDate.setText("Lỗi tải ngày hẹn");
                    holder.tvStartTime.setText("Lỗi tải giờ bắt đầu");
                    holder.tvEndTime.setText("Lỗi tải giờ kết thúc");
                    holder.tvPrice.setText("Lỗi tải giá");
                });
    }
}
