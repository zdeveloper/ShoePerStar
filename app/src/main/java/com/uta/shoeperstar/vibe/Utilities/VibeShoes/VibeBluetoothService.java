package com.uta.shoeperstar.vibe.Utilities.VibeShoes;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/** This is a Bluetooth service to manage the bluetooth device.
 * NOTE: this service assumes bluetooth is already ON
 * Created by Zedd on 5/4/2015.
 */
public class VibeBluetoothService extends Service implements VibeShoeInterface{

    public static final int RIGHT_SHOE = 0;
    public static final int LEFT_SHOE = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String rightAddress = "20:13:09:29:14:09";
    private static String leftAddress = "20:13:09:29:14:09";
    private final Binder serviceBinder = new BluetoothServiceBinder();
    /**
     * Messenger for communicating with the activity.
     */
    private Messenger rightShoeListener = null;
    private Messenger leftShoeListener = null;
    //The following are data containers
    private int leftSteps = 0, rightSteps = 0;
    private int rightBpmEstimated = 0, rightBpmActual = 0;
    private int leftBpmEstimated = 0, leftBpmActual = 0;

    //private BluetoothSocket rightBtSocket = null;
    //private BluetoothSocket leftBtSocket = null;
    private int leftBatteryLevel = 0, rightBatteryLevel = 0;
    private BluetoothAdapter btAdapter = null;
    private OutputStream rightOutStream = null;
    private OutputStream leftOutStream = null;
    private String TAG = "DEBUG";

    public VibeBluetoothService() {}


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void showToast(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
    }


    void startService(){
        Log.d(TAG, "VibeBluetoothService is alive");


        //assuming BT is enabled by before
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            showToast("ERROR", "BT not enabled");
            stopSelf();
        }

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice rightVibeDevice = btAdapter.getRemoteDevice(rightAddress);

        // Set up a pointer to the remote node using it's address.
        //BluetoothDevice leftVibeDevice = btAdapter.getRemoteDevice(leftAddress);

        rightOutStream = makeStreamFromDevice(RIGHT_SHOE, rightVibeDevice);

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        //making the service sticky
       // return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        startService();
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "VibeBluetoothService is being killed");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void publishMessage(int shoe, int what, Object message){

        //error checking
        if(leftShoeListener == null && shoe == LEFT_SHOE) return;
        if(rightShoeListener == null && shoe == RIGHT_SHOE) return;

        Message msg = Message.obtain(null, what, message);
        try {
            if(shoe == LEFT_SHOE) leftShoeListener.send(msg);
            else rightShoeListener.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in sending message to handler: " + e.toString());
        }
    }

    private void monitorInputSteam(final int shoe, final InputStream inputStream){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] inputBytes = new byte[bytesAvailable];
                            inputStream.read(inputBytes);
                            String msgReceived = new String(inputBytes);

                            String[] commands = msgReceived.split("\\|"); //using | to separate commands
                            //Log.d(TAG, "Received: " + msgReceived);
                            for (int i=0; i<commands.length; i++){


                                //Log.d(TAG, "Received: " +commands[i]);

                                String[] msgArray = commands[i].split("_");

                                //error checking
                                if(msgArray.length < 2) continue;

                                if(msgArray[0].equals("B")) { // Battery Level
                                    int batteryReading = Integer.parseInt(msgArray[1]);

                                    //publish the results
                                    publishMessage(shoe, VibeShoeHandler.MSG_BATTERY, batteryReading);
                                    //update local values
                                    if(shoe == LEFT_SHOE)leftBatteryLevel = batteryReading;
                                    else rightBatteryLevel = batteryReading;

                                } else if(msgArray[0].equals("S")){ //Step Count

                                    int steps = Integer.parseInt(msgArray[1]);

                                    //publish the results
                                    publishMessage(shoe, VibeShoeHandler.MSG_STEPS, steps);
                                    //update local values
                                    if(shoe == LEFT_SHOE)leftSteps = steps;
                                    else rightSteps = steps;


                                } else if(msgArray[0].equals("P")) { //Pulse Count

                                    //error checking
                                    if(msgArray.length != 3) continue;

                                    if (msgArray[1].equals("A")) {     //actual pulse

                                        int pulses = Integer.parseInt(msgArray[2]);
                                        //publish the results
                                        publishMessage(shoe, VibeShoeHandler.MSG_PULSE_ACTUAL, pulses);
                                        //update local values
                                        if (shoe == LEFT_SHOE) leftBpmActual = pulses;
                                        else rightBpmActual = pulses;

                                    } else if (msgArray[1].equals("E")) {  //estimated pulse

                                        int pulses = Integer.parseInt(msgArray[2]);
                                        //publish the results
                                        publishMessage(shoe, VibeShoeHandler.MSG_PULSE_ESTIMATED, pulses);
                                        //update local values
                                        if (shoe == LEFT_SHOE) leftBpmEstimated = pulses;
                                        else rightBpmEstimated = pulses;
                                    }
                                } else { //here we send in the raw value
                                    publishMessage(shoe, VibeShoeHandler.MSG_RAW, commands[i]);
                                }

                            }
                        }
                        Thread.sleep(200);  // we sleep here to conserve battery
                    } catch (Exception e) {
//                        Log.e(TAG, "ERROR in receiving data, shoe code:" + shoe + "\nError: " + e.toString());
                    }
                }
            }
        }).start();

    }

    private OutputStream makeStreamFromDevice(final int shoe, BluetoothDevice device ) {
        OutputStream outStream = null;
        BluetoothSocket btSocket = null;

        Log.d(TAG, "Connecting to " + device.getName());

        try {
            btSocket = createBluetoothSocket(device);
        } catch (Exception e1) {
            Log.e(TAG, "Fatal Error : socket creation failed: " + e1.getMessage() + ".");
        }

        try {
            Log.d(TAG, "...Connection ok...");
            btSocket.connect();
        } catch (Exception e) {
            try {
                btSocket.close();
            } catch (Exception e2) {
                Log.e(TAG, "Fatal Error : unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "Socket Created");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Fatal Error : Output stream creation failed:" + e.getMessage() + ".");
        }


        final BluetoothSocket btSocket2 = btSocket;
        //setting up the input stream monitor thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    monitorInputSteam(shoe, btSocket2.getInputStream());
                } catch (Exception e){
                    Log.e(TAG, "Fatal Error : Input stream creation failed:" + e.getMessage() + ".");
                }
            }
        }).start();
        return outStream;
    }

    private void sendData(final int shoeCode, String message) {
        OutputStream outStream = null;
        if(shoeCode == RIGHT_SHOE){
            outStream = rightOutStream;
        } else {
            outStream = leftOutStream;
        }

        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message);

        try {
            outStream.write(msgBuffer);

        } catch (IOException e) {
            String errorMsg = "Error - Exception occurred during write:" + e.getMessage();
            Log.e(TAG, errorMsg);
        }
    }

    private void sendData(final int shoeCode, byte[] msg) {
        OutputStream outStream = null;
        if (shoeCode == RIGHT_SHOE) {
            outStream = rightOutStream;
        } else {
            outStream = leftOutStream;
        }

        Log.d(TAG, "...Send data: " + new String(msg));

        try {
            outStream.write(msg);
        } catch (IOException e) {
            String errorMsg = "Error - Exception occurred during write:" + e.getMessage();
            Log.e(TAG, errorMsg);
        }
    }

    //The following are interface classes

    @Override
    public void sendVibrationCommand(final int shoe, int count, int intensity, int duration) {

        //error checking on the input
        if (count < 0) count =1;

        //error checking on the input
        if(intensity < 0) intensity = intensity*-1;
        if(intensity > 255) intensity = 255;

        //error checking on the input
        if(duration < 0) duration = duration*-1;
        if(duration > 255) duration = 255;


        //these need to be final to run inside the thread
        final byte[] msg = new byte[3]; //used to contain the msg
        final int sleepDuration = duration;
        final int vibrationCount = count;

        //constructing the message
        msg[0] = (byte)'V';
        msg[1] = (byte)intensity;
        msg[2] = (byte)duration;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int numberOfVibrations = vibrationCount;
                    //send the command as many times needed
                    while (numberOfVibrations > 0){
                        sendData(shoe, msg);
                        Thread.sleep(sleepDuration*1000);
                        numberOfVibrations--;
                    }
                } catch(Exception e){
                    Log.e(TAG, "Error sending the following vibration command " + new String(msg) + "\n" + e.toString());
                }
            }
        }).start();

    }

    @Override
    public void sendSleepCommand(final int shoe) {
        sendData(shoe, "S1");
    }

    @Override
    public void sendWakeUpCommand(final int shoe) {
        sendData(shoe, "S0");
    }

    @Override
    public int getbatteryStatus(final int shoe) {
        if(shoe == LEFT_SHOE) return leftBatteryLevel;
        return rightBatteryLevel;
    }

    @Override
    public int getSteps(final int shoe) {
        if(shoe == LEFT_SHOE) return leftSteps;
        return rightSteps;
    }

    @Override
    public int getEstimatedBPM(final int shoe) {
        if(shoe == LEFT_SHOE) return leftBpmEstimated;
        return rightBpmEstimated;
    }

    @Override
    public int getActualBPM(final int shoe) {
        if(shoe == LEFT_SHOE) return leftBpmActual;
        return rightBpmActual;
    }

    @Override
    public void setRightShoeListener(IBinder binder) {
        rightShoeListener = new Messenger(binder);
    }

    @Override
    public void setLeftShoeListener(IBinder binder) {
        leftShoeListener = new Messenger(binder);
    }

    public class BluetoothServiceBinder extends Binder{
        /** This service returns the service **/
        VibeBluetoothService getService() {
            return VibeBluetoothService.this;
        }

    }



}
