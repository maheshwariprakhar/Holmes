package com.example.prakharmaheshwari.holmes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

public class AddEvent extends AppCompatActivity {

    TabLayout tabLayout;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment frag=getSupportFragmentManager().findFragmentByTag("PublicOrPublic");
        if(frag==null) {
            super.onBackPressed();
        }

        else
        {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(frag);
            transaction.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout_addEvents);
        this.setTitle("Create Event");
        TabLayout.Tab publicTab;
        publicTab = tabLayout.newTab();
        publicTab.setText("Public Event"); // set the Text for the first Tab
        tabLayout.addTab(publicTab, true); // add  the tab to the TabLayout

        TabLayout.Tab privateTab = tabLayout.newTab();
        privateTab.setText("Private Event"); // set the Text for the first Tab
        tabLayout.addTab(privateTab, false); // add  the tab to the TabLayout
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#EF6C00"));
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayout_addEvents, new PublicEvent(), "AddEvent");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    fragment = new PublicEvent();
                } else {
                    fragment = new PrivateEvent();
                }

                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_addEvents, fragment, "PublicOrPrivate");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
