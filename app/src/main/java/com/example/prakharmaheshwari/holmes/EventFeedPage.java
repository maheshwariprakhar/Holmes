package com.example.prakharmaheshwari.holmes;

//import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;

import SqlLite.NotificationsDbHelper;

public class EventFeedPage extends AppCompatActivity
        implements Messages.OnFragmentInteractionListener,
        Chat.OnFragmentInteractionListener,
        Notification.OnFragmentInteractionListener{
    StorageReference storageRefPrakhar;
    FirebaseStorage storagePrakhar;
    TextView drawerUsername ;
    FirebaseUser user;
    private static final String TAG = "Event Feed Page";
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private Intent firebaseintent;
    private NotificationManager manager;
    private Button addEvent;

    NavigationView navigationView ;
    ImageView userProfilePic ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_feed_page);
        //getting the user authentication to fetch the details
        FirebaseAuth authentication = FirebaseAuth.getInstance();
        user = authentication.getCurrentUser();
        String name = user.getDisplayName();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+user.getUid()+".png");
        storagePrakhar = FirebaseStorage.getInstance();
        storageRefPrakhar = storagePrakhar.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+user.getUid()+".png");
         navigationView = (NavigationView) findViewById(R.id.menu_view);

        View headerView = navigationView.getHeaderView(0);
         drawerUsername = (TextView) headerView.findViewById(R.id.menuHeader_textView);
        Log.d("User Event Feed Page",""+user.getDisplayName());
        drawerUsername.setText(name);

        userProfilePic = (ImageView) headerView.findViewById(R.id.menuHeader_imageView);

        // Create a storage reference from our app

        storagePrakhar = FirebaseStorage.getInstance();
        storageRefPrakhar = storagePrakhar.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+user.getUid()+".png");

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageRefPrakhar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfilePic);

// Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("images/stars.jpg");

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        firebaseintent = new Intent(this, FirebaseService.class);
        startService(firebaseintent);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);


        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        ForwardtoEventFeed();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                if(R.id.menu_eventFeed == item.getItemId()) {
                    Log.d("item", "" + item.getItemId());
                    eventFeed();
                }

                if(R.id.menu_signout == item.getItemId()) {
                    Log.d("item", "" + item.getItemId());
                    signOut_user();
                }
                if(R.id.menu_messages == item.getItemId()) {
                    Log.d("item", ""+item.getItemId());
                    ForwardtoMessage();
                }
                if(R.id.menu_FAQ == item.getItemId()){
                    Log.d("Settings","Settings button has been pressed");
                    user_FAQ();
                }

                if(R.id.menu_userGroup == item.getItemId()){
                    Log.d("Settings","Settings button has been pressed");
                    user_groups();
                }
                if(R.id.menu_notifications == item.getItemId()){
                    Log.d("Notifications","Notifications button has been pressed");
                    ForwardtoNotification();
                }

                if(R.id.menu_product == item.getItemId()){
                    Log.d("Notifications","Notifications button has been pressed");
                    products();
                }


                return true;
            }
        });

        // prakhar  - user profile on click of the image
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("User Profile Image","Image has been pressed");
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
                }
                Intent userProfile = new Intent(getBaseContext(), UserProfile.class);
                startActivity(userProfile);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        drawerUsername.setText(user.getDisplayName());

    //    storagePrakhar = FirebaseStorage.getInstance();
     //   storageRefPrakhar = storagePrakhar.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+user.getUid()+".png");

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageRefPrakhar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userProfilePic);

        // Create a storage reference from our app
   //     FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    //    FirebaseStorage storage = FirebaseStorage.getInstance();
    //    StorageReference storageRef =
    //            storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("User_Avatar/"+firebaseUser.getUid()+".png");
//
//        try {
//            final File localFile = File.createTempFile("images", "png");
//            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    userProfilePic.setImageBitmap(bitmap);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                }
//            });
//        } catch (IOException e ) {}
        manager.cancelAll();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut_user(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d("User","Signed out");
                    Intent intent = new Intent(EventFeedPage.this,UserVerification.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                    startActivity(intent);

                }
            }
        });

        auth.signOut();
    }


    private void ForwardtoEventFeed() {
        Fragment eventList = new EventFeedListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, eventList, "event_list");
        transaction.commit();
    }

    private void ForwardtoEventFeed(Boolean Forward_to_Event, String eventid) {
        Fragment eventList = new EventFeedListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("Forward_to_Event", Forward_to_Event);
        bundle.putString("eventid", eventid);
        eventList.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, eventList, "event_list");
        transaction.commit();
    }

    public void ForwardtoMessage(){
        //replace layout in content_frame to message layout
        //get all the contacts from realtime database and create the list view
        //load message xml

        Fragment fragmentmessages = new Messages();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragmentmessages);
        transaction.addToBackStack(null);
        transaction.commit();
        Toast.makeText(this, "Load message fragment!",
                Toast.LENGTH_LONG).show();
    }
    public void ForwardtoMessage(Boolean Forward_to_Message, String receiver_id, String receiver_name){
        //replace layout in content_frame to message layout
        //get all the contacts from realtime database and create the list view
        //load message xml

        Fragment fragmentmessages = new Messages();
        Bundle bundle = new Bundle();
        bundle.putBoolean("Forward_to_Message", Forward_to_Message);
        bundle.putString("receiver_id", receiver_id);
        bundle.putString("receiver_name", receiver_name);
        fragmentmessages.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragmentmessages);
        transaction.addToBackStack(null);
        transaction.commit();
        Toast.makeText(this, "Load message fragment!",
                Toast.LENGTH_LONG).show();
    }
    public void user_FAQ(){
        Fragment faq = new FAQ();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,faq);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void eventFeed(){
        Fragment eventFeed = new EventFeedListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,eventFeed);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void user_groups(){
        Fragment userGroup = new UserGroup();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,userGroup);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void products(){
        Fragment product = new ProductFeed();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,product);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void ForwardtoProductFeed(Boolean Forward_to_Product, String productid) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("Forward_to_Product", Forward_to_Product);
        bundle.putString("productid", productid);
        Fragment product = new ProductFeed();
        product.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,product);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void ForwardtoNotification(){
        //replace layout in content_frame to message layout
        //get all the contacts from realtime database and create the list view
        //load message xml

        Fragment notificationfragment = new Notification();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, notificationfragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(firebaseintent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "From notification?");
        if (intent.getBooleanExtra("Forward_to_Message", false)) {
            String receiver_id = intent.getStringExtra("provider_id");
            String receiver_name = intent.getStringExtra("provider_name");
            if (receiver_id != null && receiver_name != null) {
                ForwardtoMessage(true,receiver_id, receiver_name);
                //manager.getNotificationChannels();
                //NotificationChannel notificationChannel = manager.getNotificationChannel("messagechannel");
                manager.cancelAll();
            }
        }

        if (intent.getBooleanExtra("Forward_to_Group", false)) {
            user_groups();
            manager.cancelAll();
        }

        if (intent.getBooleanExtra("Forward_to_Event", false)) {
            String eventid = intent.getStringExtra("eventid");
            ForwardtoEventFeed(true, eventid);
            manager.cancelAll();
        }
        if (intent.getBooleanExtra("Forward_to_Product", false)) {
            String productid = intent.getStringExtra("productid");
            ForwardtoProductFeed(true, productid);
            manager.cancelAll();
        }
    }




}