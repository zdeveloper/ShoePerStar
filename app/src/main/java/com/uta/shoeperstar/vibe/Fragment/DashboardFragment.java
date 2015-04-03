package com.uta.shoeperstar.vibe.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;
import com.uta.shoeperstar.vibe.R;

import java.util.Timer;
import java.util.TimerTask;


public class DashboardFragment extends Fragment {

    private TextView stepCount;
    private TextView distance;
    private RippleBackground rippleBackgroundLeft, rippleBackgroundRight;
    private ImageView leftShoe, rightShoe;


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

        rippleBackgroundLeft=(RippleBackground)view.findViewById(R.id.shoe_left_ripple);
        rippleBackgroundRight=(RippleBackground)view.findViewById(R.id.shoe_right_ripple);

        leftShoe = (ImageView) view.findViewById(R.id.shoe_left);
        rightShoe = (ImageView) view.findViewById(R.id.shoe_right);

        stepCount= (TextView) view.findViewById(R.id.stepCount);
        distance = (TextView) view.findViewById(R.id.distance);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // This is called after on onCreateView


        //adding onclick listeners
        leftShoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackgroundLeft.startRippleAnimation();
                //schedule to kill animation after a second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackgroundLeft.stopRippleAnimation();
                        leftShoe.setImageResource(R.drawable.ic_shoe);
                    }
                }, 2000);
            }
        });

        rightShoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBackgroundRight.startRippleAnimation();
                //schedule to kill animation after a second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackgroundRight.stopRippleAnimation();
                        rightShoe.setImageResource(R.drawable.ic_shoe);
                    }
                }, 2000);
            }
        });

        //send battery level for left shoe

        //send battery level for right shoe


        //send step count to UI
        //stepCount.setText("n/a");

        //send distance to UI
        //distance.setText("n/a");

    }
}
