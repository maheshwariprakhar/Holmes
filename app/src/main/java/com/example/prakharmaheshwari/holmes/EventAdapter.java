package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class EventAdapter extends BaseAdapter{

    private Context mContext = null;
    private int mResourceId;
    private List<HashMap<String, String>> mItems;
    private FirebaseDatabase eDatabase;
    private DatabaseReference eUsers;
    private String uname;
    private static FirebaseUser User;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("Events");
    public EventAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
        mContext = context;
        mItems = items;
        mResourceId = Resourceid;
    }
    @Override
    public int getCount() {
            return mItems.size();
        }

        @Override public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);

            View itemView = inflater.inflate(mResourceId, null);
            User = FirebaseAuth.getInstance().getCurrentUser();

            TextView event_title = (TextView) itemView.findViewById(R.id.event_list_title);
            final TextView event_host = (TextView) itemView.findViewById(R.id.event_list_host);
            TextView event_time = (TextView) itemView.findViewById(R.id.event_list_time);
            ImageView event_img = (ImageView) itemView.findViewById(R.id.event_list_img);
            eDatabase = FirebaseDatabase.getInstance();
            eUsers = eDatabase.getReference("Users");



            final HashMap<String, String> hashMap = mItems.get(position);
            // Load the image using Glide

            //IGNORE IF NULL
            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(avatars.child(hashMap.get("p_key")+".png"))
                    .into(event_img);


            //include hosted by you
            Log.d("host", User.getUid());
            eUsers.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(!dataSnapshot.exists())
                        return;

                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ds.getKey());

                        if(ds.getKey().equals("userName") && !User.getDisplayName().equals(hashMap.get("host")) && ds.getValue(String.class).equals(hashMap.get("host"))) {
                            uname = ds.getValue(String.class);
                            event_host.setText(event_host.getText()+" "+uname+" ("+hashMap.get("host")+")");
                            break;
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


            event_title.setText(hashMap.get("title"));
            if(User.getDisplayName().equals(hashMap.get("host")))
                event_host.setText(event_host.getText()+" You");
            event_time.setText(hashMap.get("start_date")+", "+hashMap.get("start_time")+" - "+hashMap.get("end_date")+", "+hashMap.get("end_time"));


            return itemView;
        }
    }

