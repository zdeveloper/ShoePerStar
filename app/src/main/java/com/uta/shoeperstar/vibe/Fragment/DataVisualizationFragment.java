package com.uta.shoeperstar.vibe.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.Data;
import com.uta.shoeperstar.vibe.Utilities.Database;

import android.util.Log;

import java.util.ArrayList;




public class DataVisualizationFragment extends Fragment {
    ImageButton stepCountButton;
    ImageButton heartBeatButton;
    LineChart stepChart;
    LineChart pulseChart;
    Database db;

//    Database db = new Database(getContext());
    public DataVisualizationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(getActivity(), null, null, 1);
        /* do stuff before the ui is loaded */
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
        // add data
        LineData data = getFakeData(36, 100);
        stepChart.setData(data);

        pulseChart = (LineChart) view.findViewById(R.id.pulseChart);
        pulseChart.setVisibility(View.GONE);
        pulseChart.setNoDataTextDescription("Nothing In Pulse Chart");
        pulseChart.setNoDataText("Pulse Chart");
        LineData data2 = getFakeData(36, 100);
        pulseChart.setData(data2);


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
                stepCountButton.setBackgroundResource(R.drawable.ic_active);
                heartBeatButton.setBackgroundResource(R.drawable.ic_heart_light);



                stepChart.animateX(2500);
            }
        });

        heartBeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stuff to change to HeartBeat fragment

                Data data1 = new Data(60, 4, 5);
                Data data2 = new Data(67, 5, 6);
                Data data3 = new Data(68, 5, 2);
                Data data4 = new Data(78, 4, 7);
                Data data5 = new Data(65, 8, 2);
                Data data6 = new Data(63, 7, 9);
                db.addPulse(data1);
                db.addPulse(data2);
                db.addPulse(data3);
                db.addPulse(data4);
                db.addPulse(data5);
                db.addPulse(data6);
                ArrayList<Data> results = new ArrayList<Data>();
                results = db.getPulse();
                for(int i = 0; i < results.size(); i ++) {
                    Log.d("Reuslts", "" + results.get(i).getData());
                }
                stepChart.setVisibility(View.GONE);
//                pulseChart.invalidate();
                pulseChart.setVisibility(View.VISIBLE);
                stepCountButton.setBackgroundResource(R.drawable.ic_active_light);
                heartBeatButton.setBackgroundResource(R.drawable.ic_heart);

                pulseChart.animateX(2500);
            }
        });

        //do stuff here after ui is initialized
    }


    private LineData getFakeData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(i % 12+"");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1.75f);
        set1.setCircleSize(3f);
        int blue = getResources().getColor(R.color.primary);
        set1.setColor(blue);
        set1.setCircleColor(blue);
        set1.setHighLightColor(blue);
        set1.setDrawValues(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        return data;
    }


}
