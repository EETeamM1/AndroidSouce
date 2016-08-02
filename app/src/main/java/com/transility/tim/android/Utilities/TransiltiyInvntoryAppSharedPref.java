package com.transility.tim.android.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Prefrence class that interacts with the Shared Prefrence of the device.
 * Created by ambesh.kukreja on 6/8/2016.
 */
public class TransiltiyInvntoryAppSharedPref  {

    private final static String TRANSILITY_INVENTORY_SHARED_PREF="tranility_inventory_shared_pref";
    private final static String KEY_MASTER_PASSWORD="key_master_password";
    private final static String KEY_USER_NAME="key_user_name";
    private final static String KEY_WAS_LOGIN_SCREEN_VISIBLE="wasLoginScreenVisible";
    private final static String KEY_DEVICE_LAST_SHUTDOWN_TIME="deviceLastShuDownTime";
    private final static String KEY_USER_PREF_TAKEN="userPrefTaken";
    private final static String KEY_DEVICE_ID="deviceId";
    private final static String KEY_SESSION_TIMEOUT = "key_session_timeout";
    private final static String KEY_SESSION_TOKEN = "key_session_token";
    private final static String KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE="isMasterPasswordScreenVisible";

    public static void setMasterPasswordToSharedPref(Context context,String masterPassword){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_MASTER_PASSWORD,masterPassword);
        editor.apply();
    }

    public static String getMasterPassword(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getString(KEY_MASTER_PASSWORD,"");
    }

    public static void setUserNameToSharedPref(Context context,String userName){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_USER_NAME,userName);
        editor.apply();
    }

    public static String getUserName(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getString(KEY_USER_NAME,"");
    }

    public static void setWasLoginScreenVisible(Context context,boolean isScreenVisible){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean(KEY_WAS_LOGIN_SCREEN_VISIBLE, isScreenVisible);
        edit.apply();
    }

    public static boolean getWasLoginScreenVisible(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getBoolean(KEY_WAS_LOGIN_SCREEN_VISIBLE, false);
    }

    public static  void setDeviceLastShutdownTime(Context context, long time){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putLong(KEY_DEVICE_LAST_SHUTDOWN_TIME, time);
        edit.apply();
    }

    public static long getDeviceLastShutdownTime(Context context){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getLong(KEY_DEVICE_LAST_SHUTDOWN_TIME, 0);
    }

    public static void setDeviceId(String deviceId,Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString(KEY_DEVICE_ID, deviceId);
        edit.apply();
    }

    public static String getDeviceId(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getString(KEY_DEVICE_ID, "");
    }

    public static void setSessionTimeoutToSharedPref(Context context, int timeout){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(KEY_SESSION_TIMEOUT, timeout);
        editor.apply();
    }

    public static int getSessionTimeout(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getInt(KEY_SESSION_TIMEOUT, 30);
    }

    public static void setSessionTokenToSharedPref(Context context,String userName){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_SESSION_TOKEN,userName);
        editor.apply();
    }

    public static String getSessionToken(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getString(KEY_SESSION_TOKEN,"");
    }
    public static void setIsMasterPasswordScreenVisible(Context context,boolean isMasterPasswordScreenVisible){

        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE,isMasterPasswordScreenVisible);
        editor.apply();

    }
public static boolean isMasterPasswordScreenVisible(Context context){
    SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
    return  sp.getBoolean(KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE,false);
}
}
