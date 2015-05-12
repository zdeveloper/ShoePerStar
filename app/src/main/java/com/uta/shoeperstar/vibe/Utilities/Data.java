package com.uta.shoeperstar.vibe.Utilities;

/**
 * Created by saads_000 on 4/6/2015.
 */
public class Data
{

//    public int startT;
//    public int startD;
    public int data;
    public String timeStamp;

    public Data() {
    }

    public Data(int Data)
    {
//        this.startT=start;
//        this.startD=startDate;
        this.data=Data;
    }

    public String getTime() {
        return timeStamp;
    }

    public void setTime(String ts) {
        this.timeStamp = ts;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

//    public int getDate(){
//        return startD;
//    }
//
//    public void setDate(int setDate) {
//        this.startD = setDate;
//    }


}