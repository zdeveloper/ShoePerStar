package com.uta.shoeperstar.vibe.Fragment;


import android.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uta.shoeperstar.vibe.R;


public class DashboardFragment extends Fragment {

    private TextView stepCount;
    private TextView distance;

    public DashboardFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do stuff before the ui is loaded
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);




        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        //do stuff here after ui is initialized
        stepCount= (TextView) view.findViewById(R.id.stepCount);
        distance = (TextView) view.findViewById(R.id.distance);

        //send battery level for left shoe

        //send battery level for right shoe


        //send step count to UI
        stepCount.setText("n/a");

        //send distance to UI
        distance.setText("n/a");

    }
}
