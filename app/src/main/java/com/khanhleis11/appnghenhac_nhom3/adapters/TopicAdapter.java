package com.khanhleis11.appnghenhac_nhom3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.R;
import com.khanhleis11.appnghenhac_nhom3.models.Topic;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topicList;

    public TopicAdapter(List<Topic> topicList) {
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.topicName.setText(topic.getTitle());

        // Check if the URL is HTTP and replace it with HTTPS
        String avatarUrl = topic.getAvatar();
        if (avatarUrl != null && avatarUrl.startsWith("http://")) {
            avatarUrl = avatarUrl.replace("http://", "https://");
        }

        // Use Picasso to load the topic image
        Picasso.get().load(avatarUrl).into(holder.topicImage);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {

        ImageView topicImage;
        TextView topicName;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicImage = itemView.findViewById(R.id.topic_image);
            topicName = itemView.findViewById(R.id.topic_name);
        }
    }
}
