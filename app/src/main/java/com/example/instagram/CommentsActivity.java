package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";
    public static final int INIT_LIMIT = 20;

    private int limit = 0;

    private RecyclerView rvComments;
    private List<Comment> allComments;
    private CommentsAdapter adapter;
    private Button btnComment;
    private EditText etAddComment;

    private SwipeRefreshLayout swipeContainer;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        rvComments = findViewById(R.id.rvComments);
        btnComment = findViewById(R.id.btnComment);
        etAddComment = findViewById(R.id.etAddComment);

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(this, allComments);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { queryCommentsUpdate(); }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };

        rvComments.addOnScrollListener(scrollListener);

        queryComments();

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etAddComment.getText().toString();

                if (content.isEmpty()) {
                    Toast.makeText(CommentsActivity.this, "Comments cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                Post post = getIntent().getParcelableExtra("post");
                saveComment(content, currentUser, post);
            }
        });

    }

    private void saveComment(String content, ParseUser currentUser, Post post) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(currentUser);
        comment.setPost(post);

        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    Toast.makeText(CommentsActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                etAddComment.setText("");
                queryCommentsUpdate();
            }
        });
    }

    private void queryCommentsUpdate() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Comment.KEY_POST, getIntent().getParcelableExtra("post"));
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                adapter.clear();

                // updates the limit
                limit = comments.size();

                // for debugging purposes let's print every post description to logcat
                for (Comment comment : comments) {
                    Log.i(TAG, "Comment: " + comment.getContent() + ", username: " + comment.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                adapter.addAll(comments);
                // Refreshed finished
                swipeContainer.setRefreshing(false);
                // Reset scrollListener
                scrollListener.resetState();
            }
        });
    }

    private void loadNextData(int page) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // include data referred by user key
        query.include(Comment.KEY_USER);
        // Skip already loaded posts
        Log.i(TAG, "Limit: " + limit);
        query.setSkip(limit);
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Comment.KEY_POST, getIntent().getParcelableExtra("post"));
        // order posts by creation date (newest first)
        query.addAscendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                Log.i(TAG, "LoadNextData");
                // Updates the limit
                limit = limit + comments.size();

                // for debugging purposes let's print every post description to logcat
                for (Comment comment : comments) {
                    Log.i(TAG, "Comment: " + comment.getContent() + ", username: " + comment.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                adapter.addAll(comments);
                // Refreshed finished
                swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryComments() {

        Log.i(TAG, "Get here");
        // specify what type of data we want to query - Post.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // include data referred by user key
        query.include(Comment.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(INIT_LIMIT);
        query.whereEqualTo(Comment.KEY_POST, getIntent().getParcelableExtra("post"));
        // order posts by creation date (newest first)
        query.addAscendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, com.parse.ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // updates the limit
                limit = comments.size();

                // for debugging purposes let's print every post description to logcat
                for (Comment comment : comments) {
                    Log.i(TAG, "Comment: " + comment.getContent() + ", username: " + comment.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                allComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}