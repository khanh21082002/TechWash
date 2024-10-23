package com.example.techwash.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Adapter.AutoAdapter;
import com.example.techwash.Adapter.NewsAdapter;
import com.example.techwash.Model.Auto;
import com.example.techwash.Model.News;
import com.example.techwash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView, newsRecyclerView;
    private AutoAdapter autoAdapter;
    private NewsAdapter newsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initializeUI(view);
        setupRecyclerView();

        autoAdapter = new AutoAdapter(new ArrayList<>());
        recyclerView.setAdapter(autoAdapter);

        newsAdapter = new NewsAdapter(getContext(), new ArrayList<>());
        newsRecyclerView.setAdapter(newsAdapter);

        loadAutoList();
        loadNewsList();

        // Gọi setUserGreeting với view đã khởi tạo
        setUserGreeting(view);

        return view;
    }

    private void setUserGreeting(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null) {
                // Cập nhật TextView với tên người dùng
                TextView greetingTextView = view.findViewById(R.id.tv_greeting);
                greetingTextView.setText("Xin chào mừng " + displayName + ", bạn đến với TechWash!");
            }
        }
    }

    private void initializeUI(View view) {
        recyclerView = view.findViewById(R.id.Rcv);
        newsRecyclerView = view.findViewById(R.id.recycler_news);
    }

    private void setupRecyclerView() {
        // Setup RecyclerView cho Auto
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup RecyclerView cho tin tức với GridLayoutManager để hiện 2 card trên mỗi hàng
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        newsRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void loadAutoList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Auto")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Auto> autoList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Auto auto = document.toObject(Auto.class);
                            auto.setAutoId(document.getId());
                            autoList.add(auto);
                        }
                        autoAdapter.updateData(autoList);
                    } else {
                        Log.w("FirebaseData", "Lỗi khi lấy dữ liệu Auto.", task.getException());
                    }
                });
    }

    private void loadNewsList() {
        List<News> newsList = new ArrayList<>();
        newsList.add(new News("Khuyến mãi rửa xe 50%", "Giảm giá dịch vụ rửa xe nhanh cho các khách hàng mới", "https://binhphuongcars.com/wp-content/uploads/2023/06/354039041_130587333380318_1371513820585816466_n.jpeg"));
        newsList.add(new News("Giới thiệu dịch vụ mới", "Dịch vụ vệ sinh nội thất xe hơi ra mắt", "https://thanhphongauto.com/wp-content/uploads/2022/10/ra-mat-dich-vu-ru-xe-tu-dong-tai-thanh-phong.jpg"));
        newsList.add(new News("Bảo dưỡng định kỳ", "Giảm giá 20% cho các dịch vụ bảo dưỡng định kỳ", "https://autowash.vn/wp-content/uploads/2020/02/FB-AD-02.jpg"));
        newsList.add(new News("Sự kiện giáng sinh", "Sự kiện giáng sinh sẽ có nhiều ưu đãi mới", "https://riversidepalace.vn/multidata/thiep-chuc-giang-sinh_092358931-20.jpg"));
        Log.d("NewsList", "Số lượng tin tức: " + newsList.size());

        newsAdapter.updateData(newsList);
    }
}
