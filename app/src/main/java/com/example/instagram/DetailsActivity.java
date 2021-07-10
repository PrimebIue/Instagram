package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private Post post;

    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvRelativeTime;

    private CommentsActivity mComments;

    private ImageView ivImage;
    private ImageView ivProfilePicture;
    private ImageView ivProfilePictureCurrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        post = getIntent().getParcelableExtra("post");

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        ivImage = findViewById(R.id.ivImage);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivProfilePictureCurrUser = findViewById(R.id.ivProfilePictureCurrUser);


        tvRelativeTime.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(ivImage);

        Glide.with(this)
                .load(post.getUser().getParseFile("profileImage").getUrl())
                .into(ivProfilePicture);

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl())
                .into(ivProfilePictureCurrUser);
    }



}