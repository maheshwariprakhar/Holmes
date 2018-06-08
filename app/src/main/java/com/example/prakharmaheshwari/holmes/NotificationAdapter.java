package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private static final String TAG = "Notification Adapter";
    private final static int Event_Type = 11;
    private final static int Product_Type = 12;
    private final static int Group_Type = 13;
    private Context mContext = null;
    private int mResourceId;
    private List<HashMap<String, String>> mItems;
    private FirebaseStorage storage;
    private StorageReference imagesRef;

    public NotificationAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
        mContext = context;
        mItems = items;
        mResourceId = Resourceid;
        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference();
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
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
        LinearLayout eventlayout = itemView.findViewById(R.id.eventlayout);
        LinearLayout productlayout = itemView.findViewById(R.id.productlayout);
        LinearLayout grouplayout = itemView.findViewById(R.id.grouplayout);
        HashMap<String, String> hashMap = mItems.get(position);

        int id = Integer.valueOf(hashMap.get("notificationid"));
        int type = Integer.valueOf(hashMap.get("type"));
        if (type == Event_Type) {
            eventlayout.setVisibility(View.VISIBLE);
            productlayout.setVisibility(View.GONE);
            grouplayout.setVisibility(View.GONE);
            ImageView eventimage = itemView.findViewById(R.id.eventcover);
            TextView text = itemView.findViewById(R.id.eventtext);
            String eventid = hashMap.get("Hid");
            String eventname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");

            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(imagesRef.child("Events").child(eventid+".png"))
                    .into(eventimage);
            text.setText(""+eventname+" event has been created by "+ hostName);

        } else if (type == Product_Type) {
            eventlayout.setVisibility(View.GONE);
            productlayout.setVisibility(View.VISIBLE);
            grouplayout.setVisibility(View.GONE);

            ImageView productimg = itemView.findViewById(R.id.productcover);
            TextView text = itemView.findViewById(R.id.producttext);
            String productid = hashMap.get("Hid");
            String productname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");
            Glide.with(mContext /* context */)
                    .using(new FirebaseImageLoader())
                    .load(imagesRef.child("Products").child(productid+".png"))
                    .into(productimg);
            text.setText(""+productname+" product has been posted by "+ hostName);
        } else if (type == Group_Type){
            eventlayout.setVisibility(View.GONE);
            productlayout.setVisibility(View.GONE);
            grouplayout.setVisibility(View.VISIBLE);

            TextView text = itemView.findViewById(R.id.grouptext);
            String groupname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");
            text.setText(""+hostName+" invited you to join new group "+ groupname+".");
        } else
            Log.d(TAG, "unknown notification type");
            return itemView;
    }
}
