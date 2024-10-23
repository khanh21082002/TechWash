package com.example.techwash.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Adapter.BookedAdapter;
import com.example.techwash.Model.Booked;
import com.example.techwash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookedFragment extends Fragment {

    private TextView greetingText, emptyMessage;
    private RecyclerView recyclerView;
    private BookedAdapter bookedAdapter;
    private List<Booked> bookedList;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        initUI(view);
        setupRecyclerView();

        loadUserGreeting();
        loadBookedList();

        return view;
    }

    private void initUI(View view) {
        greetingText = view.findViewById(R.id.xinchao_id);
        emptyMessage = view.findViewById(R.id.text1);
        recyclerView = view.findViewById(R.id.rcv_book);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        bookedList = new ArrayList<>();
        bookedAdapter = new BookedAdapter(getContext(), bookedList);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(bookedAdapter);

        // Kiểm tra danh sách có trống không để hiển thị thông báo
        bookedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                toggleEmptyMessage();
            }
        });
    }

    private void toggleEmptyMessage() {
        if (bookedAdapter.getItemCount() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadUserGreeting() {
        if (currentUser != null) {
            db.collection("User").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String username = documentSnapshot.getString("username");
                        greetingText.setText("Xin chào, " + username + "!");
                    })
                    .addOnFailureListener(e -> greetingText.setText("Đặt chỗ"));
        }
    }

    private void loadBookedList() {
        if (currentUser == null) return;

        db.collection("Booked")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookedList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Booked booked = doc.toObject(Booked.class);
                        bookedList.add(booked);
                    }
                    bookedAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show()
                );
    }
}
