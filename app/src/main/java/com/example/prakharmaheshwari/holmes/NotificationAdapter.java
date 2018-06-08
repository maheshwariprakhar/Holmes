package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public NotificationAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
        mContext = context;
        mItems = items;
        mResourceId = Resourceid;
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
        TextView data = itemView.findViewById(R.id.textView_data);
        HashMap<String, String> hashMap = mItems.get(position);

        int id = Integer.valueOf(hashMap.get("notificationid"));
        int type = Integer.valueOf(hashMap.get("type"));
        if (type == Event_Type) {
            String eventid = hashMap.get("Hid");
            String eventname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");
            data.setText(""+eventname+" event has been created by "+ hostName);

        } else if (type == Product_Type) {
            String productid = hashMap.get("Hid");
            String productname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");
            data.setText(""+productname+" product has been posted by "+ hostName);
        } else if (type == Group_Type){
            String groupid = hashMap.get("Hid");
            String groupname = hashMap.get("Hname");
            String hostName = hashMap.get("Hostname");
            data.setText(""+hostName+" group has been created by "+ hostName);
        } else
            Log.d(TAG, "unknown notification type");
            return itemView;
    }
}
