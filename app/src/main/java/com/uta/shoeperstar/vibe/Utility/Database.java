package com.uta.shoeperstar.vibe.Utility;

import android.app.Activity;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by saads_000 on 3/20/2015.
 */
public class Database extends Activity {

    public static SQLiteDatabase vibeDB = null;
    dataSetup pulseData = new dataSetup();
    dataSetup stepData = new dataSetup();

    ArrayList<dataSetup> stepRecord = new ArrayList<>();
    ArrayList<dataSetup> pulseRecord = new ArrayList<>();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void storePulseDatabase(int pulse, int date, int time)
    {

        String tableName = "pulseData";
        try {
            vibeDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + tableName
                    + " (pulse INTEGER, pulseDate INTEGER, pulseTime INTEGER);");

            vibeDB.execSQL("INSERT INTO "
                    + tableName
                    + " (pulse, pulseDate)"
                    + " VALUES (" + pulse + "," + date + "," + time + ")");
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (vibeDB != null)
                vibeDB.close();
        }


        pulseData.data = pulse;
        pulseData.startT = time;
        pulseData.startD = date;

        pulseRecord.add(pulseData);
    }


    public void storeStepCountDatabase(int stepCount, int date, int time)
    {

        String tableName = "stepData";
        try {
            vibeDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + tableName
                    + " (steps INTEGER, stepDate INTEGER, stepTime INTEGER);");

            vibeDB.execSQL("INSERT INTO "
                    + tableName
                    + " (steps, stepDate)"
                    + " VALUES (" + stepCount + "," + date + "," + time + ")");
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (vibeDB != null)
                vibeDB.close();
        }

        stepData.data = stepCount;
        stepData.startT = time;
        stepData.startD = date;

        stepRecord.add(stepData);
    }



    public ArrayList<dataSetup> retrieveSteps()
    {

        return stepRecord ;
    }



    public ArrayList<dataSetup> retrievePulse()
    {

        return pulseRecord ;
    }

}
