package com.example.prakharmaheshwari.holmes;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import dataclasses.HEvents;

import static android.app.Activity.RESULT_OK;

public class PublicEvent extends Fragment {
    private static Button createEvent, chooseImg;
    private static FirebaseAuth auth;
    private static EditText eventTitle, eventStartTime, eventLocation, eventStartDate, eventEndTime, eventEndDate, eventDescription;
    private static int hour, minute, PICK_IMAGE_REQUEST;
    private static FirebaseDatabase eDatabase;
    private static DatabaseReference PublicEvents;
    private static FirebaseUser User;
    private static Uri filePath;
    private static TextView imagePath;
    private static DatabaseReference publicItem;
    private static String p_key;
    private static View view;

    public PublicEvent() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void resetValues() {
        eventTitle.setText("");
        eventLocation.setText("");
        eventStartDate.setText("");
        eventEndDate.setText("");
        eventStartTime.setText("");
        eventEndTime.setText("");
        imagePath.setText("");
        eventDescription.setText("");
    }
    public void init() {
        auth = FirebaseAuth.getInstance();
        User = FirebaseAuth.getInstance().getCurrentUser();
        eDatabase = FirebaseDatabase.getInstance();
        PublicEvents= eDatabase.getReference("Events");

        createEvent = (Button) view.findViewById(R.id.pubevent);
        eventTitle = (EditText) view.findViewById(R.id.editText_title_pu);
        eventStartDate = (EditText) view.findViewById(R.id.editText_std_pu);
        eventEndDate = (EditText) view.findViewById(R.id.editText_edd_pu);
        eventLocation = (EditText) view.findViewById(R.id.editText_location_pu);
        eventStartTime = (EditText) view.findViewById(R.id.editText_stt_pu);
        eventEndTime = (EditText) view.findViewById(R.id.editText_edt_pu);
        chooseImg = (Button) view.findViewById(R.id.btn_chooseImg_pu);
        imagePath = (TextView) view.findViewById(R.id.textImgPath_pu);
        eventDescription = (EditText) view.findViewById(R.id.editText_description_pu);

        PICK_IMAGE_REQUEST = 111;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_public_event, container, false);
        // Inflate the layout for this fragment
        init();

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

            }
        });


        eventStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        String m, d;
                        m = Integer.toString(selectedmonth+1);
                        d = Integer.toString(selectedday);
                        if((selectedmonth+1)<10)
                            m = "0"+m;
                        if(selectedday<10)
                            d = "0"+d;
                        eventStartDate.setText(m+"/"+d+"/"+Integer.toString(selectedyear));
                    }
                },mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.setTitle("Select Start date");
                mDatePicker.show();
            }

        });

        eventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        String m, d;
                        m = Integer.toString(selectedmonth+1);
                        d = Integer.toString(selectedday);
                        if((selectedmonth+1)<10)
                            m = "0"+m;
                        if(selectedday<10)
                            d = "0"+d;
                        eventEndDate.setText(m+"/"+d+"/"+Integer.toString(selectedyear));
                    }
                },mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.setTitle("Select End date");

                mDatePicker.show();
            }

        });

        eventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Activity has to implement this interface
                TimePickerDialog mTimePicker=new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker timepicker, int selectedhour, int selectedminute) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time*/
                        String h, m;
                        if(selectedhour<10)
                            h = "0"+Integer.toString(selectedhour);
                        else
                            h = Integer.toString(selectedhour);
                        if(selectedminute<10)
                            m = "0"+Integer.toString(selectedminute);
                        else
                            m = Integer.toString(selectedminute);
                        eventStartTime.setText(h+":"+m);
                    }
                },hour, minute, true);
                mTimePicker.setTitle("Select Start time");
                mTimePicker.show();
            }
        });

        eventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                // Activity has to implement this interface
                TimePickerDialog mTimePicker=new TimePickerDialog(getActivity(), R.style.DialogTheme,new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker timepicker, int selectedhour, int selectedminute) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time*/
                        String h, m;
                        if(selectedhour<10)
                            h = "0"+Integer.toString(selectedhour);
                        else
                            h = Integer.toString(selectedhour);
                        if(selectedminute<10)
                            m = "0"+Integer.toString(selectedminute);
                        else
                            m = Integer.toString(selectedminute);
                        eventEndTime.setText(h+":"+m);
                    }
                },hour, minute, true);
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = eventTitle.getText().toString();
                String location = eventLocation.getText().toString();
                String sdate = eventStartDate.getText().toString();
                String edate = eventEndDate.getText().toString();
                String stime = eventStartTime.getText().toString();
                String etime = eventEndTime.getText().toString();
                String edescription = eventDescription.getText().toString();


                if (title.length() ==0) {
                    Toast.makeText(getContext(), "Enter event title!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (location.length() ==0) {
                    Toast.makeText(getContext(), "Enter event location!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sdate.length() ==0) {
                    Toast.makeText(getContext(), "Enter event start date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edate.length() ==0) {
                    Toast.makeText(getContext(), "Enter event end date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (stime.length() ==0) {
                    Toast.makeText(getContext(), "Enter event start time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etime.length() ==0) {
                    Toast.makeText(getContext(), "Enter event end time!", Toast.LENGTH_SHORT).show();
                    return;
                }
                publicItem = PublicEvents.push();
                p_key =  publicItem.getKey();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("Events");    //change the url according to your firebase app
                StorageReference childRef = storageRef.child(p_key+".png");

                UploadTask uploadTask;
                //uploading the image
                if(!imagePath.getText().equals(""))
                    uploadTask = childRef.putFile(filePath);


                HEvents hEvents = new HEvents(
                        User.getDisplayName(),
                        title,
                        location,
                        sdate,
                        edate,
                        stime,
                        etime,
                        edescription,
                        "public"
                );


                publicItem.setValue(hEvents);

                Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show();

                resetValues();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                //Setting image to ImageView
                Log.d("publicEvents", String.valueOf(filePath));
                imagePath.setText((String.valueOf(filePath)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

