package com.wzc.criminalintent;

import java.io.Serializable;

/**
 * Created by wzc on 2017/7/22.
 *
 */

public class Time implements Serializable{
    private int mHour;
    private int mMinute;

    public Time(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    @Override
    public String toString() {
        return  wrapperNumber(mHour) + " : " + wrapperNumber(mMinute);
    }

    private String wrapperNumber(int number){
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }
}
