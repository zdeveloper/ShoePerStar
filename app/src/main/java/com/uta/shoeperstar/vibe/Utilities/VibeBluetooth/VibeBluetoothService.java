package com.uta.shoeperstar.vibe.Utilities.VibeBluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.uta.shoeperstar.vibe.Utilities.Data;
import com.uta.shoeperstar.vibe.Utilities.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/** This is a Bluetooth service to manage the bluetooth device.
 * NOTE: this service assumes bluetooth is already ON
 * Created by Zedd on 5/4/2015.
 */
public class VibeBluetoothService extends Service implements VibeShoeInterface {


    Database db;


    public boolean RIGHT_SHOE_CONNECTED, LEFT_SHOE_CONNECTED;

    public static final int RIGHT_SHOE = 0;
    public static final int LEFT_SHOE = 1;

    /** Messenger for communicating with the activity. */
    private Messenger rightShoeListener = null;
    private Messenger leftShoeListener = null;

    //The following are data containers
    private int leftSteps=0, rightSteps=0;
    private int rightBpmEstimated=0, rightBpmActual=0;
    private int leftBpmEstimated=0, leftBpmActual=0;
    private int leftBatteryLevel=0, rightBatteryLevel=0;


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Binder serviceBinder = new BluetoothServiceBinder();

    private BluetoothAdapter btAdapter = null;

    private VibeConnection rightVibeConnection, leftVibeConnection;

    private BluetoothSocket rightBtSocket = null;
    private BluetoothSocket leftBtSocket = null;

    private OutputStream rightOutStream = null;
    private OutputStream leftOutStream = null;

    private String TAG = "DEBUG";


    private static String rightAddress = "20:13:09:29:14:09";
    private static String leftAddress = "20:15:03:03:07:29";

    public VibeBluetoothService() {}


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try{
            return device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e){
            Log.d(TAG, "I crashed creating the frickin socket.");
        }
        return null;
    }

    private void showToast(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
    }


    void startService(){

        db= new Database(getApplicationContext(), null, null, 1);


        Log.d(TAG, "VibeBluetoothService is alive");



        //assuming BT is enabled by before
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            showToast("ERROR", "BT not enabled");
            stopSelf();
        }

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice rightVibeDevice = btAdapter.getRemoteDevice(rightAddress);

        BluetoothDevice leftVibeDevice = btAdapter.getRemoteDevice(leftAddress);


        // Set up a pointer to the remote node using it's address.
        //BluetoothDevice leftVibeDevice = btAdapter.getRemoteDevice(leftAddress);

//        rightOutStream = makeStreamFromDevice(RIGHT_SHOE, rightVibeDevice);
//        leftOutStream = makeStreamFromDevice(LEFT_SHOE, leftVibeDevice);

        leftVibeConnection = new VibeConnection(LEFT_SHOE, leftVibeDevice);

        rightVibeConnection = new VibeConnection(RIGHT_SHOE, rightVibeDevice);


        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();
    }

    @Override
    public IBinder onBind(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService();
            }
        }).start();
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

                                    //add data to database
                                    Data data = new Data(steps);
                                    db.addStepCount(data);


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


                                        //add data to database
                                        Data data = new Data(pulses);
                                        db.addPulse(data);

                                        //publish the results
                                        if(shoe == LEFT_SHOE) publishMessage(shoe, VibeShoeHandler.MSG_PULSE_ACTUAL, pulses);
                                        //update local values
                                        if (shoe == LEFT_SHOE) leftBpmActual = pulses;
                                        else rightBpmActual = pulses;

                                    } else if (msgArray[1].equals("E")) {  //estimated pulse

                                        int pulses = Integer.parseInt(msgArray[2]);
                                        //publish the results
                                        if(shoe == LEFT_SHOE) publishMessage(shoe, VibeShoeHandler.MSG_PULSE_ESTIMATED, pulses);
                                        //update local values
                                        if (shoe == LEFT_SHOE) leftBpmEstimated = pulses;
                                        else rightBpmEstimated = pulses;
                                    }
                                } else { //here we send in the raw value
                                    publishMessage(shoe, VibeShoeHandler.MSG_RAW, commands[i]);
                                }

                            }
                        }
                        Thread.sleep(300);  // we sleep here to conserve battery
                    } catch (Exception e){
                        Log.e(TAG, "ERROR in receiving data, shoe code:" + shoe + "\nError: " + e.toString());
                    }
                }
            }
        }).start();

    }

    private void sendData(final int shoeCode, String message) {
        OutputStream outStream;
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

            Message msg= Message.obtain(null, VibeShoeHandler.MSG_SHOE_DISCONNECTED);
            try {
                if(shoeCode == LEFT_SHOE) {
                    leftShoeListener.send(msg);
                    leftVibeConnection.reconnectVibe();
                    LEFT_SHOE_CONNECTED = false;
                }
                else {
                    rightShoeListener.send(msg);
                    rightVibeConnection.reconnectVibe();
                    RIGHT_SHOE_CONNECTED = false;
                }
            } catch (RemoteException e1) {
                Log.e(TAG, "Error in sending message to handler: " + e1.toString());
            }
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
            Message msg2= Message.obtain(null, VibeShoeHandler.MSG_SHOE_DISCONNECTED);
            try {
                if(shoeCode == LEFT_SHOE) {
                    leftShoeListener.send(msg2);
                    leftVibeConnection.reconnectVibe();
                    LEFT_SHOE_CONNECTED = false;
                }
                else {
                    rightShoeListener.send(msg2);
                    rightVibeConnection.reconnectVibe();
                    RIGHT_SHOE_CONNECTED = false;
                }
            } catch (RemoteException e1) {
                Log.e(TAG, "Error in sending message to handler: " + e1.toString());
            }
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
                        Thread.sleep(sleepDuration*100 + 200);
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

    @Override
    public void enablePulse(int shoe, boolean val) {
        byte[] command = new byte[2];
        command[0] = (byte)'P';


        if(val == true) command[1] = (byte)'1';
        else {command[1] = (byte)'0';}

        //sending the command
        sendData(shoe,command);
    }

    @Override
    public boolean isShoeConnected(int shoe) {
        if(shoe == LEFT_SHOE) return LEFT_SHOE_CONNECTED;
        else return RIGHT_SHOE_CONNECTED;
    }


    public class BluetoothServiceBinder extends Binder{
        /** This service returns the service **/
        public VibeBluetoothService getService() {
            return VibeBluetoothService.this;
        }

    }


    public class VibeConnection {
        int shoe;
        BluetoothDevice device;

        public VibeConnection(final int shoe, BluetoothDevice device){
            this.shoe = shoe;
            this.device = device;

            if(shoe == LEFT_SHOE){
                leftOutStream = makeStreamFromDevice(shoe, device);
            } else {
                rightOutStream = makeStreamFromDevice(shoe, device);
            }
        }

        public void reconnectVibe(){
            if(shoe == LEFT_SHOE){
                leftOutStream = makeStreamFromDevice(shoe, device);
            } else {
                rightOutStream = makeStreamFromDevice(shoe, device);
            }
        }

        private OutputStream makeStreamFromDevice(final int shoe, BluetoothDevice device ) {
            OutputStream outStream = null;
            BluetoothSocket btSocket = null;

            Log.d(TAG, "Connecting to " + device.getName());

            try {

                // Create a data stream so we can talk to server.
                btSocket = createBluetoothSocket(device);
                if(shoe == LEFT_SHOE) leftBtSocket = btSocket;
                else rightBtSocket = btSocket;

                Log.d(TAG, "Socket Created");

            } catch (Exception e1) {
                Log.e(TAG, "Fatal Error : socket creation failed: " + e1.getMessage() + ".");
            }

            try {
                btSocket.connect();
                Log.d(TAG, "...Connection ok...");
            } catch (Exception e) {
                Log.d(TAG, "Connection failed: " + e.toString());
                try {
                    btSocket.close();
                } catch (Exception e2) {
                    Log.e(TAG, "Fatal Error : unable to close socket during connection failure" + e2.getMessage() + ".");
                }finally {
                    //try again!
                    return makeStreamFromDevice(shoe, device);
                }
            }

            try {
                outStream = btSocket.getOutputStream();
                if(btSocket.getOutputStream() == null){
                    Log.d(TAG, "Output is null");
                }
            } catch (IOException e) {
                Log.e(TAG, "Fatal Error : Output stream creation failed:" + e.getMessage() + ".");
            }

            Log.d(TAG, "I am starting to monitor input stream");

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


            //single connection to handler
            Message msg= Message.obtain(null, VibeShoeHandler.MSG_SHOE_CONNECTED);
            try {
                if(shoe == LEFT_SHOE) {
                    leftShoeListener.send(msg);
                    LEFT_SHOE_CONNECTED = true;
                }
                else {
                    rightShoeListener.send(msg);
                    RIGHT_SHOE_CONNECTED = true;
                }
            } catch (RemoteException e1) {
                Log.e(TAG, "Error in sending message to handler: " + e1.toString());
            }

            return outStream;
        }
    }
}
