package com.example.prakharmaheshwari.holmes;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserGroup extends Fragment {
    private static final String TAG = "HOLMES-APP_USER-GROUP";
    FloatingActionButton addNewGroup;
    ArrayAdapter<String> adapter;
    String groupName;
    Object isOwner;
    ArrayList<String> array = new ArrayList<>();
    ArrayList<String> groupNameList = new ArrayList<>();
    ArrayList<Object> ownership = new ArrayList<>();
    ArrayList<Object> memberList = new ArrayList<>();
    ListView groupList;

    public UserGroup() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_group, container, false);
        groupList = (ListView) view.findViewById(R.id.listview_group);
        getActivity().setTitle("My Groups");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().
                getReference("Users").child(user.getUid()).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    groupNameList.add(ds.getKey());
                    ownership.add(ds.getValue());
                }
                Log.d("group name", "" + groupNameList);
                Log.d("group name", "" + ownership);

                adapter = new ArrayAdapter<String>(getContext(), R.layout.activity_listdetail_view, groupNameList);
                /** Setting the data source to the list view */
                groupList.setAdapter(adapter);

                groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                        final AlertDialog.Builder adb = new AlertDialog.Builder(
                                getContext());
                        memberList.clear();

                        FirebaseDatabase.getInstance().getReference("Groups").child("" + parent.getItemAtPosition(position)).child("members").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    memberList.add(d.getValue());
                                }
                                if (ownership.get(position).equals("true")) isOwner = "Yes";
                                else isOwner = "No";
                                adb.setTitle("" + parent.getItemAtPosition(position) + "");
                                adb.setMessage("Owner of the group - " + isOwner + "\n" + "\n" + "Members are:" + "\n" + memberList);
                                adb.setPositiveButton("Ok", null);
                                adb.setIcon(R.drawable.home_icon);
                                adb.show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addNewGroup = (FloatingActionButton) view.findViewById(R.id.btn_userGroup_addNewGroup);
        addNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ADD button has been pressed");

                Fragment addGroup = new AddGroup();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, addGroup);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        addNewGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        addNewGroup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        groupNameList.clear();
        ownership.clear();
        memberList.clear();
        addNewGroup.setVisibility(View.VISIBLE);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.activity_listdetail_view, groupNameList);
        adapter.notifyDataSetChanged();


    }

}
