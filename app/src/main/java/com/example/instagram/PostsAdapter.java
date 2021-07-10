package com.example.instagram;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {


    private Context context;
    private List<Post> posts;
    private OnClickListener clickListener;

    public interface OnClickListener {
        void onPostClicked(int position);
        void onProfileClicked(int position);
        void onAddCommentClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.clickListener = clickListener;
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvUsernameDescription;
        private ImageView ivProfilePicture;
        private ImageView ivProfilePictureCurrUser;
        private TextView tvAddComment;
        private ImageView ivIconComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUsernameDescription = itemView.findViewById(R.id.tvUsernameDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivProfilePictureCurrUser = itemView.findViewById(R.id.ivProfilePictureCurrUser);
            tvAddComment = itemView.findViewById(R.id.tvAddComment);
            ivIconComment = itemView.findViewById(R.id.ivIconComment);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvUsernameDescription.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();

            Glide.with(context)
                    .load(post.getUser().getParseFile("profileImage").getUrl())
                    .into(ivProfilePicture);

            Glide.with(context)
                    .load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl())
                    .into(ivProfilePictureCurrUser);

            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onProfileClicked(getAdapterPosition()); }
            });
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onPostClicked(getAdapterPosition()); }
            });
            tvAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onAddCommentClicked(getAdapterPosition()); }
            });
            ivIconComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickListener.onAddCommentClicked(getAdapterPosition()); }
            });

            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
