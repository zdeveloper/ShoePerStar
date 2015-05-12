package com.uta.shoeperstar.vibe.Utilities.VibeBluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.uta.shoeperstar.vibe.Utilities.VibeBluetoothService;

/** This is a Singleton class that represents the shoes
 * Created by Zedd on 5/4/2015.
 */
public class VibeShoes implements VibeShoeInterface{

    public static final int RIGHT_SHOE = 0;
    public static final int LEFT_SHOE = 1;

    private static final String TAG = "DEBUG";

    private static VibeShoes instance = null;
    private static VibeBluetoothService vibeBluetoothService = null;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            VibeBluetoothService.BluetoothServiceBinder binder = (VibeBluetoothService.BluetoothServiceBinder) service;
            vibeBluetoothService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    protected VibeShoes(Activity activity){
        //enable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.e(TAG,"Device does not support Bluetooth");
            instance = null; //to allow the user to change their mind
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 0);
        }
        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(activity, "I really need Bluetooth to be ON to connect to the shoes!!", Toast.LENGTH_LONG).show();
            instance = null;    //to allow the user to change their mind
            return;
        }

        Log.d(TAG, "I am starting the bluetooth service");
        //if all is good, start the service
        Intent intent = new Intent(activity, VibeBluetoothService.class);
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //activity.startService(intent);
    }

    public static VibeShoes getInstance(Activity activity){
        if(instance == null){
            instance = new VibeShoes(activity);
        }
        return instance;
    }

    @Override
    public void sendVibrationCommand(int shoe, int count, int intensity, int duration) {
        if(vibeBluetoothService != null){
            vibeBluetoothService.sendVibrationCommand(shoe, count, intensity, duration);
        }
    }

    @Override
    public void sendSleepCommand(int shoe) {
        if(vibeBluetoothService != null){
            vibeBluetoothService.sendSleepCommand(shoe);
        }
    }

    @Override
    public void sendWakeUpCommand(int shoe) {
        if(vibeBluetoothService != null){
            vibeBluetoothService.sendWakeUpCommand(shoe);
        }
    }

    @Override
    public int getbatteryStatus(int shoe) {
        if(vibeBluetoothService != null){
            return vibeBluetoothService.getbatteryStatus(shoe);
        }
        return -1;  //ERROR
    }

    @Override
    public int getSteps(int shoe) {
        if(vibeBluetoothService != null){
            return vibeBluetoothService.getSteps(shoe);
        }
        return -1;  //ERROR
    }

    @Override
    public int getEstimatedBPM(int shoe) {
        if(vibeBluetoothService != null){
            return vibeBluetoothService.getEstimatedBPM(shoe);
        }
        return -1;  //ERROR
    }

    @Override
    public int getActualBPM(int shoe) {
        if(vibeBluetoothService != null){
            return vibeBluetoothService.getActualBPM(shoe);
        }
        return -1;  //ERROR
    }

    @Override
    public void setRightShoeListener(final IBinder binder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(vibeBluetoothService == null);    //block until the service is bound

                vibeBluetoothService.setRightShoeListener(binder);
            }
        }).start();
    }
    /*** This sets up the handler for the left vibe shoe ***/
    public void setRightShoeListener(final VibeShoeHandler handler) {
        final Messenger ms = new Messenger(handler); //making a messenger with a new instance of handler

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(vibeBluetoothService == null);    //block until the service is bound

                vibeBluetoothService.setRightShoeListener(ms.getBinder());
            }
        }).start();
    }

    @Override
    public void setLeftShoeListener(final IBinder binder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(vibeBluetoothService == null);    //block until the service is bound

                vibeBluetoothService.setLeftShoeListener(binder);
            }
        }).start();
    }

    /*** This sets up the handler for the left vibe shoe ***/
    public void setLeftShoeListener(final VibeShoeHandler handler) {
        final Messenger ms = new Messenger(handler); //making a messenger with a new instance of handler
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(vibeBluetoothService == null);    //block until the service is bound

                vibeBluetoothService.setLeftShoeListener(ms.getBinder());
            }
        }).start();
    }
}
