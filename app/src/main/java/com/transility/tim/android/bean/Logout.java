package com.transility.tim.android.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creating the Logout request bean.
 * Created by ambesh.kukreja on 7/5/2016.
 */
public class Logout{

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
}
