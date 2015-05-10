package com.uta.shoeperstar.vibe.Utilities;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by tommy on 25/04/15.
 */
public class PaceCountDownTimer extends CountDownTimer {

    private static final String FORMAT  = "%02d:%02d:%02d";
    TextView tv;
    int timeRemain;
    int index = 0;

    public PaceCountDownTimer(long startTime, long interval, TextView timerTextView){
        super(startTime,interval);
        tv = timerTextView;
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
        timeRemain = (int)(millisUntilFinished/1000)-1;
    }

    public double getDistance(double[] distanceArray){
        return distanceArray[index];
    }

    public long getTimeRemain(){
        return timeRemain;
    }
}


