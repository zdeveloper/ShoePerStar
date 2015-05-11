package com.uta.shoeperstar.vibe.Utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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


        String CREATE_STEPCOUNT_TABLE = "CREATE TABLE " +
                TABLE_STEPCOUNT + "("
                + SC_COLUMN_STEPCOUNT + " INTEGER," + "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_STEPCOUNT_TABLE);


        String CREATE_PULSE_TABLE = "CREATE TABLE " +
                TABLE_PULSE + "("
                + P_COLUMN_PULSE + " INTEGER," + "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
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
        Log.d(null, "shtuff Created");
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


    public ArrayList<Data> getStepCount() {

        int i = 0;
        ArrayList<Data> stepRecord = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_STEPCOUNT;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);


        cursor.moveToFirst();

        do {
            Data data = new Data();
            data.setData(Integer.parseInt(cursor.getString(0)));
//            data.setDate(Integer.parseInt(cursor.getString(1)));
//            data.setTime(Integer.parseInt(cursor.getString(2)));
            stepRecord.add(data);


        } while (cursor.moveToNext());


        cursor.close();
        db.close();

        for (i = 0; i < stepRecord.size(); i++) {
            System.out.println("data: " + stepRecord.get(i).data);
        }
        return stepRecord;
    }


    public ArrayList<Data> getPulse() {


        ArrayList<Data> pulseRecord = new ArrayList<>();


        String query = "Select * FROM " + TABLE_PULSE;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        do {
            Data data = new Data();
            data.setData(Integer.parseInt(cursor.getString(0)));
//            data.setDate(Integer.parseInt(cursor.getString(1)));
//            data.setTime(Integer.parseInt(cursor.getString(2)));
            pulseRecord.add(data);

        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return pulseRecord;
    }

}