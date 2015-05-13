package com.uta.shoeperstar.vibe.Utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.uta.shoeperstar.vibe.R;

import java.util.ArrayList;

/**
 * Created by saads_000 on 3/20/2015.
 */
//

public class Database extends SQLiteOpenHelper {

    public static final String SC_COLUMN_STEPCOUNT = "steps";
    public static final String SC_COLUMN_DATE = "date";
    public static final String SC_COLUMN_TIME = "time";
    public static final String P_COLUMN_PULSE = "pulse";
    public static final String P_COLUMN_DATE = "date";
    public static final String P_COLUMN_TIME = "time";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "vibeDB.db";
    private static final String TABLE_STEPCOUNT = "StepCount";
    private static final String TABLE_PULSE = "Pulse";
    private static Database db;



    /**
     * Singleton method, will return the same object each time.
     */





    public Database(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        String DROP_STEPCOUNT_TABLE = "DROP TABLE " + DATABASE_NAME+"."+TABLE_STEPCOUNT;
//        String DROP_PULSE_TABLE = "DROP TABLE " + DATABASE_NAME+"."+TABLE_PULSE;
//
//        db.execSQL(DROP_STEPCOUNT_TABLE);
//        db.execSQL(DROP_PULSE_TABLE);
        String CREATE_STEPCOUNT_TABLE = "CREATE TABLE " +
                TABLE_STEPCOUNT + "("
                + SC_COLUMN_STEPCOUNT + " INTEGER," + "Timestamp DATETIME DEFAULT (datetime('now', 'localtime')))";
        db.execSQL(CREATE_STEPCOUNT_TABLE);


        String CREATE_PULSE_TABLE = "CREATE TABLE " +
                TABLE_PULSE + "("
                + P_COLUMN_PULSE + " INTEGER," + "Timestamp DATETIME DEFAULT (datetime('now', 'localtime')))";
        db.execSQL(CREATE_PULSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

    }


    public void addStepCount(Data data) {

        ContentValues values = new ContentValues();
        values.put(SC_COLUMN_STEPCOUNT, data.getData());
//        values.put(SC_COLUMN_DATE, data.getDate());
//        values.put(SC_COLUMN_TIME, data.getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_STEPCOUNT, null, values);
        db.close();
//        Log.d(null, "stuff Created");
    }


    public void addPulse(Data data) {

        ContentValues values = new ContentValues();
        values.put(P_COLUMN_PULSE, data.getData());

//        values.put(P_COLUMN_PULSE, data.getData());
//        values.put(P_COLUMN_DATE, data.getDate());
//        values.put(P_COLUMN_TIME, data.getTime());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_PULSE, null, values);

        db.close();
    }


    public ArrayList<Data> getStepCount(String timePeriod) {
        try {
            String query = "";
//        Log.d("timePeriod:", timePeriod);
            switch (timePeriod) {
                case "day":
                    Log.d("in Day:", " Hello!");
                    query = "SELECT  MAX(steps) as Steps, strftime('%m/%d', Timestamp) as time FROM " + TABLE_STEPCOUNT + " GROUP BY strftime('%j', Timestamp)";
                    break;
                case "hour":
                    query = "SELECT  MAX(steps) as Steps, strftime('%H', Timestamp) as hour FROM " + TABLE_STEPCOUNT + " GROUP BY strftime('%j-%H', Timestamp)";
                    break;
                case "minute":
                    query = "SELECT  MAX(steps) as Steps, strftime('%H:%M', Timestamp) as hour FROM " + TABLE_STEPCOUNT + " GROUP BY strftime('%j-%H-%M', Timestamp)";
                    break;
            }

            ArrayList<Data> stepData = new ArrayList<>();


            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            int value = 0;
            do {
                Data data = new Data();
                value = Integer.parseInt(cursor.getString(0));
//            Log.d("value:",""+value);
                data.setData(value);
//            Log.d("Time String", "" + cursor.getString(1));
                data.setTime("" + cursor.getString(1));
                stepData.add(data);

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            return stepData;
        }catch (CursorIndexOutOfBoundsException e){
            ArrayList<Data> pulseRecord = new ArrayList<>();
            return pulseRecord;
        }
    }


    public ArrayList<Data> getPulse(String timePeriod) {
        try {
            String query = "";
            switch (timePeriod) {
                case "day":
                    query = "SELECT  AVG(pulse) as pulseAvg, strftime('%m-%d', Timestamp) as hour FROM " + TABLE_PULSE + " GROUP BY strftime('%j', Timestamp)";
                    break;
                case "hour":
                    query = "SELECT  AVG(pulse) as pulseAvg, strftime('%H', Timestamp) as hour FROM " + TABLE_PULSE + " GROUP BY strftime('%j-%H', Timestamp)";
                    break;
                case "minute":
                    query = "SELECT  AVG(pulse) as pulseAvg, strftime('%H:%M', Timestamp) as hour FROM " + TABLE_PULSE + " GROUP BY strftime('%j-%H-%M', Timestamp)";
                    break;
            }

            ArrayList<Data> pulseRecord = new ArrayList<>();


            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();

            do {
                Data data = new Data();
                Float value = Float.parseFloat(cursor.getString(0));
                int avg;
                avg = Math.round(value);

                data.setData(avg);
                data.setTime(cursor.getString(1));

//            Log.d("Time String", "" + cursor.getString(1));
                pulseRecord.add(data);

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            return pulseRecord;
        }catch (CursorIndexOutOfBoundsException e){
            ArrayList<Data> pulseRecord = new ArrayList<>();
            return pulseRecord;
        }
    }

}