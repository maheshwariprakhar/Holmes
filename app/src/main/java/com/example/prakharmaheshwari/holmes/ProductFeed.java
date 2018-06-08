package com.example.prakharmaheshwari.holmes;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
import dataclasses.HProducts;

public class ProductFeed extends Fragment{
    private FloatingActionButton addProduct;
    private ListView proList;
    private ArrayList<HashMap<String, String>> proArrayList;
    private DatabaseReference products;
    private FirebaseDatabase pDatabase;
    private FirebaseUser User;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.getBoolean("Forward_to_Product")) {
            ForwardtoProductDetail(args.getString("productid"));
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_products_list, container, false);
        pDatabase = FirebaseDatabase.getInstance();
        User = FirebaseAuth.getInstance().getCurrentUser();
        products= pDatabase.getReference("Products");
        proList = (ListView) view.findViewById(R.id.product_list);
        proArrayList = new ArrayList<>();
        final ProductAdapter adapter=new ProductAdapter(getContext(),R.layout.product_list_layout,proArrayList);
        proList.setAdapter(adapter);

        addProduct = (FloatingActionButton) view.findViewById(R.id.btn_addNewProduct);


        products.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists())
                    return;

                    Log.d("products", dataSnapshot.getKey());
                    HProducts hProducts = dataSnapshot.getValue(HProducts.class);
                    HashMap<String, String> pro = new HashMap<>();
                    pro.put("owner", hProducts.getOwner());
                    pro.put("title", hProducts.getTitle());
                    pro.put("price", hProducts.getPrice());
                    pro.put("description", hProducts.getDescription());
                    pro.put("p_key", dataSnapshot.getKey());
                    Log.d("products", hProducts.getOwner());
                    proArrayList.add(pro);
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

        proList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Events", "choose new contact");
                HashMap<String, String> o = (HashMap<String, String>) parent.getItemAtPosition(position);
                Log.d("Events", "choose new contact");
                for (HashMap<String, String> hashMap : proArrayList) {
                    if (hashMap.get("p_key").equals(o.get("p_key"))) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productid", o.get("p_key"));
                        onClickProductList(bundle);
                        //alertDialog.dismiss();
                        return;
                    }
                }
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), AddProduct.class);
                startActivity(intent1);

            }
        });
        return view;

    }

    void onClickProductList(Bundle bundle) {
        Fragment frag = new ProductDetailFragment();
        frag.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void ForwardtoProductDetail(String productid) {
        Bundle bundle = new Bundle();
        bundle.putString("productid", productid);
        onClickProductList(bundle);

    }
}
