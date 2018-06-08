package com.example.prakharmaheshwari.holmes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogIn extends Fragment {
    Intent intent;
    Button logInBtn ;
    Button forgotPassword;
    FirebaseAuth auth;

    EditText userEmail ;
    EditText userPassword ;
    boolean emailValue ;
    boolean passwordValue ;


    public UserLogIn() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_user_log_in, container, false);
// Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();

        Intent intent = new Intent(getActivity(),EventFeedPage.class);


        logInBtn = (Button) view.findViewById(R.id.btn_LogIn);
        forgotPassword = (Button) view.findViewById(R.id.btn_forgotPassword);
        userEmail = (EditText) view.findViewById(R.id.editText_email);
        userPassword = (EditText) view.findViewById(R.id.editText_password);

        logInBtn.setEnabled(false);
        logInBtn.getBackground().setAlpha(100);
        emailValue = false;
        passwordValue = false;

        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(userEmail.getText().toString().trim().length() >0) emailValue = true;
                else emailValue = false;
                if(passwordValue && emailValue){
                    logInBtn.setEnabled(true);
                    logInBtn.getBackground().setAlpha(255);
                }
                else {
                    logInBtn.setEnabled(false);
                    logInBtn.getBackground().setAlpha(100);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(userEmail.getText().toString().trim().length() >0) emailValue = true;
                else emailValue = false;
                if(passwordValue && emailValue){
                    logInBtn.setEnabled(true);
                    logInBtn.getBackground().setAlpha(255);
                }
                else {
                    logInBtn.setEnabled(false);
                    logInBtn.getBackground().setAlpha(100);

                }

            }
        });

        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(userPassword.getText().toString().trim().length() >0) passwordValue = true;
                else passwordValue = false;

                if(passwordValue && emailValue){
                    logInBtn.setEnabled(true);
                    logInBtn.getBackground().setAlpha(255);
                }
                else {
                    logInBtn.setEnabled(false);
                    logInBtn.getBackground().setAlpha(100);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(userPassword.getText().toString().trim().length() >0) passwordValue = true;
                else passwordValue = false;

                if(passwordValue && emailValue){
                    logInBtn.setEnabled(true);
                    logInBtn.getBackground().setAlpha(255);
                }
                else {
                    logInBtn.setEnabled(false);
                    logInBtn.getBackground().setAlpha(100);

                }

            }
        });


        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = (EditText) view.findViewById(R.id.editText_email);
                userPassword = (EditText) view.findViewById(R.id.editText_password);


                String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();


                if (userEmail.length() ==0) {
                    Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.length() ==0) {
                    Toast.makeText(getContext(), "Enter password !", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("log-in", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("log-in", "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(getActivity(),EventFeedPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                                    startActivity(intent);
                                }
                            }
                        });



            }
        });

//
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPassword = new Intent(getActivity(),ForgotPassword.class);
                startActivity(resetPassword);

            }
        });
        return view;
    }

}