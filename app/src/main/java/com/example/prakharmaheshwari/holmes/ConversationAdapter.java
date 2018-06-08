package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConversationAdapter extends BaseAdapter {
    private static final String TAG = "ConversationAdapter";
    private Context mContext = null;
    private int mResourceId;
    private List<HashMap<String, String>> mItems;
    private FirebaseDatabase mDatabase;
    private DatabaseReference Images;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference();
    StorageReference avatars = imagesRef.child("User_Avatar");
    public ConversationAdapter(Context context, int Resourceid, List<HashMap<String, String>> items){
        mContext = context;
        mItems = items;
        mResourceId = Resourceid;
        mDatabase = FirebaseDatabase.getInstance();
        Images= mDatabase.getReference("Images");
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

        TextView contact_name = (TextView) itemView.findViewById(R.id.contact_name);
        TextView contact_time = (TextView) itemView.findViewById(R.id.contact_time);
        TextView contact_text = (TextView) itemView.findViewById(R.id.contact_text);
        TextView unreadcount = (TextView) itemView.findViewById(R.id.unreadcount);
        final ImageView contact_avatar = (ImageView) itemView.findViewById(R.id.contact_avatar);
        HashMap<String, String> hashMap = mItems.get(position);
        // Load the image using Glide
    /*    try {
            StorageReference storageReference = avatars.child(hashMap.get("contact_avatar"));

            final File localFile = File.createTempFile(hashMap.get("contact_id"), "png", mContext.getCacheDir());
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    contact_avatar.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.d(TAG, exception.getMessage());
                }
            });
        } catch (IOException e ) {}*/

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(avatars.child(hashMap.get("contact_avatar")))
                .into(contact_avatar);/*
        avatars.child(hashMap.get("contact_avatar"))
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(mContext)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(contact_avatar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/
        contact_name.setText(hashMap.get("contact_name"));
        contact_time.setText(hashMap.get("contact_time"));
        contact_text.setText(hashMap.get("contact_text"));

        if (hashMap.get("unreadcount") == null || hashMap.get("unreadcount").equals("0")) {
            unreadcount.setVisibility(View.GONE);
        } else {
            unreadcount.setVisibility(View.VISIBLE);
            unreadcount.setText(hashMap.get("unreadcount"));
        }

        return itemView;
    }
}
