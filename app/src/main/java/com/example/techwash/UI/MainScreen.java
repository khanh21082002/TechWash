package com.example.techwash.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.techwash.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainScreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final SearchFragment searchFragment = new SearchFragment();
    //private final FavoriteFragment favoriteFragment = new FavoriteFragment();
    private final BookFragment bookFragment = new BookFragment();
    private final UserFragment userFragment = new UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Log.d("MainScreen", "MainScreen đã được khởi tạo!");
        bindViews();
        setInitialFragment();
        setupBottomNavigation();
    }

    private void bindViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    }

    private void setInitialFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, searchFragment)
                .commit();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_search:
                        switchFragment(searchFragment);
                        return true;
                    case R.id.menu_favorite:
                        //switchFragment(favoriteFragment);
                        return true;
                    case R.id.menu_book:
                        switchFragment(bookFragment);
                        return true;
                    case R.id.menu_user:
                        switchFragment(userFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
