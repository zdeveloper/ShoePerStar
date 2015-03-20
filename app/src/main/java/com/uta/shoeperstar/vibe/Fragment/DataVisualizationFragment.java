package com.uta.shoeperstar.vibe.Fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.uta.shoeperstar.vibe.R;


public class DataVisualizationFragment extends Fragment {
    ImageButton stepCountButton;
    ImageButton heartBeatButton;
    LineChart stepChart;
    LineChart pulseChart;

    public DataVisualizationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_data_visualization, container, false);

        stepCountButton = (ImageButton) view.findViewById(R.id.stepCountButton);
        heartBeatButton = (ImageButton) view.findViewById(R.id.heartBeatButton);
        stepChart = (LineChart) view.findViewById(R.id.stepChart);
        stepChart.setVisibility(View.GONE);
        stepChart.setNoDataTextDescription("Nothing In Step Chart");
        stepChart.setNoDataText("Step Chart");
        pulseChart = (LineChart) view.findViewById(R.id.pulseChart);
        pulseChart.setVisibility(View.GONE);
        pulseChart.setNoDataTextDescription("Nothing In Pulse Chart");
        pulseChart.setNoDataText("Pulse Chart");

        return view;

        //link UI here


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        stepCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseChart.setVisibility(View.GONE);
//                stepChart.invalidate();
                stepChart.setVisibility(View.VISIBLE);
            }
        });

        heartBeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stuff to change to HeartBeat fragment
                stepChart.setVisibility(View.GONE);
//                pulseChart.invalidate();
                pulseChart.setVisibility(View.VISIBLE);
            }
        });

        //do stuff here after ui is initialized
    }


}
