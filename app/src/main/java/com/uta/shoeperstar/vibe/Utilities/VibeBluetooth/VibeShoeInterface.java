package com.uta.shoeperstar.vibe.Utilities.VibeBluetooth;

import android.os.IBinder;

/**
 * Created by Zedd on 5/4/2015.
 */
public interface VibeShoeInterface {

    /***
     * This sends a command to the specified shoe
     * @param shoe specify a shoe using ex. BluetoothService.RIGHT_SHOE
     * @param count how many times should the shoe vibrate (0 - 255)
     * @param intensity vibration intensity (0 - 255)
     * @param duration vibration duration in deci-seconds (0 - 255)
     */
    void sendVibrationCommand(final int shoe, int count, int intensity, int duration);

    /**
     * This sends out a sleeping command
     * @param shoe specify a shoe using ex. BluetoothService.RIGHT_SHOE
     */
    void sendSleepCommand(final int shoe);

    /**
     * This is used to wake up shoe
     * @param shoe
     */
    void sendWakeUpCommand(final int shoe);

    /**
     * This returns the battery level percentage (0% - 100%)
     * @param shoe specify a shoe using ex. BluetoothService.RIGHT_SHOE
     * @return
     */
    int getbatteryStatus(final int shoe);


    /**
     * This returns the number of steps the user took
     * @param shoe specify a shoe using ex. BluetoothService.RIGHT_SHOE
     * @return
     */
    int getSteps(final int shoe);

    /**
     * This returns the latest estimated BPM acquired from the shoe
     * @param shoe
     * @return
     */
    int getEstimatedBPM(final int shoe);

    /**
     * This returns the latest actual BPM acquired from the shoe
     * @param shoe
     * @return
     */
    int getActualBPM(final int shoe);

    /**
     * This is used for asynchronous callback for when ever we get data from the shoe.
     * This is pushing data as opposed to pulling data with the get interfaces
     * @param binder this is the binder for the Messenger, ex. messenger.getBinder()
     */
    void setRightShoeListener(final IBinder binder);

    /**
     * This is used for asynchronous callback for when ever we get data from the shoe.
     * This is pushing data as opposed to pulling data with the get interfaces
     * @param binder this is the binder for the Messenger, ex. messenger.getBinder()
     */
    void setLeftShoeListener(final IBinder binder);


}
