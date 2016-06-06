package com.transility.tim.android.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.transility.tim.android.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Himanshu Bapna  on 03/06/16.
 */
public class Logon implements Parcelable{

    public static final Creator<Logon> CREATOR = new Creator<Logon>() {
        @Override
        public Logon createFromParcel(Parcel source) {
            return new Logon(source);
        }

        @Override
        public Logon[] newArray(int size) {
            return new Logon[size];
        }
    };

    public Logon (){}

    private String masterPassword;
    private int timeout;
    private String sessionToken;

    public static Logon parseLogon (String jsonResponse){
         Logon logon = new Logon();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject result  = jsonObject.getJSONObject("result");

            logon.sessionToken = result.getString("sessionToken");
            logon.masterPassword = result.optString("masterPassword");
            logon.timeout = Integer.parseInt(jsonObject.optString("timeout").toString());


        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "Unable to parse JSON: " + jsonResponse, e);
            logon = null;
        }
        return  logon;
    }

    public static String writeLogonJSON (String username, String password, long deviceId){

        String logonJSON=null;
        try {
            JSONObject jsonObject =  new JSONObject();
            JSONObject paramObject = new JSONObject();

            paramObject.put("userId", username);
            paramObject.put("password", password);
            paramObject.put("deviceId", deviceId);

            //TODO add os version
//            paramObject.put("osVersion", "osVersion");

            //TODO add latiude and logitude
            //paramObject.put("latitude", "latitude");
            //paramObject.put("longitude", "longitude");

            jsonObject.put("parameters",paramObject);

            logonJSON = jsonObject.toString();


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(Constants.LOGTAG, "Unable to write Logon JSON: ", e);
        }

        return logonJSON;
    }

    public String getMasterPassword(){
        return  masterPassword;
    }

    public String getSessionToken (){
        return sessionToken;
    }

    public  int getTimeout(){
        return timeout;
    }


    protected Logon(Parcel source) {
        final Object[] values = source.readArray(getClass().getClassLoader());
        int i = 0;

        masterPassword = (String)values[i++];
        sessionToken = (String)values[i++];
        timeout = (Integer)values[i++];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{
                masterPassword,
                sessionToken,
                timeout
        });
    }
}
