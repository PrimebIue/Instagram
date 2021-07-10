package com.example.instagram.Application;

import android.app.Application;

import com.example.instagram.Comment;
import com.example.instagram.Post;
import com.example.instagram.R;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        // Register your parse models
        // TODO -- ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id)) // should correspond to Application Id env variable
                .clientKey(getString(R.string.back4app_client_key))// should correspond to Client key env variable
                        .server("https://parseapi.back4app.com").build());
    }
}
