package com.transility.tim.android.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.transility.tim.android.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Himanshu Bapna on 26/07/16.
 */
public class Report implements Parcelable {

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel source) {
            return new Report(source);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    private Report (){}

    private List<DeviceReport> deviceReportList;

    public static Report parseDeviceReport (String jsonResponse){
        Report report = new Report();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject result  = jsonObject.getJSONObject("result");

            JSONArray reportList = result.getJSONArray("deviceReportDtoList");

            report.deviceReportList = new ArrayList<>();
            for (int i=0; i<reportList.length(); i++){
                JSONObject reportObject = reportList.getJSONObject(i);

                DeviceReport deviceReport = new DeviceReport();
                deviceReport.setInTime(reportObject.optString("loginTIme"));
                deviceReport.setOutTime(reportObject.optString("logOutTime"));
                deviceReport.setUserId(reportObject.optString("userId"));
                deviceReport.setUserName(reportObject.optString("userName"));

                report.deviceReportList.add(deviceReport);
            }

        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "Unable to parse JSON: " + jsonResponse, e);
            report = null;
        }
        return  report;
    }

    public List<DeviceReport> getDeviceReportList(){
        return deviceReportList;
    }

    protected Report(Parcel source) {
        final Object[] values = source.readArray(getClass().getClassLoader());
        int i = 0;

        deviceReportList = (List<DeviceReport>)values[i++];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{
                deviceReportList
        });
    }
}
