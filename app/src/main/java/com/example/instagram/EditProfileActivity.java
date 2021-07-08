package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileActivity";
    public static final int CHOOSE_PIC_REQUEST_CODE = 200;

    private Uri mMediaUri;

    private ImageView ivProfilePicture;
    private EditText etUsername;
    private EditText etDescription;
    private Button btnProfilePhoto;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etUsername = findViewById(R.id.etUsername);
        etDescription = findViewById(R.id.etDescription);
        btnProfilePhoto = findViewById(R.id.btnProfilePhoto);
        btnSubmit = findViewById(R.id.btnSubmit);

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile("profileImage").getUrl())
                .into(ivProfilePicture);

        etUsername.setText(ParseUser.getCurrentUser().getUsername());
        etDescription.setText(ParseUser.getCurrentUser().getString("userDescription"));

        btnProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload image
                Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePictureIntent.setType("image/*");
                startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                String username = etUsername.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Bio can't be empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (username.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "username can't be empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (username.length() < 5) {
                    Toast.makeText(EditProfileActivity.this, "username can't be less than 5 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i(TAG, "on btnSubmit click");
                try {
                    saveProfileChanges(description, username, mMediaUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveProfileChanges(String description, String username, Uri mMediaUri) throws IOException {
        Log.i(TAG, "Saving changes...");
        ParseUser user = ParseUser.getCurrentUser();

        user.setUsername(username);
        user.put("userDescription", description);

        if (mMediaUri != null) {
        // Convert URI to bytes for upload
        InputStream iStream = getContentResolver().openInputStream(mMediaUri);
        byte[] fileBytes = getBytes(iStream);

        ParseFile file = new ParseFile(fileBytes);
        user.put("profileImage", file);
        }



        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(EditProfileActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "User save was successful!");
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PIC_REQUEST_CODE) {
            if  (resultCode == RESULT_OK) {
                mMediaUri = data.getData();

                ivProfilePicture.setImageURI(mMediaUri);
            }
        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}