package com.example.techwash.Adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techwash.Model.Auto;
import com.example.techwash.R;
import com.example.techwash.UI.AutoDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class AutoAdapter extends RecyclerView.Adapter<AutoAdapter.ViewHolder> {
    private List<Auto> list;

    public AutoAdapter(List<Auto> list) {
        this.list = list;
    }

    // Phương thức cập nhật dữ liệu
    public void updateData(List<Auto> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Auto auto = list.get(position);

        // Gán dữ liệu vào các View
        holder.garageName.setText(auto.getAutoName() != null ? auto.getAutoName() : "Không rõ tên");
        holder.garageAddress.setText(auto.getAddress() != null ? auto.getAddress() : "Không rõ địa chỉ");
        holder.garagePrice.setText(String.format(Locale.getDefault(), "Giá: %,d VND", auto.getPrice()));

        // Kiểm tra null trước khi gọi .isEmpty()
        if (auto.getImageAuto() != null && !auto.getImageAuto().isEmpty()) {
            Picasso.get().load(auto.getImageAuto())
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .fit()
                    .centerCrop()
                    .into(holder.imgGarage);
        } else {
            holder.imgGarage.setImageResource(R.drawable.ic_baseline_image_24);
        }

        holder.btn_view_timeslots.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AutoDetailActivity.class);
            intent.putExtra("autoId", auto.getAutoId());  // Truyền autoId sang AutoDetailActivity
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGarage;
        TextView garageName, garageAddress, garagePrice;
        Button btn_view_timeslots;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGarage = itemView.findViewById(R.id.img1);
            garageName = itemView.findViewById(R.id.garage_name);
            garageAddress = itemView.findViewById(R.id.garage_address);
            garagePrice = itemView.findViewById(R.id.garage_price);
            btn_view_timeslots = itemView.findViewById(R.id.btn_view_timeslots);
        }
    }
}


