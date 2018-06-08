package com.example.prakharmaheshwari.holmes;

import android.Manifest;
import com.bumptech.glide.Glide;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class UserProfile extends AppCompatActivity {

    ImageButton userProfilePic;
    EditText userName ;
    TextView userFullName;
    TextView userEmail;
    TextView userAptNumber;
    ImageButton editButton;
    Button updateButton;
    String fullNameValue;
    String emailAddressValue;
    String userAptNumberValue;
    final int PICK_IMAGE_REQUEST = 111;
    private static Uri filePath;
    boolean imageUpdated = false;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("User Profile");

        // USER info
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+firebaseUser.getUid()+".png");
        userProfilePic = (ImageButton) findViewById(R.id.imageView_userProfilePic);
        userName=(EditText) findViewById(R.id.editText_userProfile_userName);
        userFullName = (TextView) findViewById(R.id.textView_userProfile_name);
        userEmail = (TextView) findViewById(R.id.textView_userProfile_email);
        userAptNumber = (TextView) findViewById(R.id.textView_userProfile_aptNumber);
        editButton =(ImageButton) findViewById(R.id.btn_userProfile_edit);
        updateButton=(Button) findViewById(R.id.btn_userProfile_update);
        updateButton.setEnabled(false);
        updateButton.getBackground().setAlpha(100);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        userName.setText(firebaseUser.getDisplayName());



//        Glide.with(this /* context */)
//                .using(new FirebaseImageLoader())
//                .load(storageRef)
//                .into(userProfilePic);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfilePic);

//        try {
//            final File localFile = File.createTempFile("images", "png");
//            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    userProfilePic.setImageBitmap(bitmap);
//
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                }
//            });
//        } catch (IOException e ) {}


        //Database connection
        DatabaseReference data =
                FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 fullNameValue = (String) dataSnapshot.child("fullName").getValue();
                 emailAddressValue = (String) dataSnapshot.child("emailAddress").getValue();
                userAptNumberValue =(String) dataSnapshot.child("apartmentNumber").getValue();


                Log.d("User Profile","Name-"+fullNameValue);
                Log.d("User Profile","Email Address-"+emailAddressValue);
                Log.d("User Profile","Apartment Number-"+userAptNumberValue);
                Log.d("User Profile After","Name-"+fullNameValue);
                //set values

                userFullName.setText(fullNameValue);
                userEmail.setText(emailAddressValue);
                userAptNumber.setText(userAptNumberValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName.setEnabled(true);
                updateButton.setEnabled(true);
                updateButton.getBackground().setAlpha(255);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageUpdated){
                    imageUpdated = false;
                    try {

                      //  Uri resourceURI = Uri.parse(""+filePath);
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar");    //change the url according to your firebase app
                        StorageReference childRef = storageRef.child(firebaseUser.getUid()+".png");
                        childRef.putFile(filePath);

//                        //getting image from gallery
//
//                       // userProfilePic.setImageURI(resourceURI);
//                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                .setPhotoUri(resourceURI)
//                                .build();
//                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Log.d("user profile updated", "User profile updated.");
//                                        }
//                                    }
//                                });

                        Toast.makeText(getBaseContext(), "Image has been updated", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                final String newUserName = userName.getText().toString().trim();
                if(!newUserName.equals(firebaseUser.getDisplayName())){

                   final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUserName)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userName.setText(newUserName);
                                        userName.setEnabled(false);
                                        Log.d("user profile updated", "User username updated.");
                                        Log.d("user profile updates","new user name is "+user.getDisplayName());
                                    }
                                }
                            });
                }

                finish();
            }
        });


        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageUpdated = true;
            updateButton.setEnabled(true);
            updateButton.getBackground().setAlpha(255);
            try {
                Bitmap bMap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), filePath);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 150, 150, true);
                userProfilePic.setImageBitmap(bMapScaled);
            }catch (IOException e){}
            }
    }
}
