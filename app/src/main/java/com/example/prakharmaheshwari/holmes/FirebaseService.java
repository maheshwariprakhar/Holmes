package com.example.prakharmaheshwari.holmes;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import SqlLite.NotificationsDbHelper;
import dataclasses.HMessage;

public class FirebaseService extends Service {
    private FirebaseDatabase mDatabase;
    private Context mContext;
    private DatabaseReference Messages;
    private DatabaseReference Events;
    private DatabaseReference Products;
    private DatabaseReference Groups;
    private DatabaseReference Usergroups;
    private FirebaseUser User;
    private NotificationManager manager;
    private int id = 1;
    private NotificationChannel messagechannel;
    private NotificationChannel groupchannel;
    private NotificationChannel eventchannel;
    private NotificationChannel productchannel;
    private final static String TAG = "Firebase Service";
    private final static String TITLE = "Holmes";
    private NotificationsDbHelper dbHelper;
    private SQLiteDatabase sqliteDatabase;
    private final HashMap<String, Integer> map = new HashMap<>();
    private final HashSet<String> groupset = new HashSet<>();
    private int Group_ID = 1000;
    private final static int Event_Type = 11;
    private final static int Product_Type = 12;
    private final static int Group_Type = 13;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        User = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        Messages= mDatabase.getReference("Messages");
        Events = mDatabase.getReference("Events");
        Groups = mDatabase.getReference("Groups");
        Usergroups = mDatabase.getReference("Users").child(User.getUid()).child("groups");
        Products = mDatabase.getReference("Products");
        dbHelper = new NotificationsDbHelper(this, "Notifications.db", null, 1);
        sqliteDatabase = dbHelper.getWritableDatabase();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            messagechannel = new NotificationChannel("messagechannel", "messagechannel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(messagechannel);
            eventchannel = new NotificationChannel("eventchannel", "eventchannel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(eventchannel);
            productchannel = new NotificationChannel("productchannel", "productchannel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(productchannel);
            groupchannel = new NotificationChannel("groupchannel", "groupchannel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(groupchannel);

        }

        Usergroups.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                    groupset.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                    groupset.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    groupset.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //only try to deal message send to user
        Messages.orderByChild("receiverid")
                .equalTo(User.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    if (!isForeground(mContext)) {
                        HMessage hMessage = dataSnapshot.getValue(HMessage.class);
                        MessageNotify(hMessage);
                        Log.d(TAG, "notificate new message.");
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

        Groups.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String groupname = dataSnapshot.getKey();
                    if (!isForeground(mContext)) {
                        String created_by = dataSnapshot.child("created_By").getValue(String.class);
                        String timestamp = dataSnapshot.child("timeStamp").getValue(Object.class).toString();
                        if (created_by.equals(User.getDisplayName()))
                            return;
                        for (DataSnapshot members : dataSnapshot.child("members").getChildren()) {
                            String membername = members.getValue(String.class);
                            if (membername.equals(User.getDisplayName())) {
                                GroupNotify(groupname, created_by, timestamp);
                            }
                        }
                        Log.d(TAG, "notificate new group.");
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

        Events.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    if (!isForeground(mContext)) {
                        //to do
                        String pr_or_pu = dataSnapshot.child("pr_or_pu").getValue(String.class);
                        String hostname = dataSnapshot.child("host").getValue(String.class);
                        String eventname = dataSnapshot.child("title").getValue(String.class);
                        if (pr_or_pu.equals("private")) {
                            //check whether user are invited
                            for (DataSnapshot group: dataSnapshot.child("groupsInvited").getChildren()) {
                                if (groupset.contains(group.getKey())) {
                                    EventNotify(pr_or_pu,hostname, eventname, dataSnapshot.getKey());
                                    return;
                                }
                            }
                            for (DataSnapshot people: dataSnapshot.child("peopleInvited").getChildren()) {
                                if (people.getKey().contains(User.getDisplayName())) {

                                    EventNotify(pr_or_pu,hostname, eventname, dataSnapshot.getKey());
                                    return;
                                }
                            }
                        }
                        else
                            EventNotify(pr_or_pu,hostname, eventname, dataSnapshot.getKey());
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

        Products.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    if (!isForeground(mContext)) {
                        String productid = dataSnapshot.getKey();
                        String owner = dataSnapshot.child("owner").getValue(String.class);
                        String productname = dataSnapshot.child("title").getValue(String.class);
                        //check whether user are invited
                        ProductNotify(productid, owner, productname);
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void SavetoDb(ContentValues values) {
        sqliteDatabase.insert("Notification", null, values);
        Log.d(TAG, "SAVE TO SQLITE");
    }
    public void MessageNotify(HMessage hMessage){
        Intent mIntent = new Intent(this, EventFeedPage.class);
        mIntent.putExtra("Forward_to_Message", true);
        mIntent.putExtra("provider_id", hMessage.getProviderid());
        mIntent.putExtra("provider_name", hMessage.getProvidername());
        PendingIntent openactivity = PendingIntent.getActivity(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int SUMMARY_ID;
        if (map.get(hMessage.getProviderid()) != null) {
            //already have the group
            SUMMARY_ID = map.get(hMessage.getProviderid());
        } else {
            SUMMARY_ID = ++Group_ID;
            map.put(hMessage.getProviderid(), Group_ID);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification summaryNotification =
                    new Notification.Builder(this, messagechannel.getId())
                            .setContentTitle(hMessage.getProviderid())
                            //set content text to support devices running API level < 24
                            .setContentText("Two new messages")
                            .setSmallIcon(R.drawable.app_icon)
                            //build summary info into InboxStyle template
                            .setStyle(new Notification.InboxStyle()
                                    .addLine("Alex Faarborg  Check this out")
                                    .addLine("Jeff Chang    Launch Party")
                                    .setBigContentTitle("2 new messages")
                                    .setSummaryText("janedoe@example.com"))
                            //specify which group this notification belongs to
                            .setGroup(hMessage.getProviderid())
                            .setContentIntent(openactivity)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .build();
            Notification.Builder builder = new Notification.Builder(getApplicationContext(), messagechannel.getId())
                    .setContentTitle(hMessage.getProvidername())
                    .setContentText(hMessage.getText())
                    .setSmallIcon(R.drawable.app_icon)
                    .setBadgeIconType(1)
                    .setGroup(hMessage.getProviderid())
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            manager.notify(id++, builder.build());
            manager.notify(SUMMARY_ID, summaryNotification);
        } else {
            Notification summaryNotification =
                    new NotificationCompat.Builder(this, "123")
                            .setContentTitle(hMessage.getProviderid())
                            //set content text to support devices running API level < 24
                            .setContentText("Two new messages")
                            .setSmallIcon(R.drawable.app_icon)
                            //build summary info into InboxStyle template
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine("Alex Faarborg  Check this out")
                                    .addLine("Jeff Chang    Launch Party")
                                    .setBigContentTitle("2 new messages")
                                    .setSummaryText("janedoe@example.com"))
                            //specify which group this notification belongs to
                            .setGroup(hMessage.getProviderid())
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .build();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                    .setContentTitle(hMessage.getProvidername())
                    .setContentText(hMessage.getText())
                    .setSmallIcon(R.drawable.app_icon)
                    .setDefaults(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setGroup(hMessage.getProviderid())
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(id++, builder.build());
            notificationManager.notify(SUMMARY_ID, summaryNotification);
        }


    }

    public void GroupNotify(String groupname, String created_by, String timestamp) {
        Intent mIntent = new Intent(this, EventFeedPage.class);
        mIntent.putExtra("Forward_to_Group", true);

        PendingIntent openactivity = PendingIntent.getActivity(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), groupchannel.getId())
                    .setContentTitle("Holmes Groups")
                    .setContentText(created_by+" invites you to join "+groupname+".")
                    .setSmallIcon(R.drawable.app_icon)
                    .setBadgeIconType(1)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            manager.notify(id++, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "234")
                    .setContentTitle("Holmes Groups")
                    .setContentText(created_by+" invites you to join "+groupname+".")
                    .setSmallIcon(R.drawable.app_icon)
                    .setDefaults(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(id++, builder.build());
        }

        ContentValues values = new ContentValues();
        values.put("type", Group_Type);
        values.put("Hid", groupname);
        values.put("Hname", groupname);
        values.put("Hostid", created_by);
        values.put("Hostname", created_by);
        values.put("timestamp", timestamp);
        SavetoDb(values);
    }

    public void EventNotify(String pr_or_pu, String hostname, String eventname, String eventid) {
        String contenttext = "";
        if (pr_or_pu.equals("public")) {
            contenttext = hostname+" create a new public event "+eventname+".";
        }else
            contenttext = hostname+" invite you to join a private event "+eventname+".";

        Intent mIntent = new Intent(this, EventFeedPage.class);
        mIntent.putExtra("Forward_to_Event", true);
        mIntent.putExtra("eventid", eventid);
        PendingIntent openactivity = PendingIntent.getActivity(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), eventchannel.getId())
                    .setContentTitle("Holmes Events")
                    .setContentText(contenttext)
                    .setSmallIcon(R.drawable.app_icon)
                    .setBadgeIconType(1)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            manager.notify(id++, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "345")
                    .setContentTitle("Holmes Events")
                    .setContentText(contenttext)
                    .setSmallIcon(R.drawable.app_icon)
                    .setDefaults(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(id++, builder.build());
        }
        ContentValues values = new ContentValues();
        values.put("type", Event_Type);
        values.put("Hid", eventid);
        values.put("Hname", eventname);
        values.put("Hostid", hostname);
        values.put("Hostname", hostname);
        values.put("timestamp", ServerValue.TIMESTAMP.toString());
        SavetoDb(values);
    }

    public void ProductNotify(String productid, String owner, String productname) {
        String contenttext = owner+" just post a new product "+productname+" for sale.";
        Intent mIntent = new Intent(this, EventFeedPage.class);
        mIntent.putExtra("Forward_to_Product", true);
        mIntent.putExtra("productid", productid);
        PendingIntent openactivity = PendingIntent.getActivity(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), productchannel.getId())
                    .setContentTitle("Holmes Products")
                    .setContentText(contenttext)
                    .setSmallIcon(R.drawable.app_icon)
                    .setBadgeIconType(1)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            manager.notify(id++, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "456")
                    .setContentTitle("Holmes Products")
                    .setContentText(contenttext)
                    .setSmallIcon(R.drawable.app_icon)
                    .setDefaults(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(openactivity);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(id++, builder.build());
        }
        ContentValues values = new ContentValues();
        values.put("type", Product_Type);
        values.put("Hid", productid);
        values.put("Hname", productname);
        values.put("Hostid", owner);
        values.put("Hostname", owner);
        values.put("timestamp", ServerValue.TIMESTAMP.toString());
        SavetoDb(values);
    }

    private boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList==null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName())
                    && processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }
}
