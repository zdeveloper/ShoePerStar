package com.uta.shoeperstar.vibe.Utility;

/**
 * Created by saads_000 on 4/6/2015.
 */
    public class dataSetup
    {

        public int startT;
        public int startD;
        public int data;

        public dataSetup(){}

        public dataSetup(int Data, int start, int startDate)
        {
            this.startT=start;
            this.startD=startDate;
            this.data=Data;
        }

        public int getTime() {
            return startT;
        }

        public void setTime(int startTime) {
            this.startT = startTime;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public int getDate(){
            return startD;
        }

        public void setDate(int setDate) {
            this.startD = setDate;

        }


    }
