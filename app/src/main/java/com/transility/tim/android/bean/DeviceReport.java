package com.transility.tim.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by himanshu bapna on 26/07/16.
 */
public class DeviceReport implements Parcelable {
    public static final Creator<DeviceReport> CREATOR = new Creator<DeviceReport>() {
        @Override
        public DeviceReport createFromParcel(Parcel in) {
            return new DeviceReport(in);
        }

        @Override
        public DeviceReport[] newArray(int size) {
            return new DeviceReport[size];
        }
    };

    private String inTime;
    private String outTime;
    private String userId;
    private String userName;


    public  DeviceReport(){}

    private DeviceReport(Parcel source) {
        final Object[] values = source.readArray(getClass().getClassLoader());
        int i = 0;

        inTime = (String)values[i++];
        outTime = (String)values[i++];
        userId = (String)values[i++];
        userName = (String)values[i++];
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{
                inTime,
                outTime, userId, userName
        });
    }
}
