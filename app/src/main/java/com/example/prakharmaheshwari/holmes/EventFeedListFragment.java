package com.example.prakharmaheshwari.holmes;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import dataclasses.HEvents;

public class EventFeedListFragment extends Fragment {
    private DatabaseReference publicEvents, users, egroups;
    private ArrayList<HashMap<String, String>> eventList;
    private FirebaseDatabase eDatabase, eUsers, eGroups;
    private FloatingActionButton addEvent;
    private ListView eventListView;
    private static FirebaseUser User;
    private EventAdapter adapter;
    private TextView text;
    ArrayList<String> usersgroups;

    public EventFeedListFragment() {
        eventList=new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.getBoolean("Forward_to_Event")) {
            ForwardtoEvenDetail(args.getString("eventid"));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_event_list, container, false);
        eDatabase = FirebaseDatabase.getInstance();
        User = FirebaseAuth.getInstance().getCurrentUser();
        publicEvents= eDatabase.getReference("Events");
        eUsers = FirebaseDatabase.getInstance();
        users = eUsers.getReference("Users");
        eGroups = FirebaseDatabase.getInstance();
        egroups = eGroups.getReference("Groups");
        usersgroups  = new ArrayList<>();
        eventList = new ArrayList<>();
        text = (TextView) rootView.findViewById(R.id.event_text);
        eventListView = (ListView) rootView.findViewById(R.id.eve_event_list);

        adapter =new EventAdapter(inflater.getContext(),R.layout.event_list_layout,eventList);
        eventListView.setAdapter(adapter);

        users.child(User.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("groups")) {
                    for (DataSnapshot it : dataSnapshot.child("groups").getChildren()) {
                        usersgroups.add(it.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        publicEvents.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists()) {
                    text.setText("No upcoming events :(");
                    return;
                }

                boolean ispartofGroup = false, isMember=false;


                //Log.d("people", Boolean.toString(dataSnapshot.hasChild("peopleInvited")));

                if(dataSnapshot.hasChild("peopleInvited")) {
                    Log.d("people", "here");
                    for(DataSnapshot dschild : dataSnapshot.child("peopleInvited").getChildren()) {
                        String members[] = dschild.getKey().replaceAll(" ",",").split(",");
                        Log.d("people", members[1]);
                        members[1] = members[1].substring(1, members[1].length()-1);
                        if(members[1].equals(User.getDisplayName())) {
                            isMember = true;
                            Log.d("people1", User.getDisplayName());
                            break;
                        }


                    }
                }
                else
                    isMember = true;
                Log.d("people1", Boolean.toString(isMember));

                if(dataSnapshot.hasChild("groupsInvited")) {
                    DataSnapshot ds = dataSnapshot.child("groupsInvited");
                    for(DataSnapshot dschild : ds.getChildren()) {
                        String groupname = dschild.getKey();

                        if(usersgroups.size()>0 && usersgroups.contains(groupname)) {
                            ispartofGroup = true;
                            break;
                        }
                    }
                }
                else
                    ispartofGroup = true;

                HEvents hPublicEvents = dataSnapshot.getValue(HEvents.class);
                Log.d("Events", hPublicEvents.getPr_or_pu());
                String host = "";

                host = dataSnapshot.child("host").getValue(String.class);
                Log.d("Events1", host);
                if(hPublicEvents.getPr_or_pu().equals("private")) {
                    //if I am not the host

                    Log.d("Events2", Boolean.toString(ispartofGroup));
                    Log.d("Events2", Boolean.toString(isMember));
                    //Log.d("Events2",User.getDisplayName().equals(host));


                    /*Check this*/
                    if((ispartofGroup && isMember) || User.getDisplayName().equals(host)) {

                    }
                    else
                        return;

                    Log.d("Events2", Boolean.toString(isMember));
                    //if I am part of the group

                    //if I am invited personally

                }
                Log.d("Events", dataSnapshot.getKey());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("host", host);
                hashMap.put("title", hPublicEvents.getTitle());
                hashMap.put("start_date", hPublicEvents.getStart_date());
                hashMap.put("end_date", hPublicEvents.getEnd_date());
                hashMap.put("start_time", hPublicEvents.getStart_time());
                hashMap.put("end_time", hPublicEvents.getEnd_time());
                hashMap.put("p_key", dataSnapshot.getKey());
                hashMap.put("desc", hPublicEvents.getDescription());
                hashMap.put("pr_or_pu", hPublicEvents.getPr_or_pu());
                hashMap.put("location", hPublicEvents.getLocation());
                eventList.add(hashMap);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> o = (HashMap<String, String>) parent.getItemAtPosition(position);
                Log.d("Events", "choose new contact");
                for (HashMap<String, String> hashMap : eventList) {
//                    if (hashMap.get("p_key").equals(o.get("p_key"))) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("title", o.get("title"));
//                        bundle.putString("host", o.get("host"));
//                        bundle.putString("start_date", o.get("start_date"));
//                        bundle.putString("end_date", o.get("end_date"));
//                        bundle.putString("start_time", o.get("start_time"));
//                        bundle.putString("end_time", o.get("end_time"));
//                        bundle.putString("p_key", o.get("p_key"));
//                        bundle.putString("desc", o.get("desc"));
//                        bundle.putString("pr_or_pu", o.get("pr_or_pu"));
//                        bundle.putString("location",o.get("location"));
//                        onClickEventList(bundle);
//                        //alertDialog.dismiss();
//                        return;
//                    }

                    //prakhar
                    if (hashMap.get("p_key").equals(o.get("p_key"))) {
                        Bundle bundle = new Bundle();
                        bundle.putString("eventid", o.get("p_key"));
                        onClickEventList(bundle);
                        //alertDialog.dismiss();
                        return;
                    }
                    //prakhar
                }
            }
        });
        addEvent = (FloatingActionButton) rootView.findViewById(R.id.btn_addNewEvent);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEvent.class);
                startActivity(intent);
                //finish();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        addEvent.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        addEvent.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        addEvent.setVisibility(View.VISIBLE);


    }

    void onClickEventList(Bundle bundle) {
        Fragment frag = new EventFeedFragmentDetail();
        frag.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void ForwardtoEvenDetail(String eventid) {
        Bundle bundle = new Bundle();
        bundle.putString("eventid", eventid);
        onClickEventList(bundle);

    }
}
