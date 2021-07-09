package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    public static final String KEY_POST = "post";

    private ParseUser user;

    private GridView gridView;
    private GridPostsAdapter adapter;
    private ImageView ivProfilePicture;
    private TextView tvDescription;
    private TextView tvPostsNumber;
    private TextView tvUsername;

    private static final int INIT_LIMIT = 20;

    private int limit = 0;

    List<Post> userPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        user = getIntent().getParcelableExtra("user");

        tvDescription = findViewById(R.id.tvDescription);
        tvPostsNumber = findViewById(R.id.tvPostsNumber);
        tvUsername = findViewById(R.id.tvUsername);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        Glide.with(this)
                .load(user.getParseFile("profileImage").getUrl())
                .into(ivProfilePicture);

        Log.i(TAG, "Started Profile Activity");
        tvDescription.setText(user.getString("userDescription"));
        tvUsername.setText(user.getUsername());

        try {
            tvPostsNumber.setText(String.valueOf(query.whereEqualTo(Post.KEY_USER, user).count()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        gridView = (GridView) findViewById(R.id.gvPosts);

        userPosts = new ArrayList<>();
        adapter = new GridPostsAdapter(this, userPosts);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "Click at post position: " + position);
                // Create new activity intent
                Intent i = new Intent(ProfileActivity.this, DetailsActivity.class);
                // Pass data
                i.putExtra(KEY_POST, userPosts.get(position));
                // Display Activity
                startActivity(i);
            }
        });

        queryPosts();
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Post.KEY_USER, user);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // updates the limit
                limit = posts.size();

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                userPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}