package com.example.prakharmaheshwari.holmes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class UserSignInUp extends AppCompatActivity {

    TabLayout tabLayout;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_in_up);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout_singUpIn);
        TabLayout.Tab loginTab = tabLayout.newTab();
        loginTab.setText("Log-IN"); // set the Text for the first Tab
        tabLayout.addTab(loginTab, true); // add  the tab to the TabLayout

        TabLayout.Tab signUpTab = tabLayout.newTab();
        signUpTab.setText("Sign-Up"); // set the Text for the first Tab
        tabLayout.addTab(signUpTab, false); // add  the tab to the TabLayout

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayout_fragment, new UserLogIn());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    fragment = new UserLogIn();
                } else {
                    fragment = new UserSignUp();
                }

                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment, fragment);
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
