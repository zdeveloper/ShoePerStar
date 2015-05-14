package com.uta.shoeperstar.vibe.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.widget.PaperButton;
import com.skyfishjy.library.RippleBackground;
import com.uta.shoeperstar.vibe.R;
import com.uta.shoeperstar.vibe.Utilities.VibeBluetooth.VibeShoeHandler;
import com.uta.shoeperstar.vibe.Utilities.VibeBluetooth.VibeShoes;


public class DashboardFragment extends Fragment {

    private static final String TAG = "DEBUG";

    private TextView stepCount;
    private TextView distance;
    private TextView rightShoeText, leftShoeText;
    private RippleBackground rippleBackgroundLeft, rippleBackgroundRight;
    private ImageView leftShoe, rightShoe;


    private VibeShoes vibeShoes;

    private PaperButton vibrateBtn, pulseBtn;
    private boolean pulseStatus = true;

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

        vibrateBtn = (PaperButton) view.findViewById(R.id.sendVibrationButton);
        pulseBtn = (PaperButton) view.findViewById(R.id.togglePulseButton);


        rippleBackgroundLeft=(RippleBackground)view.findViewById(R.id.shoe_left_ripple);
        rippleBackgroundRight=(RippleBackground)view.findViewById(R.id.shoe_right_ripple);

        leftShoeText = (TextView) view.findViewById(R.id.batteryLevelLeft);
        rightShoeText = (TextView) view.findViewById(R.id.batteryLevelRight);

        leftShoe = (ImageView) view.findViewById(R.id.shoe_left);
        rightShoe = (ImageView) view.findViewById(R.id.shoe_right);

        stepCount= (TextView) view.findViewById(R.id.stepCount);
        distance = (TextView) view.findViewById(R.id.distance);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView

        //call on Bluetooth Utilities
        vibeShoes = VibeShoes.getInstance(getActivity());
        //note that data will take some time to get to the shoe

        vibeShoes.setRightShoeListener(new RightVibeHandler()); //registering handler
        vibeShoes.setLeftShoeListener(new LeftVibeHandler()); //registering handler

        try {
            if (!vibeShoes.isShoeConnected(VibeShoes.RIGHT_SHOE)) {
                rippleBackgroundRight.startRippleAnimation();
            } else {
                rippleBackgroundRight.stopRippleAnimation();
                rightShoe.setImageResource(R.drawable.ic_shoe);
            }
        } catch(Exception e){
            rippleBackgroundRight.startRippleAnimation();
        }


        try {
            if (!vibeShoes.isShoeConnected(VibeShoes.LEFT_SHOE)) {
                rippleBackgroundLeft.startRippleAnimation();
            } else {
                rippleBackgroundLeft.stopRippleAnimation();
                leftShoe.setImageResource(R.drawable.ic_shoe);
            }
        } catch(Exception e){
            rippleBackgroundLeft.startRippleAnimation();
        }
//        //adding onclick listeners
//        leftShoe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //schedule to kill animation after a second
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, 2000);
//            }
//        });
//
//        rightShoe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //schedule to kill animation after a second
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, 2000);
//            }
//        });


        vibrateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibeShoes.sendVibrationCommand(VibeShoes.RIGHT_SHOE, 2, 100, 5);
                vibeShoes.sendVibrationCommand(VibeShoes.LEFT_SHOE, 2, 100, 5);
            }
        });

        pulseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseStatus=!pulseStatus;
                vibeShoes.enablePulse(VibeShoes.LEFT_SHOE, pulseStatus);
            }
        });
        //send battery level for left shoe

        //send battery level for right shoe


        //send step count to UI
        //stepCount.setText("n/a");

        //send distance to UI
        //distance.setText("n/a");

    }





    /**
     * This is a call back class
     */
    class RightVibeHandler extends VibeShoeHandler {

        @Override
        public void onStepReceived(int steps) {
            Log.d(TAG, "Right Steps: " + steps);
            stepCount.setText(steps + "");
            rightShoeText.setText(getRightText(null, steps));
            stepCount.setText(getAverageSteps());
            distance.setText(getDistance());
        }

        @Override
        public void onBatteryLevelReceived(int batteryLevel) {
            Log.d(TAG, "Right Battery: " + batteryLevel);
            rightShoeText.setText(getRightText(batteryLevel, null));
        }

        @Override
        public void onPulseEstimatedReceived(int pulses) {
            Log.d(TAG, "Right Estimated Pulses: " + pulses);
        }

        @Override
        public void onPulseActualReceived(int pulses) {
            Log.d(TAG, "Right Actual Pulses: " + pulses);
        }

        @Override
        public void onStringReceived(String message) {
            Log.d(TAG, "Right Raw Message: " + message);
        }

        @Override
        public void onShoeConnected() {
            rippleBackgroundRight.stopRippleAnimation();
            rightShoe.setImageResource(R.drawable.ic_shoe);
        }

        @Override
        public void onShoeDisconnected() {
            rippleBackgroundRight.startRippleAnimation();
            rightShoe.setImageResource(R.drawable.ic_shoe_light);
        }
    }



    class LeftVibeHandler extends VibeShoeHandler {

        @Override
        public void onStepReceived(int steps) {
            Log.d(TAG, "Left Steps: " + steps);
            stepCount.setText(steps + "");
            leftShoeText.setText(getLeftText(null, steps));
            stepCount.setText(getAverageSteps());
            distance.setText(getDistance());
        }

        @Override
        public void onBatteryLevelReceived(int batteryLevel) {
            Log.d(TAG, "Left Battery: " + batteryLevel);
            leftShoeText.setText(getLeftText(batteryLevel, null));
        }

        @Override
        public void onPulseEstimatedReceived(int pulses) {
            Log.d(TAG, "Left Estimated Pulses: " + pulses);
        }

        @Override
        public void onPulseActualReceived(int pulses) {
            Log.d(TAG, "Left Actual Pulses: " + pulses);
        }

        @Override
        public void onStringReceived(String message) {
            Log.d(TAG, "Left Raw Message: " + message);
        }

        @Override
        public void onShoeConnected() {
            rippleBackgroundLeft.stopRippleAnimation();
            leftShoe.setImageResource(R.drawable.ic_shoe);
        }

        @Override
        public void onShoeDisconnected() {
            rippleBackgroundLeft.startRippleAnimation();
            leftShoe.setImageResource(R.drawable.ic_shoe_light);
        }
    }


    int leftBattery=0, rightBattery=0;
    int leftSteps = 0, rightSteps=0;
    String getLeftText(Integer battery, Integer steps){

        if(battery != null) leftBattery = battery;
        if(steps!=null) leftSteps = steps;

        if(leftBattery > 100) leftBattery = 100;
        if(leftBattery < 0) leftBattery = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("LEFT SHOE\n");
        sb.append(leftBattery+"%\n");
        sb.append(leftSteps + " steps");

        return sb.toString();
    }

    String getRightText(Integer battery, Integer steps){

        if(battery != null) rightBattery = battery;
        if(steps!=null) rightSteps = steps;

        if(rightBattery  > 100) rightBattery = 100;
        if(rightBattery < 0) leftBattery=0;

        StringBuilder sb = new StringBuilder();
        sb.append("LEFT SHOE\n");
        sb.append(rightBattery+"%\n");
        sb.append(rightSteps + " steps");

        return sb.toString();
    }


    String getDistance(){
        double avg = (leftSteps+rightSteps)/2;
        avg = avg*0.762;
        return String.format("%.2f", avg) + " Meters";
    }

    String getAverageSteps(){
        int avg = (leftSteps+rightSteps)/2;
        return avg+"";
    }
}
