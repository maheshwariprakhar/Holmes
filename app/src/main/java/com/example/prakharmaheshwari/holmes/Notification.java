package com.example.prakharmaheshwari.holmes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import SqlLite.NotificationsDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Notification.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notification extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String QUERY_ALL_NOTIFICATIONS = "select notificationid, type, Hid, Hname, Hostid, Hostname, timestamp from Notification";
    private static final String TAG = "Notification Page";
    private final static int Event_Type = 11;
    private final static int Product_Type = 12;
    private final static int Group_Type = 13;
    private NotificationsDbHelper dbHelper;
    private SQLiteDatabase sqliteDatabase;
    private List<HashMap<String, String>> notificationlist = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Notification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Notification.
     */
    // TODO: Rename and change types and number of parameters
    public static Notification newInstance(String param1, String param2) {
        Notification fragment = new Notification();
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
        dbHelper = new NotificationsDbHelper(getContext(), "Notifications.db", null, 1);
        sqliteDatabase = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ListView notificationlistview = view.findViewById(R.id.notificationlists);
        //query data from sqlite database Notification

        Cursor result = sqliteDatabase.rawQuery(QUERY_ALL_NOTIFICATIONS, null);
        while (result.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("notificationid", String.valueOf(result.getInt(0)));
            hashMap.put("type", String.valueOf(result.getInt(1)));
            hashMap.put("Hid", result.getString(2));
            hashMap.put("Hname", result.getString(3));
            hashMap.put("Hostid", result.getString(4));
            hashMap.put("Hostname", result.getString(5));
            hashMap.put("timestamp", result.getString(6));
            notificationlist.add(hashMap);
        }
        result.close();

        final NotificationAdapter notificationAdapter = new NotificationAdapter(this.getContext(), R.layout.notification_list_layout, notificationlist);
        notificationlistview.setAdapter(notificationAdapter);

        notificationlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hashMap = (HashMap<String, String>) parent.getItemAtPosition(position);
                int type = Integer.valueOf(hashMap.get("type"));
                if (type == Event_Type) {
                    String eventid = hashMap.get("Hid");
                    String eventname = hashMap.get("Hname");
                    Intent intent = new Intent(getContext(), EventFeedPage.class);
                    intent.putExtra("eventid", eventid);
                    intent.putExtra("Forward_to_Event", true);
                    startActivity(intent);
                } else if (type == Product_Type) {
                    String productid = hashMap.get("Hid");
                    Intent intent = new Intent(getContext(), EventFeedPage.class);
                    intent.putExtra("Forward_to_Product", true);
                    intent.putExtra("productid", productid);
                    startActivity(intent);

                } else if (type == Group_Type){
                    Intent intent = new Intent(getContext(), EventFeedPage.class);
                    intent.putExtra("Forward_to_Group", true);
                    startActivity(intent);
                } else
                    Log.d(TAG, "unknown notification type");
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
}
