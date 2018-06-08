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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailFragment extends Fragment {
    private String p_key, pName, pPrice, pDescription, pCategories, pOwner;
    private ImageView eventImg;
    private TextView proTitle, proPrice, proDescription, proOwner, proCategories;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("Products");
    private DatabaseReference products;
    private FirebaseDatabase pDatabase;

    public ProductDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            p_key = getArguments().getString("productid");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_products_detail, container, false);
        eventImg = (ImageView) rootView.findViewById(R.id.pro_detail_img);
        proOwner = (TextView) rootView.findViewById(R.id.pro_detail_owner);
        proTitle = (TextView) rootView.findViewById(R.id.pro_detail_title);
        proCategories = (TextView) rootView.findViewById(R.id.pro_detail_categories);
        proDescription = (TextView) rootView.findViewById(R.id.pro_detail_description);
        proPrice = (TextView) rootView.findViewById(R.id.pro_detail_price);
        pDatabase = FirebaseDatabase.getInstance();
        products = pDatabase.getReference("Products");
        pCategories = "";

        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(avatars.child(p_key+".png"))
                .into(eventImg);


        products.child(p_key).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("products1", dataSnapshot.getKey());
                if(dataSnapshot.getKey().equals("title"))
                    pName = dataSnapshot.getValue(String.class);
                if(dataSnapshot.getKey().equals("price"))
                    pPrice = dataSnapshot.getValue(String.class);
                if(dataSnapshot.getKey().equals("owner"))
                    pOwner = dataSnapshot.getValue(String.class);
                if(dataSnapshot.getKey().equals("description"))
                    pDescription = dataSnapshot.getValue(String.class);
                Log.d("products2", Long.toString(dataSnapshot.getChildrenCount()));
                if(dataSnapshot.getKey().equals("categories")) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        pCategories += ds.getKey() + ", ";
                        Log.d("products3", ds.getKey());
                    }
                    //pCategories = pCategories.substring(0, pCategories.length() - 2);
                }
                proTitle.setText(pName);
                proOwner.setText(pOwner);
                proPrice.setText(pPrice);
                proDescription.setText(pDescription);
                proCategories.setText(pCategories);
                //p

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

        return rootView;
    }

}

