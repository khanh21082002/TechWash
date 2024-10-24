package com.example.techwash.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techwash.R;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        TextView title = findViewById(R.id.news_detail_title);
        TextView description = findViewById(R.id.news_detail_description);
        ImageView image = findViewById(R.id.news_detail_image);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String newsTitle = intent.getStringExtra("title");
        String newsDescription = intent.getStringExtra("description");
        String newsImageUrl = intent.getStringExtra("imageUrl");

        // Thiết lập dữ liệu vào các view
        title.setText(newsTitle);
        description.setText(newsDescription);
        Picasso.get().load(newsImageUrl).into(image);
    }
}
