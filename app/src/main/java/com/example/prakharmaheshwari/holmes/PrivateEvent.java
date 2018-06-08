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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import dataclasses.HEvents;

import static android.app.Activity.RESULT_OK;

public class PrivateEvent extends Fragment {
    private static Button createEvent, chooseImg;
    private static FirebaseAuth auth;
    private static EditText eventTitle, eventStartTime, eventLocation, eventStartDate, eventEndTime, eventEndDate, eventDescription;
    private static int hour, minute, PICK_IMAGE_REQUEST;
    private static FirebaseDatabase eDatabase;
    private static DatabaseReference PrivateEvents;
    private static FirebaseUser User;
    private static Uri filePath;
    private static TextView imagePath;
    private static DatabaseReference privateItem;
    private static String p_key;
    private static View view;
    private static MultiAutoCompleteTextView invitePeople, inviteGroup;

    public PrivateEvent() {
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
        invitePeople.setText("");
        inviteGroup.setText("");
    }
    public void init() {
        auth = FirebaseAuth.getInstance();
        User = FirebaseAuth.getInstance().getCurrentUser();
        eDatabase = FirebaseDatabase.getInstance();
        PrivateEvents= eDatabase.getReference("Events");

        createEvent = (Button) view.findViewById(R.id.privevent);
        eventTitle = (EditText) view.findViewById(R.id.editText_title_pr);
        eventStartDate = (EditText) view.findViewById(R.id.editText_std_pr);
        eventEndDate = (EditText) view.findViewById(R.id.editText_edd_pr);
        eventLocation = (EditText) view.findViewById(R.id.editText_location_pr);
        eventStartTime = (EditText) view.findViewById(R.id.editText_stt_pr);
        eventEndTime = (EditText) view.findViewById(R.id.editText_edt_pr);
        chooseImg = (Button) view.findViewById(R.id.btn_chooseImg_pr);
        imagePath = (TextView) view.findViewById(R.id.textImgPath_pr);
        eventDescription = (EditText) view.findViewById(R.id.editText_description_pr);
        invitePeople = (MultiAutoCompleteTextView) view.findViewById(R.id.invitePeople_pr);
        inviteGroup = (MultiAutoCompleteTextView) view.findViewById(R.id.inviteGroup_pr);
        PICK_IMAGE_REQUEST = 111;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_private_event, container, false);
        final ArrayList<String> allUsers = new ArrayList<>();
        final ArrayList<String> allGroups = new ArrayList<>();
        // Inflate the layout for this fragment
        init();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userName = ds.child("userName").getValue(String.class);
                    String name = ds.child("firstName").getValue(String.class);
                    Log.d("name",""+name);
                    Log.d("name",""+userName);
                    name = name + " (" + userName + ")";
                    allUsers.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,allUsers);
        invitePeople.setThreshold(0);
        invitePeople.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        invitePeople.setAdapter(arrayAdapter);

        Log.d("Events", User.getUid());
        database.getReference().child("Users/" + User.getUid() + "/groups" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    Log.d("name",""+name);
                    allGroups.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> arrayGroupAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,allGroups);
        inviteGroup.setThreshold(1);
        inviteGroup.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        inviteGroup.setAdapter(arrayGroupAdapter);

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
                mTimePicker.setTitle("Select Start Time");
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
                TimePickerDialog mTimePicker=new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker timepicker, int selectedhour, int selectedminute) {
                        // TODO Auto-generated method stub
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
                String[] epeople = invitePeople.getText().toString().split(",");
                String[] egroup = inviteGroup.getText().toString().split(",");
                HashMap<String, String> hpeople = new HashMap<>();
                HashMap<String, String> hgroups = new HashMap<>();

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

                if (epeople.length ==0) {
                    Toast.makeText(getContext(), "Invite People!", Toast.LENGTH_SHORT).show();
                    return;
                }


                privateItem = PrivateEvents.push();
                DatabaseReference privateGroup = privateItem;
                p_key =  privateItem.getKey();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://holmes-268.appspot.com").child("Events");    //change the url according to your firebase app
                StorageReference childRef = storageRef.child(p_key+".png");
                UploadTask uploadTask;
                //uploading the image
                if(!imagePath.getText().equals(""))
                    uploadTask = childRef.putFile(filePath);

                String host = User.getDisplayName();
                HEvents hEvents = new HEvents(
                        host,
                        title,
                        location,
                        sdate,
                        edate,
                        stime,
                        etime,
                        edescription,
                        "private"
                );


                privateItem.setValue(hEvents);
                int i=0;
                privateItem = privateItem.child("peopleInvited");
                while(i<epeople.length-1) {
                    if(i!=0) {
                        epeople[i]= epeople[i].substring(1, epeople[i].length());
                    }
                    hpeople.putIfAbsent(epeople[i], "Invited"+(i+1));
                    i++;
                }
                //Fix
                privateItem.setValue(hpeople);

                privateGroup = privateGroup.child("groupsInvited");
                i=0;
                while(i<egroup.length-1) {
                    if(i!=0) {
                        egroup[i]= egroup[i].substring(1, egroup[i].length());
                    }
                    hgroups.putIfAbsent(egroup[i], "Invited"+(i+1));
                    i++;
                }
                privateGroup.setValue(hgroups);
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
                Log.d("privateEvents", String.valueOf(filePath));
                imagePath.setText((String.valueOf(filePath)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

