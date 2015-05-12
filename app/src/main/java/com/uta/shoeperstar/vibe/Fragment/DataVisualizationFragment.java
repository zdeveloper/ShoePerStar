package com.uta.shoeperstar.vibe.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.Data;
import com.uta.shoeperstar.vibe.Utilities.Database;

import android.util.Log;
import android.widget.Spinner;

import java.util.ArrayList;




public class DataVisualizationFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    ImageButton stepCountButton;
    ImageButton heartBeatButton;
    Spinner timeSpinner;
    LineChart stepChart;
    LineChart pulseChart;
    Database db;
    String timep = "minute";
    int graphFlag=-1;

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
        timeSpinner = (Spinner) view.findViewById(R.id.timeSpinner);




        stepChart = (LineChart) view.findViewById(R.id.stepChart);
        stepChart.setVisibility(View.GONE);
        stepChart.setNoDataTextDescription("Nothing In Step Chart");
        stepChart.setNoDataText("Step Chart");
        // add data
//        LineData data = getData();
//        stepChart.setData(data);

        pulseChart = (LineChart) view.findViewById(R.id.pulseChart);
        pulseChart.setVisibility(View.GONE);
        pulseChart.setNoDataTextDescription("Nothing In Pulse Chart");
        pulseChart.setNoDataText("Pulse Chart");


//        Data data1 = new Data(80);
//        Data data2 = new Data(85);
//        Data data3 = new Data(84);
//        Data data4 = new Data(83);
//        Data data5 = new Data(82);
//        Data data6 = new Data(81);
//
//        db.addPulse(data1);
//        db.addPulse(data2);
//        db.addPulse(data3);
//        db.addPulse(data4);
//        db.addPulse(data5);
//        db.addPulse(data6);
//
//
//        db.addStepCount(data1);
//        db.addStepCount(data2);
//        db.addStepCount(data3);
//        db.addStepCount(data4);
//        db.addStepCount(data5);
//        db.addStepCount(data6);


        return view;

        //link UI here


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.timeArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        timeSpinner.setAdapter(adapter);
        timeSpinner.setOnItemSelectedListener(this);


        stepCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
;               graphFlag = 0; //sets graph flag to step Count aka 0
                pulseChart.setVisibility(View.GONE);
//                stepChart.invalidate();
                stepChart.setVisibility(View.VISIBLE);
                stepCountButton.setBackgroundResource(R.drawable.ic_active);
                heartBeatButton.setBackgroundResource(R.drawable.ic_heart_light);


                LineData stepdata = getData("Step", timep);
                stepChart.setData(stepdata);


                stepChart.animateX(2500);
            }
        });

        heartBeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphFlag = 1;
                stepChart.setVisibility(View.GONE);
//                pulseChart.invalidate();
                pulseChart.setVisibility(View.VISIBLE);
                stepCountButton.setBackgroundResource(R.drawable.ic_active_light);
                heartBeatButton.setBackgroundResource(R.drawable.ic_heart);
                LineData Pulsedata = getData("Pulse", timep);
                pulseChart.setData(Pulsedata);

                pulseChart.animateX(2500);
            }
        });

        //do stuff here after ui is initialized
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        timep= parent.getItemAtPosition(pos).toString();
//        Log.d("graph flag","" + graphFlag);
        if (graphFlag == 0) { // graph is on step count
            LineData stepdata = getData("Step", timep);
            stepChart.setData(stepdata);
            stepChart.animateX(1500);
        }
        else if(graphFlag == 1){ // graph is on Pulse

            LineData Pulsedata = getData("Pulse", timep);
            pulseChart.setData(Pulsedata);
            pulseChart.animateX(1500);
        }


    }

    public void onNothingSelected(AdapterView<?> parent){
            timep = "minute";
    }

    private LineData getData(String chooseDB, String timePeriod) {  // DB - Step , Pulse  timePeriod - minute, hour, day
        String choice = chooseDB;
        ArrayList<Data> results = new ArrayList<>();
        ArrayList<Data> temp = new ArrayList<>();
        if(choice == "Step"){
            temp = db.getStepCount(timePeriod);
            int prev = 0;
            int current, value;
            for(int i =0; i < temp.size(); i++){
                current = temp.get(i).getData();
                Log.d("Current Value", "" + current);
                value = current - prev;
                results.add(new Data(value));
                prev = current;
            }
        }
        else if(choice == "Pulse"){
            results = db.getPulse(timePeriod);
        }

//
//        for(int i = 0; i < results.size(); i ++) {
//            Log.d("Reuslts", "" + results.get(i).getData() + " " + results.get(i).getTime());
//
//        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
            xVals.add("" + results.get(i).getTime());
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < results.size(); i++) {
//            float val = (float) (Math.random() * range) + 3;
            int val = results.get(i).getData();
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
