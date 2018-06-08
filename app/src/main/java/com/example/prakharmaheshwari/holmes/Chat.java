package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dataclasses.HConversation;
import dataclasses.HMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Chat.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */


public class Chat extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Chat";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference Messages;
    private DatabaseReference Conversations;
    private FirebaseUser User;
    private String Chat_name;
    private String Chat_id;
    private final List<HashMap<String, String>> messagesList = new ArrayList<>();
    private Button Send_Message;
    private EditText Message_text;
    private Button chat_user_profile;
    private TextView chat_user_name;

    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1, String param2) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        Chat_name = bundle.getString("receiver_name");
        Chat_id = bundle.getString("receiver_id");

        final ListView chatlistview = view.findViewById(R.id.chatlists);
        User = FirebaseAuth.getInstance().getCurrentUser();
        messagesList.clear();
        mDatabase = FirebaseDatabase.getInstance();
        Messages= mDatabase.getReference("Messages");
        Conversations = mDatabase.getReference("Conversations");
        chat_user_name = view.findViewById(R.id.chat_user_name);
        chat_user_profile = view.findViewById(R.id.chat_user_profile);
        Message_text = view.findViewById(R.id.Message_text);
        Send_Message = view.findViewById(R.id.Send_Message);
        chat_user_name.setText(Chat_name);
        Send_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Message_text.getText()))
                    return;
                final String mtext = Message_text.getText().toString().trim();
                final Object timestamp = ServerValue.TIMESTAMP;
                Message_text.setText("");
                //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Conversations.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean User_Contact = false, Contact_User = false;
                            for (DataSnapshot child: dataSnapshot.getChildren()) {
                                HConversation hc = child.getValue(HConversation.class);
                                if (hc.getProviderid().equals( User.getUid())
                                        && hc.getReceiverid().equals(Chat_id)) {
                                    //already have record for usr to contact
                                    //need update
                                    User_Contact = true;
                                    child.getRef().setValue(new HConversation(User.getUid(), User.getDisplayName(), Chat_id, Chat_name, timestamp, mtext, 0));
//                                    Conversations.child(child.getKey()).child("lasttext").setValue(mtext);
//                                    Conversations.child(child.getKey()).child("lasttime").setValue(timestamp);
                                }
                                if (hc.getProviderid().equals(Chat_id)
                                        && hc.getReceiverid().equals(User.getUid())) {
                                    //already have record for contact to usr
                                    //need update
                                    Contact_User = true;
                                    child.getRef().setValue(new HConversation(Chat_id, Chat_name, User.getUid(), User.getDisplayName(), timestamp, mtext, ((hc.getUnreadcount()==null)?1:hc.getUnreadcount()+1)));
                                    //Conversations.child(child.getKey()).child("lasttext").setValue(mtext);
                                    //Conversations.child(child.getKey()).child("lasttime").setValue(timestamp);
                                }
                            }
                            if (User_Contact == false) {
                                HConversation hConversation = new HConversation(User.getUid(),
                                        User.getDisplayName(),
                                        Chat_id,
                                        Chat_name,
                                        timestamp,
                                        mtext,
                                        0);
                                Conversations.push().setValue(hConversation);
                            }
                            if (Contact_User == false) {
                                HConversation hConversation = new HConversation(Chat_id,
                                        Chat_name,
                                        User.getUid(),
                                        User.getDisplayName(),
                                        timestamp,
                                        mtext,
                                        1);
                                Conversations.push().setValue(hConversation);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                HMessage hMessage = new HMessage(User.getUid(),
                        User.getDisplayName(),
                        Chat_id,
                        Chat_name,
                        timestamp,
                        mtext,false);
                Messages.push().setValue(hMessage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "Error in sending new message: "+databaseError.getMessage());
                        } else {
                            Log.d(TAG, "New Message send.");
                        }
                    }
                });
                //Update Conversations
            }
        });


        final ChatAdapter chatAdapter = new ChatAdapter(getContext(), R.layout.chat_list_layout, messagesList);
        chatlistview.setAdapter(chatAdapter);

        Messages.orderByChild("time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists())
                    return;
                HMessage hMessage = dataSnapshot.getValue(HMessage.class);
                if (!(hMessage.getProviderid().equals(User.getUid()) && hMessage.getReceiverid().equals(Chat_id))
                        && !(hMessage.getReceiverid().equals(User.getUid()) && hMessage.getProviderid().equals(Chat_id))
                        )
                    return;

                Log.d(TAG, "Add one new message.");
                HashMap<String, String> hashMap = new HashMap<>();
                if (!hMessage.getProviderid().equals(User.getUid())) {
                    hashMap.put("isleft", "true");
                } else {
                    hashMap.put("isleft", "false");
                }
                hashMap.put("chat_text", hMessage.getText());
                //new message
                if (hMessage.isread == false && hMessage.getReceiverid().equals(User.getUid()))
                    Messages.child(dataSnapshot.getKey()).child("isread").setValue(true);
                messagesList.add(hashMap);

                chatAdapter.notifyDataSetChanged();

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

        chat_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to profile with Chat_ID
                Intent intent = new Intent(getContext(), PublicUserProfile.class);
                intent.putExtra("userID", Chat_id);
                startActivity(intent);
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
        //update conversation
        //unread message count set 0
        Conversations.orderByChild("providerid")
                .equalTo(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        HConversation hc = child.getValue(HConversation.class);
                        if (hc.getReceiverid().equals(Chat_id)) {
                            child.getRef().child("unreadcount").setValue(0);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
