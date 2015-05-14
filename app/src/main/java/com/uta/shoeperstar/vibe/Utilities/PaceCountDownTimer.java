package com.uta.shoeperstar.vibe.Utilities;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.uta.shoeperstar.vibe.Utilities.VibeBluetooth.VibeShoes;

import java.util.concurrent.TimeUnit;

/**
 * Created by tommy on 25/04/15.
 */
public class PaceCountDownTimer extends CountDownTimer {

    private static final String FORMAT  = "%02d:%02d:%02d";
    TextView tv;
    int timeRemain;
    int index = 0;
    int initalStepCount;
    double overallPace;
    VibeShoes stepCounter;


    public PaceCountDownTimer(Activity a, long startTime, long interval, TextView timerTextView, double pace){
        super(startTime,interval);
        tv = timerTextView;
        stepCounter = VibeShoes.getInstance(a);
        initalStepCount = stepCounter.getSteps(VibeShoes.LEFT_SHOE);
        Log.d("Inital Steps" ,": "+ initalStepCount);
        overallPace = pace;
    }

    @Override
    public void onFinish(){
        tv.setText("done");
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tv.setText("" + String.format(FORMAT, TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
        index++;
        timeRemain = (int)(millisUntilFinished);
        int currentSteps = stepCounter.getSteps(VibeShoes.LEFT_SHOE);
        int stepInterval = currentSteps - initalStepCount;
        double dist = 2.5 * stepInterval;

        checkPace(overallPace, dist, timeRemain);


    }

    private void checkPace(double pace, double distance, long time){
        double currentPace = distance/time;
        Log.d("current Pace", "Current Pace: " + currentPace);
        Log.d("OPace", "Original Pace: " + pace);
        if (currentPace > pace) {
            Log.d("Behind Pace", "Behind Pace");
            stepCounter.sendVibrationCommand(VibeShoes.LEFT_SHOE, 3, 100, 10);
            stepCounter.sendVibrationCommand(VibeShoes.RIGHT_SHOE, 3, 100, 10);
        }
        else{
            Log.d("On Pace", "On Pace");

        }
    }



}


