package com.transility.tim.android.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Preference class that interacts with the Shared Preference of the device.
 * Created by ambesh.kukreja on 6/8/2016.
 */
public class TransiltiyInvntoryAppSharedPref  {

    private final static String TRANSILITY_INVENTORY_SHARED_PREF="tranility_inventory_shared_pref";
    private final static String KEY_MASTER_PASSWORD="key_master_password";
    private final static String KEY_USER_NAME="key_user_name";
    private final static String KEY_WAS_LOGIN_SCREEN_VISIBLE="wasLoginScreenVisible";
    private final static String KEY_DEVICE_LAST_SHUTDOWN_TIME="deviceLastShuDownTime";

    private final static String KEY_DEVICE_ID="deviceId";
    private final static String KEY_SESSION_TIMEOUT = "key_session_timeout";
    private final static String KEY_SESSION_TOKEN = "key_session_token";
    private final static String KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE="isMasterPasswordScreenVisible";

    /**
     * Set the master password to shared preference
     * @param context current context of the application
     * @param masterPassword master password that need to be saved in shared pref
     */
    public static void setMasterPasswordToSharedPref(Context context,String masterPassword){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_MASTER_PASSWORD,masterPassword);
        editor.apply();
    }

    /**
     * Get the current master password to shared preference
     * @param context current context of the application
     *
     */
    public static String getMasterPassword(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getString(KEY_MASTER_PASSWORD,"");
    }


    /**
     * Set the user name to shared preference
     * @param context current context of the application
     * @param userName user name that need to be saved in shared pref
     */
    public static void setUserNameToSharedPref(Context context,String userName){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_USER_NAME,userName);
        editor.apply();
    }

    /**
     * Get the current user name saved in shared preference
     * @param context current context of the application.
     * @return current userName or blank in case no user name is saved.
     */
    public static String getUserName(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getString(KEY_USER_NAME,"");
    }

    /**
     * Set if the login screen is visible
     * @param context current context of the application
     * @param isScreenVisible is set to true if the Login Screen is visible or false otherwise
     */
    public static void setWasLoginScreenVisible(Context context,boolean isScreenVisible){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean(KEY_WAS_LOGIN_SCREEN_VISIBLE, isScreenVisible);
        edit.apply();
    }

    /***
     *
     * @param context current context of the application
     * @return true if the login screen was visible false otherwise.
     */
    public static boolean getWasLoginScreenVisible(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getBoolean(KEY_WAS_LOGIN_SCREEN_VISIBLE, false);
    }

    /**
     * Set the time stamp to shared prefrence when the device was last shut down
     * @param context current context of the application
     * @param time time stamp at which device was last shut down.
     */
    public static  void setDeviceLastShutdownTime(Context context, long time){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putLong(KEY_DEVICE_LAST_SHUTDOWN_TIME, time);
        edit.apply();
    }

    /**
     *
     * @param context current context of the application
     * @return the last shutdown time of the device.
     */
    public static long getDeviceLastShutdownTime(Context context){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getLong(KEY_DEVICE_LAST_SHUTDOWN_TIME, 0);
    }

    /**
     * Set the device id to shared preference.
     * @param deviceId devie id of the device
     * @param context of the application
     */
    public static void setDeviceId(String deviceId,Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString(KEY_DEVICE_ID, deviceId);
        edit.apply();
    }

    /**
     *
     * @param context of the application
     * @return the device id if it is present in shared prefrence blank otherwise.
     */
    public static String getDeviceId(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getString(KEY_DEVICE_ID, "");
    }

    /**
     * Time out period at which the lock screen should get appreared.
     * @param context of the application
     * @param timeout time out period after which the device should get locked
     */
    public static void setSessionTimeoutToSharedPref(Context context, int timeout){
        SharedPreferences  sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(KEY_SESSION_TIMEOUT, timeout);
        editor.apply();
    }

    /**
     *
     * @param context of the application
     * @return Time out period at which the device should get clocked blank otherwise.
     */
    public static int getSessionTimeout(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return sp.getInt(KEY_SESSION_TIMEOUT, 30);
    }

    /**
     * Current session token of the application.
     * @param context of the application
     * @param sessionToken current session token of the application
     */
    public static void setSessionTokenToSharedPref(Context context,String sessionToken){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_SESSION_TOKEN,sessionToken);
        editor.apply();
    }

    /**
     *
     * @param context of the application.
     * @return The current session token of the application.
     */
    public static String getSessionToken(Context context){
        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        return  sp.getString(KEY_SESSION_TOKEN,"");
    }

    /**
     * Set if Master password screen is visible.
     * @param context of the application.
     * @param isMasterPasswordScreenVisible if Master Password screen is visible
     */
    public static void setIsMasterPasswordScreenVisible(Context context,boolean isMasterPasswordScreenVisible){

        SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE,isMasterPasswordScreenVisible);
        editor.apply();

    }

    /**
     *
     * @param context of the application.
     * @return true if the master password screen is visible false otherwise.
     */
    public static boolean isMasterPasswordScreenVisible(Context context){
    SharedPreferences sp=context.getSharedPreferences(TRANSILITY_INVENTORY_SHARED_PREF,Context.MODE_PRIVATE);
    return  sp.getBoolean(KEY_IS_MASTER_PASSWORD_SCREEN_VISIBLE,false);
}
}
