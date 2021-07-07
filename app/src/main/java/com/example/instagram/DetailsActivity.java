package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private Post post;

    TextView tvUsername;
    TextView tvDescription;
    TextView tvRelativeTime;

    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        post = getIntent().getParcelableExtra("post");

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        ivImage = findViewById(R.id.ivImage);


        tvRelativeTime.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(ivImage);
    }



}