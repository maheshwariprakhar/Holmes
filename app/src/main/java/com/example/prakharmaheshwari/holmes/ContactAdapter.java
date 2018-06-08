package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class ContactAdapter extends BaseAdapter {
    private Context mContext = null;
    private int mResourceId;
    private List<HashMap<String, String>> mItems;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("User_Avatar");
    public ContactAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
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


        TextView contact_name = (TextView) itemView.findViewById(R.id.nametext);
        ImageView contact_avatar = (ImageView) itemView.findViewById(R.id.avatarimg);
        HashMap<String, String> hashMap = mItems.get(position);
        // Load the image using Glide
        Glide.with(mContext /* context */)
                .using(new FirebaseImageLoader())
                .load(avatars.child(hashMap.get("contact_avatar")))
                .into(contact_avatar);
        /*avatars.child(hashMap.get("contact_avatar"))
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/
        contact_name.setText(hashMap.get("fullname")+"(" +hashMap.get("contact_name")+")");

        return itemView;
    }
}
