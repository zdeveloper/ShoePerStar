package com.uta.shoeperstar.vibe.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;


import com.uta.shoeperstar.vibe.BluetoothShoeServiceRight;
import com.uta.shoeperstar.vibe.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zedd on 3/20/2015.
 */
@Deprecated
public class BluetoothUtilities {

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Activity activity;  //used to keep track of parent activity


    private ArrayList<BluetoothDevice> bleDevicesList = new ArrayList<>();
    private boolean mScanning = false;

    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;

    public BluetoothUtilities(Activity activity) {

        //init some parameter
        this.activity = activity;

        //initialize bluetooth
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //this enables the Bluetooth
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Use this check to determine whether BLE is supported on the device.
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE IS NOT SUPPORTED, SORRY", Toast.LENGTH_SHORT).show();
        } else {
            scanLeDevice();
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            bleDevicesList.add(device);
            Toast.makeText(activity, "Found Device "+ device.getName(), Toast.LENGTH_SHORT).show();
        }

    };

    private void scanLeDevice() {

        bleDevicesList.clear();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //schedule handler to stop the scan
        //mHandler.postDelayed(mStopRunnable, 2500);

        //call stop scanning after 2.5 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mStopRunnable.run();
            }
        }, SCAN_PERIOD);
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };


    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if(bleDevicesList.size() > 0) displaySearchResults();
    }

    void displaySearchResults() {
        if (bleDevicesList.size() > 0) {
            DialogFragment listFragment = new DisplayDeviceListDialog();
            listFragment.show(activity.getFragmentManager(), "BLE List");
        }
    }

    String[] getArrayOfBleDevices() {
        String[] devices = new String[bleDevicesList.size()];
        for (int i = 0; i < bleDevicesList.size(); i++) {
            devices[i] = bleDevicesList.get(i).getName();
        }
        return devices;
    }


    private BluetoothShoeServiceRight rightShoeService;
    private Intent btRightShoeServiceIntent;

    /**
     * This handles getting an instance of the right arm service
     */
    private ServiceConnection rightServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothShoeServiceRight.BluetoothServiceBinder binder = (BluetoothShoeServiceRight.BluetoothServiceBinder) service;
            rightShoeService  = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    void startRightShoeService(String address){
        if(btRightShoeServiceIntent == null){
            btRightShoeServiceIntent = new Intent(activity, BluetoothShoeServiceRight.class);
        }
        btRightShoeServiceIntent.putExtra(BluetoothShoeServiceRight.PARAM_DEVICE_ADDRESS, address);
        activity.bindService(btRightShoeServiceIntent, rightServiceConnection, Context.BIND_NOT_FOREGROUND );
        activity.startService(btRightShoeServiceIntent);
    }

    @SuppressLint("ValidFragment")
    class DisplayDeviceListDialog extends DialogFragment {

        public DisplayDeviceListDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_ble_device_list)
                        .setItems(getArrayOfBleDevices(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item
                                BluetoothDevice device = bleDevicesList.get(which);
                                //we have the device
                                startRightShoeService(device.getAddress());
                            }
                        });

            return builder.create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            this.onDestroy();
        }
    }
}
