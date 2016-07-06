package com.transility.tim.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.transility.tim.android.Utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creating the Logout request bean.
 * Created by ambesh.kukreja on 7/5/2016.
 */
public class Logout implements  Parcelable{

    private String sessionToken;
    private String code;
    public static final Parcelable.Creator<Logout> CREATOR = new Parcelable.Creator<Logout>() {
        @Override
        public Logout createFromParcel(Parcel source) {
            return new Logout(source);
        }

        @Override
        public Logout[] newArray(int size) {
            return new Logout[size];
        }
    };

    private  Logout() {

    }


    public static Logout parseLogout(String jsonResponse){

        Logout logout=new Logout();
            try{
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONObject result  = jsonObject.getJSONObject("responseCode");
                logout.code=result.optString("code");
            }
            catch (JSONException e){
                Utility.printHandledException(e);
                logout=null;
            }

        return  logout;
    }
    public static String writeLogoutJson(String sessionToken){
        String logoutJson=null;
        JSONObject jsonObject =  new JSONObject();
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("sessionToken", sessionToken);
            jsonObject.put("parameters",paramObject);
            logoutJson=jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    return logoutJson;
    }
    public String getSessionToken(){
        return sessionToken;
    }
    private Logout(Parcel source) {
        final Object[] values = source.readArray(getClass().getClassLoader());
        int i = 0;
          sessionToken = (String)values[i++];

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{
                sessionToken
        });
    }
}
