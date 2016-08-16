package com.transility.tim.android.Utilities;


import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.transility.tim.android.InventoryManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import devicepolicymanager.MasterPasswordScreenLauncherBroadcast;
import devicepolicymanager.SessionTimeOutReceiver;

/**
 * Logger class to log application info and print handled exception.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class Utility {
    /**
     * Utility function to log error on console
     * @param tag Descriptive tag form whier the log is printed
     * @param log actual log message.
     */
    public static void logError(String tag,String log){
        Log.e(tag,log);
    }

    /**
     * Utility function to print exception.
     * @param e its the exception returned by Runtime.
     */
    public static void printHandledException(Exception e){
        e.printStackTrace();
    }

    /**
     * Check the internet connection is present or not.
     * @param context current context of the application.
     * @return true if the internet is connected or else false.
     */
    public static boolean checkInternetConnection(Context context){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conMgr.getActiveNetworkInfo();
        boolean isConnected;
        isConnected = networkInfo != null && networkInfo.isConnected();
        return isConnected;
    }

    /**
     * Removed the keyboard from the screen.
     * @param view current view on which keyboard is opened.
     */
    public static boolean removeKeyboardFromScreen(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
     return  imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    /**
     * Helper method to cancel the current intent which is called when the user session is over.
     * @param context current context of the application.
     */
    public static void cancelCurrentAlarmToLaunchTheLoginScreen(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
        appendLog("Login Screen Launcher Alarm cancelled");
    }

    /**
     * Helper method to cancel the pending intent to launch the Master Password Screen.
     * @param context current context of the application
     */
    public static void cancelCurrentAlarmToLaunchTheMasterPasswordScreen(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MasterPasswordScreenLauncherBroadcast.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
        appendLog("Master Password Alarm cancelled");
    }
    /**
     * Check whether Google Play Services are available.
     *
     * If not, then display dialog allowing user to update Google Play Services
     *
     * @return true if available, or false if not
     */
    public static boolean checkGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status=googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        else {
            Toast.makeText(context,"Please install or update google play services",Toast.LENGTH_LONG).show();
        }




        return false;
    }

    public static String getDeviceId(Context context){
        if (TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getDeviceId(context))){
            TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceID=telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(deviceID)){
                deviceID= android.os.Build.SERIAL;
            }
            TransiltiyInvntoryAppSharedPref.setDeviceId(deviceID,context);
            return deviceID;
        }
        else {
            return TransiltiyInvntoryAppSharedPref.getDeviceId(context);
        }
    }

    public static void appendLog(String text) {
        logError(Utility.class.getSimpleName(),text);
        File extStore = Environment.getExternalStoragePublicDirectory("");
        File logFile=new File(extStore.getPath(),"logFile.txt");
        Utility.logError(Utility.class.getSimpleName(),logFile.getPath());
        if (!logFile.exists()) {
            try {
                extStore.mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
              // TODO Auto-generated catch block
                printHandledException(e);
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text).append("\n");
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            printHandledException(e);
        }
    }

    /**
     * Clears the previous session token form shared pref.
     */
    public static void clearPreviousSessionToken(){
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(InventoryManagement.getContext(),"");
    }

}
