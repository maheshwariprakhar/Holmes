package com.example.prakharmaheshwari.holmes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserVerification extends AppCompatActivity {
    ArrayList<Object> array = new ArrayList<>();
    EditText submit ;
    Button verify;
    TextView error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        submit = (EditText) findViewById(R.id.editText_emailAddressVerify);
        verify = (Button) findViewById(R.id.btn_verifyEmailAddress);
        error = (TextView) findViewById(R.id.textView_verification_Error);

        verify.getBackground().setAlpha(100);

        FirebaseDatabase.getInstance().getReference("EmailVerification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object userEmail = ds.getValue();
                    Log.d("user ",""+userEmail);
                    array.add(userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(submit.getText().toString().trim().length()>0){
                    verify.setEnabled(true);
                    verify.getBackground().setAlpha(255);
                }
                else{
                    verify.setEnabled(false);
                    verify.getBackground().setAlpha(100);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(submit.getText().toString().trim().length()>0){
                    verify.setEnabled(true);
                    verify.getBackground().setAlpha(255);
                }
                else{
                    verify.setEnabled(false);
                    verify.getBackground().setAlpha(100);

                }

            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
                error.setVisibility(View.INVISIBLE);
                String value = submit.getText().toString().trim();

                    if(array.contains(value)){
                        Log.d("verified","verified");
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                        //     Intent intent = new Intent(getBaseContext(), PublicUserProfile.class);
                   //      intent.putExtra("userID","h2vKBSD6d8Sq2cf97qn9rxc17Z82");
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d("Holmes- Verification","User Not Verified");
                        error.setText("The email address does not match with the community society members database !! Please contact the community office for further assistance.");
                        error.setTextColor(Color.RED);
                        error.setVisibility(View.VISIBLE);
                    }
            }
        });
    }

}