package com.example.prakharmaheshwari.holmes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PublicUserProfile extends AppCompatActivity {
    private static final String TAG = "PublicUserProfile";
    String fullNameValue;
    String emailAddressValue;
    String userAptNumberValue;
    String userNameValue;

     ImageView userProfilePic;
     TextView userName;
     TextView userFullName ;
     TextView userEmail;
     TextView  userAptNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_user_profile);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setTitle("User Profile");
        userProfilePic = (ImageView) findViewById(R.id.imageView_publicUserProfilePic);
        userName=(TextView) findViewById(R.id.editText_publicUserProfile_userName);
        userFullName = (TextView) findViewById(R.id.textView_publicUserProfile_name);
       userEmail = (TextView) findViewById(R.id.textView_publicUserProfile_email);
       userAptNumber = (TextView) findViewById(R.id.textView_publicUserProfile_aptNumber);

        Intent i=getIntent();
        String userID =i.getStringExtra("userID");


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+userID+".png");
        //Glide.get(this).clearDiskCache();
/*        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(userProfilePic);*/
        try {
            final File localFile = File.createTempFile(userID, "png");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    userProfilePic.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, exception.getMessage());
                }
            });
        } catch (IOException e ) {}

        FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullNameValue = (String) dataSnapshot.child("fullName").getValue();
                emailAddressValue = (String) dataSnapshot.child("emailAddress").getValue();
                userAptNumberValue =(String) dataSnapshot.child("apartmentNumber").getValue();
                userNameValue = (String) dataSnapshot.child("userName").getValue();

                Log.d("User Profile","Name-"+fullNameValue);
                Log.d("User Profile","Email Address-"+emailAddressValue);
                Log.d("User Profile","Apartment Number-"+userAptNumberValue);
                Log.d("User Profile After","Name-"+userNameValue);
                //set values
                userFullName.setText(fullNameValue);
                userEmail.setText(emailAddressValue);
                userAptNumber.setText(userAptNumberValue);
                userName.setText(userNameValue);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
