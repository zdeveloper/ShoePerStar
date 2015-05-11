package com.uta.shoeperstar.vibe.Fragment;

import android.app.Fragment;
//import com.devadvance.circularseekbar.CircularSeekBar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.PaceCountDownTimer;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * Created by tommy on 24/04/15.
 */
public class PaceFragment extends Fragment {
    TextView timerTextView, distanceTextView, distanceQuestion, timeQuestion;
    EditText distanceText;

    private static final int ONESEC  = 1000 ;
    int secondVal, minuteVal;
    double distanceToGo;
    double[] distanceData = {1,.9,.8,.7,.7,.7,.6,.5,.2,0};
    long countdownTime, timeRemain;
    float pace, distance;
   // NumberPicker minutes, seconds;
    Button setTimeButton, paceButton;
    Handler getDistanceHandler;



    public PaceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pace, container, false);

//        minutes =(NumberPicker) view.findViewById(R.id.minutePicker);
//        seconds =(NumberPicker) view.findViewById(R.id.secondPicker);
//        distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
//        minutes.setMaxValue(59);
//        minutes.setMinValue(0);
//        minutes.setWrapSelectorWheel(true);
//        seconds.setMaxValue(59);
//        seconds.setMinValue(0);
//        seconds.setWrapSelectorWheel(true);

        //setDistanceButton =  (Button) view.findViewById(R.id.setDistance);
//        setTimeButton = (Button)view.findViewById(R.id.setPace);
//        timerTextView = (TextView) view.findViewById(R.id.timerTextView);
        distanceQuestion = (TextView) view.findViewById(R.id.distanceQuestion);
        distanceText = (EditText) view.findViewById(R.id.distanceEditText);



//
//
        return view;
        //link UI here
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
//        getDistanceHandler = new Handler();
//        // when values change then it updates the Minutes and seconds variable
//        minutes.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                minuteVal = newVal;
//            }
//        });
//
//        seconds.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                secondVal = newVal;
//            }
//        });
//
//
//        // Locks the value and sends it to the countdown timer
//        setTimeButton.setOnClickListener(new View.OnClickListener() {
//           long cdt; //countdown timer
//           @Override
//           public void onClick(View v) {
////               minutes.setVisibility(View.GONE);
////               seconds.setVisibility(View.GONE);
//               String dist = distanceTextView.getText().toString();
//
//               distance = Float.parseFloat(dist);
//               distanceTextView.setVisibility(View.GONE);
//               setTimeButton.setVisibility(View.GONE);
//               countdownTime = setCountdownTime(minuteVal, secondVal);
//               pace= distance/(countdownTime/1000);
//               final PaceCountDownTimer paceTimer = new PaceCountDownTimer(countdownTime, ONESEC, timerTextView);
//               paceTimer.start();
//
//               getDistanceHandler.postDelayed(new Runnable(){
//                   public void run(){
//                       timeRemain = paceTimer.getTimeRemain();
//                     //  Log.d("Time Remain", "Time Remain : "+ timeRemain);
//                       if(timeRemain > 0) {
//                          distanceToGo=paceTimer.getDistance(distanceData);
//                          Log.d("distanceTogo", "Dist to go: " + distanceToGo);
//                          checkPace(pace, distanceToGo,(long)timeRemain);
//                          getDistanceHandler.postDelayed(this, ONESEC);
//                       }
//                   }
//               },ONESEC);
//               Log.d("CountDown Time", "Countdown Time: " + countdownTime);
//               Log.d("Pace", "Pace: " + pace);
//               Log.d("Distance", "Distance: " + distance);
//           }
//       });


//        setDistanceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String dist = distanceTextView.getText().toString();
//                distance = Float.parseFloat(dist);
//                distanceTextView.setVisibility(View.GONE);
//                setDistanceButton.setVisibility(View.GONE);
//                Log.d("Distance", "Distance: " + distance);
//            }
//        });
    }

//    private long setCountdownTime(int min, int sec){
//        //converts minutes to seconds to milliseconds
//        // for input to PaceCountDownTimer
//        long cdtMilli= TimeUnit.MINUTES.toMillis(min) + TimeUnit.SECONDS.toMillis(sec);
//        return cdtMilli;
//    }
//
//    private boolean checkPace(float pace, double distance, long time){
//        double currentPace = distance/time;
//        Log.d("current Pace", "Current Pace: " + currentPace);
//        Log.d("OPace", "Original Pace: " + pace);
//        if (currentPace > pace) {
//            Log.d("Behind Pace", "Behind Pace");
//            return true;
//        }
//        else{
//            Log.d("On Pace", "On Pace");
//            return false;
//        }
//    }

}
