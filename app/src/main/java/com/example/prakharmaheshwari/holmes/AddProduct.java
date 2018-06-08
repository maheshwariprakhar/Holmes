package com.example.prakharmaheshwari.holmes;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import dataclasses.HProducts;

public class AddProduct extends AppCompatActivity {

    private EditText pTitle, pPrice, pDescription;
    private Button pImg, createProduct;
    private MultiAutoCompleteTextView pCategories;
    private static FirebaseDatabase pDatabase;
    private static DatabaseReference Products, pro;
    private static FirebaseUser User;
    private static Uri filePath;
    private TextView imagePath;
    private static int PICK_IMAGE_REQUEST;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        pTitle = (EditText) findViewById(R.id.product_title);
        pPrice = (EditText) findViewById(R.id.product_price);
        pDescription = (EditText) findViewById(R.id.product_description);
        pImg = (Button) findViewById(R.id.product_Img_btn);
        createProduct = (Button) findViewById(R.id.createProduct);
        pCategories = (MultiAutoCompleteTextView) findViewById(R.id.product_categories);
        imagePath = (TextView) findViewById(R.id.product_Img);
        pDatabase = FirebaseDatabase.getInstance();
        Products= pDatabase.getReference("Products");
        User = FirebaseAuth.getInstance().getCurrentUser();

        PICK_IMAGE_REQUEST = 111;


        pImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

            }
        });

        createProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pTitle.length() ==0) {
                    Toast.makeText(getApplicationContext(), "Enter product's name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pPrice.length() ==0) {
                    Toast.makeText(getApplicationContext(), "Enter pice!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pCategories.length() ==0) {
                    Toast.makeText(getApplicationContext(), "Enter category!", Toast.LENGTH_SHORT).show();
                    return;
                }

                pro = Products.push();
                String p_key =  pro.getKey();


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("Products");    //change the url according to your firebase app
                StorageReference childRef = storageRef.child(p_key+".png");

                UploadTask uploadTask;
                //uploading the image
                if(!imagePath.getText().equals(""))
                    uploadTask = childRef.putFile(filePath);
                String owner = User.getDisplayName();
                String title = pTitle.getText().toString();
                String price = pPrice.getText().toString();
                String desc = pDescription.getText().toString();
                HProducts hProducts = new HProducts(owner, title , price, desc, User.getUid());
                String[] categories = pCategories.getText().toString().split(",");
                HashMap<String, String> hpro = new HashMap<>();
                pro.setValue(hProducts);
                if(categories.length>0) {
                    pro = pro.child("categories");
                    int i=0;
                    for (String category : categories) {
                        if(i!=0) {
                            categories[i]= categories[i].substring(1, categories[i].length());
                        }
                        hpro.putIfAbsent(categories[i], "Category"+(i+1));
                        i++;
                    }
                }
                pro.setValue(hpro);
                Toast.makeText(getApplicationContext(), "Product added!", Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);

                //Setting image to ImageView
                Log.d("products", String.valueOf(filePath));
                imagePath.setText((String.valueOf(filePath)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

