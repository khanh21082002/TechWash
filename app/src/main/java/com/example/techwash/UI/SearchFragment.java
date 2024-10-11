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

import com.example.techwash.Adapter.RoomAdapter;
import com.example.techwash.Model.Auto;
import com.example.techwash.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private List<Auto> list;
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private SearchView searchView;
    private Toolbar toolbar;
    private String[] districts = {
            "Quận 1", "Quận 2", "Quận 3", "Quận 4",
            "Quận 5", "Quận 6", "Quận 7", "Quận 8",
            "Quận 9", "Quận 10", "Quận 11", "Quận 12"
    };
    private DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initializeUI(view);
        setupToolbar();
        setupRecyclerView();
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
        reference = FirebaseDatabase.getInstance().getReference("TPHCM");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Auto auto = child.getValue(Auto.class);
                    if (auto != null && auto.isTrangthai()) {
                        list.add(auto);
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Khong the lay du lieu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        if (roomAdapter == null) {
            roomAdapter = new RoomAdapter(list);
            recyclerView.setAdapter(roomAdapter);
        } else {
            roomAdapter.notifyDataSetChanged();
        }
    }

    private void sortList(boolean highToLow) {
        Query query = reference.orderByChild("gia");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Auto auto = child.getValue(Auto.class);
                    if (auto != null) {
                        if (highToLow) {
                            list.add(0, auto);
                        } else {
                            list.add(auto);
                        }
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Khong the lay du lieu!", Toast.LENGTH_SHORT).show();
            }
        });
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
                roomAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("SearchFragment", "Selected item ID: " + id);
        switch (id) {
//            case R.id.menu_hightolow:
//                sortList(true);
//                return true;
//            case R.id.menu_lowtohigh:
//                sortList(false);
//                return true;
//            case R.id.menu_fillerQuan:
//                showDistrictDialog();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDistrictDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn quận muốn tìm");
        builder.setItems(districts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchView.setQuery(districts[which], true);
            }
        });
        builder.create().show();
    }
}
