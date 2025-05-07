package com.khanhleis11.appnghenhac_nhom3.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khanhleis11.appnghenhac_nhom3.R;
import com.khanhleis11.appnghenhac_nhom3.TopicDetailActivity;
import com.khanhleis11.appnghenhac_nhom3.models.Topic;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topicList;
    private Context context;  // Declare context

    // Constructor with context
    public TopicAdapter(List<Topic> topicList, Context context) {
        this.topicList = topicList;
        this.context = context;  // Assign context
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use context to inflate the view
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item, parent, false);
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

        // Use Picasso to load the topic image with updated HTTPS URL
        Picasso.get().load(avatarUrl).resize(200, 200).into(holder.topicImage);

        // Set click listener to open TopicDetailActivity
        holder.itemView.setOnClickListener(v -> {
            // Open TopicDetailActivity when the item is clicked and pass the topic id
            Intent intent = new Intent(context, TopicDetailActivity.class);
            intent.putExtra("topic_id", topic.getId()); // Pass the topic id to the intent
            context.startActivity(intent);
        });
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
