package com.example.prakharmaheshwari.holmes;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText emailAddrress ;
    Button reset ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Reset Password");

        emailAddrress = (EditText) findViewById(R.id.editText_forgotPassword_email);
        reset = (Button) findViewById(R.id.btn_forgotPassword_reset);

        reset.getBackground().setAlpha(100);
        reset.setEnabled(false);

        emailAddrress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(emailAddrress.getText().toString().trim().length() >0){
                    reset.setEnabled(true);
                    reset.getBackground().setAlpha(255);
                }
                else{
                    reset.setEnabled(false);
                    reset.getBackground().setAlpha(100);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAddrress.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Email has been sent", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {

                                }
                            }
                        });
            }
        });

    }
}
