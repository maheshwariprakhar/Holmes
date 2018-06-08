package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class ProductAdapter extends BaseAdapter{

    private Context mContext = null;
    private int mResourceId;
    private List<HashMap<String, String>> mItems;
    private FirebaseDatabase eDatabase;
    private DatabaseReference eUsers;
    private String uname;
    private static FirebaseUser User;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("Products");
    public ProductAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
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

        TextView product_title = (TextView) itemView.findViewById(R.id.product_title);
        final TextView product_owner = (TextView) itemView.findViewById(R.id.product_owner);
        TextView product_price = (TextView) itemView.findViewById(R.id.product_price);
        ImageView event_img = (ImageView) itemView.findViewById(R.id.product_list_img);
        eDatabase = FirebaseDatabase.getInstance();
        eUsers = eDatabase.getReference("Products");



        final HashMap<String, String> hashMap = mItems.get(position);
        // Load the image using Glide

        //IGNORE IF NULL
        Glide.with(mContext /* context */)
                .using(new FirebaseImageLoader())
                .load(avatars.child(hashMap.get("p_key")+".png"))
                .into(event_img);


        //include hosted by you
        Log.d("host", User.getUid());


        product_title.setText(hashMap.get("title"));
        if(User.getDisplayName().equals(hashMap.get("owner")))
            product_owner.setText(product_owner.getText()+" You");
        else
            product_owner.setText(product_owner.getText()+" "+hashMap.get("owner"));
        product_price.setText("$ "+hashMap.get("price"));
        //event_time.setText(hashMap.get("start_date")+", "+hashMap.get("start_time")+" - "+hashMap.get("end_date")+", "+hashMap.get("end_time"));


        return itemView;
    }
}

