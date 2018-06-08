package com.example.prakharmaheshwari.holmes;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dataclasses.HGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroup extends Fragment {
    String userNameErrorMsg = "Sorry ! The group name already exists";

    FloatingActionButton addNewGroup;

    ArrayList<String> array = new ArrayList<>();
    HashMap<String, String> username_idmap = new HashMap<>();


    EditText addGroupName;
    MultiAutoCompleteTextView addGroupMembers;
    Button addGroup;
    TextView groupNameError;


    public AddGroup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);


        addGroupName = (EditText) view.findViewById(R.id.editText_groupName);
        addGroupMembers = (MultiAutoCompleteTextView) view.findViewById(R.id.editText_groupMembers);
        addGroup = (Button) (Button) view.findViewById(R.id.btn_saveGroup);
        groupNameError = (TextView) view.findViewById(R.id.editText_groupErrorMessage);


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userName = ds.child("userName").getValue(String.class);
                    String name = ds.child("firstName").getValue(String.class);
                    String userid = ds.getKey();
                    Log.d("name", "" + name);
                    Log.d("name", "" + userName);
                    name = name + " (" + userName + ")";
                    username_idmap.put(userName, userid);
                    array.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, array);
        addGroupMembers.setThreshold(1);
        addGroupMembers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        addGroupMembers.setAdapter(arrayAdapter);


        //
        addGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged Group Name", "" + s);
                Log.d("afterTextChanged Group Name", "" + firebaseUser.getDisplayName());
                database.getReference().child("Users/" + firebaseUser.getUid() + "/Groups" + s).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (addGroupName.getText().length() != 0) {
                            if (dataSnapshot.getValue() != null) {
                                addGroupName.setTextColor(Color.RED);
                                groupNameError.setTextColor(Color.RED);
                                groupNameError.setText(userNameErrorMsg);
                            } else {
                                groupNameError.setTextColor(Color.BLACK);
                                groupNameError.setText("");
                                addGroupName.setTextColor(Color.BLACK);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                 String name = addGroupName.getText().toString().trim();
                 String members = addGroupMembers.getText().toString().trim();
                members = members.replace(", ",",");

                String[] membersList = members.split(",");
                ArrayList<String> newList = new ArrayList<>();
                for(String str:membersList){
                    String[] s = str.split(" \\(");
                    String newStr = s[1].replace(")","");
                    newList.add(newStr);
                }
                List<String> membersListName = Arrays.asList(membersList);


                if (name.length() == 0) {
                    Toast.makeText(getContext(), "Enter group name !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (members.length() == 0) {
                    Toast.makeText(getContext(), "Enter group members !", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference ref = database.getReference();
                ref.child("Users").child(firebaseUser.getUid()).child("groups").child(name).setValue("true");

                ref.child("Groups").child(name).setValue(new HGroup(firebaseUser.getDisplayName(), newList, ServerValue.TIMESTAMP));
                /*ref.child("Groups").child(name).child("Created By").setValue(firebaseUser.getDisplayName());
                ref.child("Groups").child(name).child("Members").setValue(newList);
                ref.child("Groups").child(name).child("TimeStamp").setValue(ServerValue.TIMESTAMP);*/


                for(String s:membersList){
                    String str = s.split("\\(")[1].replace(")", "");
                    String userid = username_idmap.get(str);
              //      ref.child("Users").child(userid[1]).child("Groups").push().setValue(name, "TRUE");
                    if (!firebaseUser.getDisplayName().equals(str))
                        ref.child("Users").child(userid).child("groups").child(name).setValue("false");
                }

                Fragment userGroup = new UserGroup();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, userGroup);
                transaction.commit();

            }
        });

        return view;
    }

}
