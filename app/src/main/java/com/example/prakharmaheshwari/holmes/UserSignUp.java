package com.example.prakharmaheshwari.holmes;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserSignUp extends Fragment {


    Button signUpButton;
    EditText userFullName;
    EditText userEmail;
    EditText userPassword;
    EditText userAptNumber;
    EditText userName;
    TextView userNameError;
    String userNameErrorMsg = "Sorry ! The user name already exists";

    private final static String TAG = "UserSignUp";



    public UserSignUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);
        signUpButton = (Button) view.findViewById(R.id.btn_signUp_singUp);

        userFullName = (EditText) view.findViewById(R.id.editText_signUp_fullName);
        userName = (EditText) view.findViewById(R.id.editText_signUp_userName);
        userAptNumber = (EditText) view.findViewById(R.id.editText_signUp_enterApartmentNumber);
        userEmail = (EditText) view.findViewById(R.id.editText_signUp_email);
        userPassword = (EditText) view.findViewById(R.id.editText_signUp_password);
        userNameError = (TextView) view.findViewById(R.id.editText_signUp_userNameErrorMsg);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();



        DatabaseReference fdbRefer = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged", "" + s);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", "" + s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged", "" + s);

                database.getReference().child("Users/" + s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (userName.getText().length() != 0) {
                            if (dataSnapshot.getValue() != null) {
                                userName.setTextColor(Color.RED);
                                userNameError.setTextColor(Color.RED);
                                userNameError.setText(userNameErrorMsg);
                            } else {
                                userNameError.setTextColor(Color.BLACK);
                                userNameError.setText("");
                                userName.setTextColor(Color.BLACK);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        userEmail = view.findViewById(R.id.editText_signUp_email);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = userEmail.getText().toString().trim();
                final String password = userPassword.getText().toString().trim();
                final String fullname = userFullName.getText().toString().trim();
                final String apartmentNumber = userAptNumber.getText().toString().trim();
                final String username = userName.getText().toString().trim();

                if (email.length() == 0) {
                    Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() == 0) {
                    Toast.makeText(getContext(), "Enter password !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fullname.length() == 0) {
                    Toast.makeText(getContext(), "Enter full name !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (apartmentNumber.length() == 0) {
                    Toast.makeText(getContext(), "Enter Apartment Number !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userName.length() == 0) {
                    Toast.makeText(getContext(), "Enter User Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    User newUser = new User();
                                    newUser.emailAddress = email;
                                    String userNM[] = fullname.split(" ");
                                    newUser.firstName = userNM[0];
                                    newUser.lastName = userNM[1];
                                    newUser.fullName = fullname;
                                    newUser.userName = username;
                                    newUser.apartmentNumber = apartmentNumber;
                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference();
                                    //Uri file = Uri.fromFile(new File("/Users/prakharmaheshwari/Documents/Git_Upload/Holmes/app/src/main/res/drawable/user_avatar_default.png"));
                                    Uri resourceURI = Uri.parse("android.resource://com.example.prakharmaheshwari.holmes/" + R.drawable.user_avatar_default);
                                    Log.d("new user", "createUserWithEmail:success the id is "+user.getUid());
                                    Log.d("new user", "createUserWithEmail:success the name is "+fullname);
                                    Log.d("new user", "createUserWithEmail:success the email is "+email);
                                    Log.d("new user", "createUserWithEmail:success the email is "+apartmentNumber);
                                    ref.child("Users").child(user.getUid()).setValue(newUser);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("user profile updated", "User profile updated.");
                                                    }
                                                }
                                            });

                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference().child("User_Avatar");    //change the url according to your firebase app
                                    StorageReference childRef = storageRef.child(user.getUid()+".png");
                                    childRef.putFile(resourceURI);
                                   // if(!imagePath.getText().equals(""))

                                    Intent intent = new Intent(getActivity(), EventFeedPage.class);
                                 //   intent.putExtra("EXTRA_SESSION_ID", user.getDisplayName());
                                //    intent.putExtra("EXTRA_Image", user.getDisplayName());


                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    userEmail.setTextColor(Color.RED);
                                    Log.w("new user", "createUserWithEmail:failure" + task.getException().getMessage());
                                    Toast.makeText(getContext(), "Authentication failed." + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                signUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "sign up click.");
                    }
                });
            }
        });

        return view;
    }
}
