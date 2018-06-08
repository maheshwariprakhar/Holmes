package com.example.prakharmaheshwari.holmes;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EventFeedFragmentDetail extends Fragment {
    private String etitle, ehost, date_time, p_key, descr, pr_or_pu, location;
    private ImageView eventImg;
    private TextView eventTitle, eventTime, prOrPub, eventDes, eventHost, eventLocation;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("Events");
    String startDate,startTime,endDate,endTime;

    public EventFeedFragmentDetail() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            etitle = getArguments().getString("title");
//            ehost = getArguments().getString("host");
//            date_time = getArguments().getString("start_date")+" "+getArguments().getString("start_time")+" - "+getArguments().getString("end_date")+" "+getArguments().getString("end_time");
//            p_key = getArguments().getString("p_key");
//            descr = getArguments().getString("desc");
//            pr_or_pu = getArguments().getString("pr_or_pu");
//            location = getArguments().getString("location");
//        }
        //prakhar
        if (getArguments() != null) {
            p_key = getArguments().getString("eventid");
        }

        FirebaseDatabase.getInstance().getReference().child("Events").child(p_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    if(d.getKey().equals("title")) etitle = (String) d.getValue();
                    if(d.getKey().equals("host")) {
                        ehost = d.getValue(String.class);
                        if(ehost.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
                            ehost = "You";
                    }
                    if(d.getKey().equals("description") ) descr =  d.getValue(String.class);
                    if(d.getKey().equals("pr_or_pu")) pr_or_pu = (String) d.getValue();
                    if(d.getKey().equals("location")) location = (String) d.getValue();
                    if(d.getKey().equals("start_date")){  startDate = (String) d.getValue();}
                    if(d.getKey().equals("start_time")){  startTime = (String) d.getValue();}
                    if(d.getKey().equals("end_date")){  endDate = (String) d.getValue();}
                    if(d.getKey().equals("end_time")){  endTime = (String) d.getValue();}
                    date_time = startDate+" "+startTime+" "+endDate+" "+endTime;
                }
                eventTitle.setText(etitle);
                eventTime.setText(date_time);
                eventHost.setText(ehost);
                eventDes.setText(descr);
                prOrPub.setText(pr_or_pu);
                eventLocation.setText(location);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_event_detail, container, false);
        eventImg = (ImageView) rootView.findViewById(R.id.event_detail_img);
        eventTime = (TextView) rootView.findViewById(R.id.event_detail_when);
        eventTitle = (TextView) rootView.findViewById(R.id.event_detail_title);
        eventDes = (TextView) rootView.findViewById(R.id.event_detail_description);
        eventHost = (TextView) rootView.findViewById(R.id.event_detail_host);
        prOrPub = (TextView) rootView.findViewById(R.id.event_detail_pr_or_pub);
        eventLocation = (TextView) rootView.findViewById(R.id.event_detail_loc);

        Glide.with(getContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(avatars.child(p_key+".png"))
                .into(eventImg);

        return rootView;
    }

}