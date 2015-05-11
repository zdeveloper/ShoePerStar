package com.uta.shoeperstar.vibe;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothShoeServiceRight extends Service {


    public static final String TAG = "DEBUG";

    public static String PARAM_DEVICE_ADDRESS;
    public static String PARAM_DEVICE_NAME;


    // UUIDs for UAT service and associated characteristics.
    public static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // UUID for the BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // BTLE state
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;
    //this is only populated to the connected device
    private BluetoothDevice bluetoothDevice = null;
    // Main BTLE device callback where much of the logic occurs.
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // Called whenever the device connection state changes, i.e. from disconnected to connected.
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                writeLine("Connected!");
                // Discover services.
                if (!gatt.discoverServices()) {
                    writeLine("Failed to start discovering services!");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                writeLine("Disconnected!");
            } else {
                writeLine("Connection state changed.  New state: " + newState);
            }
        }

        // Called when services have been discovered on the remote device.
        // It seems to be necessary to wait for this discovery to occur before
        // manipulating any services or characteristics.
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeLine("Service discovery completed!");
            } else {
                writeLine("Service discovery failed with status: " + status);
            }
            // Save reference to each characteristic.
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(rx, true)) {
                writeLine("Couldn't set notifications for RX characteristic!");
            }
            // Next update the RX characteristic's client descriptor to enable notifications.
            if (rx.getDescriptor(CLIENT_UUID) != null) {
                BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (!gatt.writeDescriptor(desc)) {
                    writeLine("Couldn't write RX client descriptor value!");
                }
            } else {
                writeLine("Couldn't get RX client descriptor!");
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onBluetoothReceive(characteristic.getStringValue(0));
        }
    };

    public BluetoothShoeServiceRight() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            Bundle bundle = intent.getExtras();
            String address = bundle.getString(PARAM_DEVICE_ADDRESS);

            //acticityReceiver = bundle.getString(PARAM_IN_BT_ACTIVITY_RECEIVER);
            //serviceReceiver = bundle.getString(PARAM_IN_BT_SERVICE_RECEIVER);


            //Initialize Bluetooth
            BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            //get the device
            bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);

            //if a device was already configured, connect to it. otherwise search for devices
            if (bluetoothDevice != null) {
                connectBleDevice(bluetoothDevice);
            }
        }else {
            Log.d(TAG, "ERROR! in service Right");
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void sendStringOverBluetooth(String message){
        if (tx == null || message == null || message.isEmpty()) {
            // Do nothing if there is no device or message to send.
            return;
        }
        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
        tx.setValue(message.getBytes(Charset.forName("UTF-8")));
        if (mBluetoothGatt.writeCharacteristic(tx)) {
            writeLine("Sent: " + message);
        }
        else {
            writeLine("Couldn't write TX characteristic!");
        }
    }

    private void onBluetoothReceive(String message) {
        writeLine("Received: " + message);


        sendStringOverBluetooth("You Suck!!");
    }

    private void connectBleDevice(BluetoothDevice device) {
        writeLine("Connecting to device " + device.getName() + " with address " + device.getAddress());
        bluetoothDevice = device;
        mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);
        //Display progress UI
    }

    private void writeLine(String text) {
        Log.d(TAG, text);
    }

    @Override
    public void onDestroy() {
        if (mBluetoothGatt != null) {
            // For better reliability be careful to disconnect and close the connection.
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            tx = null;
            rx = null;
        }
        super.onDestroy();
    }

    public class BluetoothServiceBinder extends Binder {
        public BluetoothShoeServiceRight getService() {
            return BluetoothShoeServiceRight.this;
        }
    }




}
