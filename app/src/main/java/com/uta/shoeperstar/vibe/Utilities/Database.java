package com.uta.shoeperstar.vibe.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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


    public Database(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_STEPCOUNT_TABLE = "CREATE TABLE " +
                TABLE_STEPCOUNT + "("
                + SC_COLUMN_STEPCOUNT + " INTEGER," + SC_COLUMN_DATE
                + " INTEGER," + SC_COLUMN_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_STEPCOUNT_TABLE);


        String CREATE_PULSE_TABLE = "CREATE TABLE " +
                TABLE_PULSE + "("
                + P_COLUMN_PULSE + " INTEGER," + P_COLUMN_DATE
                + " INTEGER," + P_COLUMN_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_PULSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

    }


    public void addStepCount(Data data) {

        ContentValues values = new ContentValues();
        values.put(SC_COLUMN_STEPCOUNT, data.getData());
        values.put(SC_COLUMN_DATE, data.getDate());
        values.put(SC_COLUMN_TIME, data.getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_STEPCOUNT, null, values);
        db.close();
        Log.d(null, "shtuff Created");
    }


    public void addPulse(Data data) {

        ContentValues values = new ContentValues();
        values.put(P_COLUMN_PULSE, data.getData());
        values.put(P_COLUMN_DATE, data.getDate());
        values.put(P_COLUMN_TIME, data.getTime());

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
            data.setDate(Integer.parseInt(cursor.getString(1)));
            data.setTime(Integer.parseInt(cursor.getString(2)));
            stepRecord.add(data);


        } while (cursor.moveToNext());


        cursor.close();
        db.close();

        for (i = 0; i < stepRecord.size(); i++) {
            System.out.println("data: " + stepRecord.get(i).data + " start Time: " + stepRecord.get(i).startT + " start Date: " + stepRecord.get(i).startD);
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
            data.setDate(Integer.parseInt(cursor.getString(1)));
            data.setTime(Integer.parseInt(cursor.getString(2)));
            pulseRecord.add(data);

        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        return pulseRecord;
    }

}










/*
public class Database extends SQLiteDatabaseHelper {

    public static SQLiteDatabase vibeDB = null;
    Data pulseData = new Data();
    Data stepData = new Data();

    ArrayList<Data> stepRecord = new ArrayList<>();
    ArrayList<Data> pulseRecord = new ArrayList<>();


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
                    + " (pulse, pulseDate, pulseTime)"
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
                    + " (steps, stepDate, stepTime)"
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

      //  System.out.println(stepData.getData());
    }



    public ArrayList<Data> retrieveSteps()
    {

        return stepRecord ;
    }



    public ArrayList<Data> retrievePulse()
    {

        return pulseRecord ;
    }

}
*/
