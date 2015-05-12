package com.uta.shoeperstar.vibe.Utilities;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** Extend this class to get shoe status asynchronously
 * Created by Zedd on 5/4/2015.
 */
public abstract class VibeShoeHandler extends Handler{

    /** Command to the service to display a message */
    static final int MSG_BATTERY = 1;
    static final int MSG_STEPS = 2;
    static final int MSG_PULSE_ACTUAL = 3;
    static final int MSG_PULSE_ESTIMATED = 4;
    static final int MSG_RAW = 5;

   //@Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_BATTERY:
                onBatteryLevelReceived( (Integer) msg.obj );
                break;
            case MSG_STEPS:
                onStepReceived( (Integer) msg.obj );
                break;
            case MSG_PULSE_ACTUAL:
                onPulseActualReceived( (Integer) msg.obj );
                break;
            case MSG_PULSE_ESTIMATED:
                onPulseEstimatedReceived( (Integer) msg.obj );
                break;
            default:
                onStringReceived((String) msg.obj);
                break;
        }
    }

    public abstract void onStepReceived(int steps);
    public abstract void onBatteryLevelReceived(int batteryLevel);
    public abstract void onPulseEstimatedReceived(int pulses);
    public abstract void onPulseActualReceived(int pulses);
    public abstract void onStringReceived(String message);
}
