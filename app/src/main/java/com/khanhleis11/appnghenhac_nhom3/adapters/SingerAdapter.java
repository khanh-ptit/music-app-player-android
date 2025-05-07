package com.khanhleis11.appnghenhac_nhom3.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.R;
import com.khanhleis11.appnghenhac_nhom3.SingerDetailActivity;
import com.khanhleis11.appnghenhac_nhom3.models.Singer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingerViewHolder> {

    private List<Singer> singerList;
    private OnItemClickListener onItemClickListener;

    public SingerAdapter(List<Singer> singerList) {
        this.singerList = singerList;
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singer_item, parent, false);
        return new SingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, int position) {
        Singer singer = singerList.get(position);
        holder.singerName.setText(singer.getFullName());

        // Use Picasso to load the image using URL
        String avatarUrl = singer.getAvatar();
        if (avatarUrl != null && avatarUrl.startsWith("http://")) {
            avatarUrl = avatarUrl.replace("http://", "https://");
        }
        Picasso.get().load(avatarUrl).resize(200, 200).into(holder.singerAvatar);

        // Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(singer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    // Phương thức để set listener cho sự kiện click
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Interface cho sự kiện click vào item
    public interface OnItemClickListener {
        void onItemClick(Singer singer);
    }

    public static class SingerViewHolder extends RecyclerView.ViewHolder {

        ImageView singerAvatar;
        TextView singerName;

        public SingerViewHolder(@NonNull View itemView) {
            super(itemView);
            singerAvatar = itemView.findViewById(R.id.singer_avatar);
            singerName = itemView.findViewById(R.id.singer_name);
        }
    }
}
