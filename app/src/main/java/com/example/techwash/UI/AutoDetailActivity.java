package com.example.techwash.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Adapter.TimeSlotAdapter;
import com.example.techwash.Model.TimeSlot;
import com.example.techwash.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AutoDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private FirebaseFirestore db;
    private String autoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autodetail);

        // Nhận autoId từ Intent
        autoId = getIntent().getStringExtra("autoId");
        if (autoId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin gara!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.recycler_timeslots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeSlotList = new ArrayList<>();
        timeSlotAdapter = new TimeSlotAdapter(this,timeSlotList);
        recyclerView.setAdapter(timeSlotAdapter);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Tải khung giờ cho Auto này
        loadTimeSlots(autoId);
    }

    private void loadTimeSlots(String autoId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("TimeSlots")
                .whereEqualTo("autoId", autoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<TimeSlot> timeSlotList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TimeSlot slot = document.toObject(TimeSlot.class);
                            // Đảm bảo gán ID từ Firestore
                            slot.setId(document.getId());
                            timeSlotList.add(slot);
                        }
                        updateRecyclerView(timeSlotList);
                    } else {
                        Log.e("AutoDetailActivity", "Lỗi khi lấy TimeSlots", task.getException());
                    }
                });
    }

    private void updateRecyclerView(List<TimeSlot> timeSlotList) {
        TimeSlotAdapter adapter = new TimeSlotAdapter(this, timeSlotList);
        RecyclerView recyclerView = findViewById(R.id.recycler_timeslots);
        recyclerView.setAdapter(adapter);
    }

}
