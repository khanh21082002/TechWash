package com.example.techwash.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Adapter.DateAdapter;
import com.example.techwash.Adapter.TimeSlotAdapter;
import com.example.techwash.Model.TimeSlot;
import com.example.techwash.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AutoDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlotList;
    private FirebaseFirestore db;
    private String autoId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autodetail);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        autoId = getIntent().getStringExtra("autoId");
        if (autoId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin gara!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycler_timeslots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        timeSlotList = new ArrayList<>();
        timeSlotAdapter = new TimeSlotAdapter(this, timeSlotList);
        recyclerView.setAdapter(timeSlotAdapter);

        db = FirebaseFirestore.getInstance();
        setupToolbar();

        // Khởi tạo danh sách ngày
        List<String> futureDates = getFutureDates(7); // Lấy 7 ngày tiếp theo
        DateAdapter dateAdapter = new DateAdapter(futureDates, selectedDate -> {
            loadTimeSlots(autoId, selectedDate); // Gọi hàm loadTimeSlots với ngày được chọn
        });

        RecyclerView dateRecyclerView = findViewById(R.id.recycler_dates);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateRecyclerView.setAdapter(dateAdapter);

        // Gọi loadTimeSlots ngay lần đầu với ngày đầu tiên trong danh sách (ngày hôm nay)
        if (!futureDates.isEmpty()) {
            String todayDate = futureDates.get(0); // Ngày đầu tiên trong danh sách
            loadTimeSlots(autoId, todayDate);
        }
    }


    private void loadTimeSlots(String autoId, String date) {
        Log.d("AutoDetailActivity", "Loading slots for autoId: " + autoId + " on date: " + date);

        db.collection("TimeSlots")
                .whereEqualTo("autoId", autoId)
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        timeSlotList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TimeSlot slot = document.toObject(TimeSlot.class);
                            slot.setId(document.getId());
                            timeSlotList.add(slot);
                        }
                        timeSlotAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("AutoDetailActivity", "Lỗi khi lấy TimeSlots", task.getException());
                    }
                });
    }


    private void updateRecyclerView(List<TimeSlot> timeSlotList) {
        this.timeSlotList.clear();
        this.timeSlotList.addAll(timeSlotList);
        timeSlotAdapter.notifyDataSetChanged(); // Sử dụng cùng adapter để refresh
    }



    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_detail); // Sử dụng findViewById để tìm toolbar
        setSupportActionBar(toolbar); // Đặt toolbar làm ActionBar
        getSupportActionBar().setTitle("Lịch"); // Thiết lập tiêu đề cho toolbar
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_sort_24)); // Đặt icon
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public List<String> getFutureDates(int daysInFuture) {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM - E", Locale.getDefault());
        SimpleDateFormat firestoreFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < daysInFuture; i++) {
            String displayDate = displayFormat.format(calendar.getTime());  // Định dạng hiển thị
            String firestoreDate = firestoreFormat.format(calendar.getTime());  // Định dạng Firestore

            // Thêm ngày với định dạng Firestore vào danh sách (truyền đúng khi lấy TimeSlots)
            dateList.add(firestoreDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateList;
    }



}
