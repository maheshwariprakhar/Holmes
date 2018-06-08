package com.example.prakharmaheshwari.holmes;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import dataclasses.HConversation;
import dataclasses.HUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Messages.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Messages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Messages extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Conversations";
    private final String[] RemoveContact = {""};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference Messages;
    private DatabaseReference Conversations;
    private FirebaseUser User;
    private List<HashMap<String, String>> conversationList = new ArrayList<>();

    public Messages() {

    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Messages.
     */
    // TODO: Rename and change types and number of parameters
    public static Messages newInstance(String param1, String param2) {
        Messages fragment = new Messages();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.getBoolean("Forward_to_Message")) {
            ForwardtoChat(args.getString("receiver_id"), args.getString("receiver_name"));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ListView messagelists = view.findViewById(R.id.messagelists);
        FloatingActionButton add_new_con = view.findViewById(R.id.add_new_conversation);

        User = FirebaseAuth.getInstance().getCurrentUser();
        conversationList.clear();
        mDatabase = FirebaseDatabase.getInstance();
        Messages= mDatabase.getReference("Messages");
        Conversations = mDatabase.getReference("Conversations");

        //user can add new conversation by click this button
        add_new_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new listview in dialogue for user choosing contacts
                ListView contacterlistview = new ListView(getContext());
                final List<HashMap<String, String>> contactList = new ArrayList<>();
                DatabaseReference Users = mDatabase.getReference("Users");
                //Users.push().setValue(new HUser("7W6zNf3EbyYs9Y8M8rkSjWCTmUK2", "Ape_Su", "Yipeng", "Su", "mr.xiaosusu@gmail.com"));

                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Choose contact")
                        .setView(contacterlistview)
                        .create();
                //customized adapter with data
                final ContactAdapter contactAdapter = new ContactAdapter(getContext(), R.layout.contact_list_layout, contactList);
                contacterlistview.setAdapter(contactAdapter);
                //set onclick function on listview's item
                contacterlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> o = (HashMap<String, String>) parent.getItemAtPosition(position);
                        Log.d(TAG, "choose new contact");
                        //check whether converasation exists
                        for (HashMap<String, String> hashMap : conversationList) {
                            if (hashMap.get("contact_id").equals(o.get("userid"))) {
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        //create new conversation with
                        Conversations.push().setValue(new HConversation(User.getUid(), User.getDisplayName(), o.get("userid"), o.get("contact_name"),ServerValue.TIMESTAMP, "", 0));
                        //Conversations.push().setValue(new HConversation(o.get("userid"), User.getUid(), ServerValue.TIMESTAMP, ""));
                        alertDialog.dismiss();
                    }
                });
                //update contacts list
                Users.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.getKey().equals(User.getUid()))
                                    continue;
                                HUser huser = child.getValue(HUser.class);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("userid", child.getKey());
                                hashMap.put("fullname", huser.getFullName());
                                hashMap.put("email", huser.getEmailAddress());
                                hashMap.put("contact_name", huser.getUserName());
                                hashMap.put("contact_avatar", child.getKey()+".png");
                                contactList.add(hashMap);
                            }
                            contactAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                alertDialog.show();
            }
        });

        final ConversationAdapter conversationAdapter = new ConversationAdapter(this.getContext(), R.layout.message_list_layout,conversationList);
        messagelists.setAdapter(conversationAdapter);
        messagelists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> o = (HashMap<String, String>) parent.getItemAtPosition(position);
                ForwardtoChat(o.get("contact_id"), o.get("contact_name"));
            }
        });
        /*messagelists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> o = (HashMap<String, String>) parent.getItemAtPosition(position);
                //remove conversation from user to receiver
                RemoveContact[0] = o.get("contact_id");
                Conversations.orderByChild("providerid")
                        .equalTo(User.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child: dataSnapshot.getChildren()) {
                                HConversation hC = child.getValue(HConversation.class);
                                if (hC.getReceiverid().equals(RemoveContact[0])) {
                                    //should we delete the conversation
                                    //child.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }
        });*/

        Conversations.orderByChild("providerid")
                .equalTo(User.getUid())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists())
                    return;
                HConversation hConversation = dataSnapshot.getValue(HConversation.class);
                Log.d(TAG, "Add one new conversation.");

                String time = hConversation.getLasttime().toString();
                Long t = Long.parseLong(time);
                Date date = new Date(t);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("contact_id", hConversation.getReceiverid());
                hashMap.put("contact_name", hConversation.getReceivername());
                hashMap.put("sender_id", hConversation.getProviderid());
                hashMap.put("sender_name", hConversation.getReceivername());
                hashMap.put("contact_time", timeAgo(date));
                hashMap.put("contact_text", hConversation.getLasttext());
                hashMap.put("contact_avatar", hConversation.getReceiverid()+".png");
                hashMap.put("unreadcount", (hConversation.getUnreadcount()==null)?"0":hConversation.getUnreadcount().toString());
                //hashMap.put("unreadcount", hConversation.getUnreadcount().toString());
                Boolean flag = true;
                for (HashMap<String, String> h : conversationList) {
                    if (h.get("contact_id").equals(hConversation.getReceiverid())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {

                    conversationList.add(hashMap);

                    conversationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HConversation hConversation = dataSnapshot.getValue(HConversation.class);
                Log.d(TAG, "Change one conversation.");
                if (hConversation.getProviderid() == null)
                    return;
                String time = hConversation.getLasttime().toString();
                Long t = Long.parseLong(time);
                Date date = new Date(t*1000);
                for (HashMap<String, String> hashMap : conversationList) {
                    if (hashMap.get("sender_id").equals(hConversation.getProviderid())
                            && hashMap.get("contact_id").equals(hConversation.getReceiverid())) {
                        hashMap.put("contact_time", timeAgo(date));
                        hashMap.put("contact_text", hConversation.getLasttext());
                        hashMap.put("unreadcount", (hConversation.getUnreadcount()==null)?"0":hConversation.getUnreadcount().toString());
                    }
                }

                conversationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error conversation."+databaseError.getMessage());
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void ForwardtoChat(String receiver_id, String receiver_name) {
        //replace layout in content_frame to chat page
        Fragment chat = new Chat();
        Bundle bundle = new Bundle();
        bundle.putString("receiver_id", receiver_id);
        bundle.putString("receiver_name", receiver_name);
        chat.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, chat);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static String timeAgo(Date createdTime) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        if (createdTime != null) {
            long agoTimeInMin = (new Date(System.currentTimeMillis()).getTime() - createdTime.getTime()) / 1000 / 60;

            if (agoTimeInMin <= 1) {
                return "just now";
            } else if (agoTimeInMin <= 60) {
                return agoTimeInMin + " minutes ago";
            } else if (agoTimeInMin <= 60 * 24) {
                return agoTimeInMin / 60 + ((agoTimeInMin/60)==1?" hour ago":" hours ago");
            } else if (agoTimeInMin <= 60 * 24 * 2) {
                return agoTimeInMin / (60 * 24) + ((agoTimeInMin/(60*24))==1?" day ago":" days ago");
            } else {
                return format.format(createdTime);
            }
        } else {
            return format.format(new Date(0));
        }
    }
}
