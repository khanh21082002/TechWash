package com.example.techwash.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Adapter.AutoAdapter;
import com.example.techwash.Model.Auto;
import com.example.techwash.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private List<Auto> list;
    private RecyclerView recyclerView;
    private AutoAdapter autoAdapter;
    private SearchView searchView;
    private Toolbar toolbar;
    private DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Khởi tạo UI và RecyclerView
        initializeUI(view);
        setupToolbar();
        setupRecyclerView();

        // Khởi tạo adapter với danh sách rỗng ban đầu
        autoAdapter = new AutoAdapter(new ArrayList<>());
        recyclerView.setAdapter(autoAdapter); // Gán adapter ngay khi khởi tạo

        // Tải dữ liệu từ Firebase
        loadAutoList();

        return view;
    }



    private void initializeUI(View view) {
        recyclerView = view.findViewById(R.id.Rcv);
        toolbar = view.findViewById(R.id.toolbar);
        list = new ArrayList<>();
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("");
            toolbar.setOverflowIcon(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_sort_24));
        }
    }

    private void setupRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                            // Ghi log để kiểm tra giá trị từng trường
                            Log.d("FirebaseData", "AutoName: " + auto.getAutoName());
                            Log.d("FirebaseData", "ImageAuto: " + auto.getImageAuto());
                            autoList.add(auto);
                        }
                        autoAdapter.updateData(autoList);
                    } else {
                        Log.w("FirebaseData", "Lỗi khi lấy dữ liệu.", task.getException());
                    }
                });
    }






    private void updateRecyclerView() {
        if (autoAdapter == null) {
            autoAdapter = new AutoAdapter(list);
            recyclerView.setAdapter(autoAdapter);
        } else {
            autoAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        setupSearch(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSearch(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconified(true);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Tìm kiếm...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //autoAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}
