package com.example.prakharmaheshwari.holmes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

    public class FAQ extends Fragment {




        public FAQ() {
            // Required empty public constructor

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.faq_page, container, false);
            getActivity().setTitle("FAQs");
            return view;
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onResume() {
            super.onResume();


        }

    }

