package com.example.prakharmaheshwari.holmes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // the user log-in should be checked whether the user is logged in.
        //
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(this, UserVerification.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this, EventFeedPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ;
            startActivity(intent);
            finish();
        }

    }
}