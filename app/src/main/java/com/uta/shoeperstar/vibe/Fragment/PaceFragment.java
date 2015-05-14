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
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.PaceCountDownTimer;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * Created by tommy on 24/04/15.
 */
public class PaceFragment extends Fragment {
    TextView distancetv, timetv, distanceQuestion, timeQuestion, timerTextView;
    private static final int ONESEC  = 1000 ;
    private static final float MAXDISTANCEVALUE  = 5 ;
    FrameLayout second, first;


    double distanceToGo;
    double[] distanceData = {1,.9,.8,.7,.7,.7,.6,.5,.2,0};
    long countdownTime, timeRemain;
    int time;
    double pace, distance, paced;
    private SeekArc distanceSeek , timeSeek;
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

        first = (FrameLayout) view.findViewById(R.id.paceLayout1);
        second = (FrameLayout) view.findViewById(R.id.paceLayout2);
        distanceQuestion = (TextView) view.findViewById(R.id.distanceQuestion);
        timeQuestion = (TextView) view.findViewById(R.id.timeQuestion);
        distanceSeek = (SeekArc) view.findViewById(R.id.distanceSeekArc);
        distancetv = (TextView) view.findViewById(R.id.distanceProgress);
        timeSeek = (SeekArc) view.findViewById(R.id.timeSeekArc);
        timetv = (TextView) view.findViewById(R.id.timeProgress);
        timerTextView =(TextView)view.findViewById(R.id.timerText);

//
//
        return view;
        //link UI here
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        distanceSeek.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                distancetv.setClickable(false);
                distance = ((float) i * MAXDISTANCEVALUE) / 100;
                String fuck = String.format("%.2f", distance);
                distancetv.setText(fuck);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                distancetv.setClickable(false);
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                distancetv.setText("Next");
                distancetv.setClickable(true);
                distancetv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("button Clicked", "button clicked homie");
                        first.setVisibility(View.GONE);
                        second.setVisibility(View.VISIBLE);
                        timeQuestion.setText("Select a time");
                        timeQuestion.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        timeSeek.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                timetv.setClickable(false);
                time = i;
                timetv.setText("" + time);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                timetv.setClickable(false);
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                timetv.setText("Set Pace");
                timetv.setClickable(true);
                timetv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("set button Clicked", " set button clicked homie");
                        paced = convertMilestofeet(distance);
                        countdownTime = convertMintoMilli(time);
                        pace = paced/countdownTime;
                        timeSeek.setVisibility(View.GONE);
                        Log.d("Pace", "pace = " +pace);
                        final PaceCountDownTimer paceTimer = new PaceCountDownTimer(getActivity(),countdownTime, ONESEC, timerTextView, pace);
                        paceTimer.start();
                    }
                });
            }
        });
    }

    private double convertMilestofeet(double miles){
        double feet;
        feet = miles * 5280;
        return feet;
    }

    private long convertMintoMilli(int min){
        //converts minutes to seconds to milliseconds
        // for input to PaceCountDownTimer
        long cdtMilli= TimeUnit.MINUTES.toMillis(min);
        return cdtMilli;
    }

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
//    }

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
