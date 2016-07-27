package com.transility.tim.android.Utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.transility.tim.android.InventoryManagment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import devicepolicymanager.SessionTimeOutReciever;

/**
 * Logger class to log application info and print handled exception.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class Utility {
    /**
     * Utility function to log error on console
     * @param tag
     * @param log
     */
    public static void logError(String tag,String log){
        Log.e(tag,log);
    }

    /**
     * Utility function to print exception.
     * @param e
     */
    public static void printHandledException(Exception e){
        e.printStackTrace();
    }

    /**
     * Chcek the internet connection is present or not.
     * @param context
     * @return
     */
    public static boolean checkInternetConnection(Context context){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conMgr.getActiveNetworkInfo();
        boolean isConnected=false;
        isConnected = networkInfo != null && networkInfo.isConnected();
        return isConnected;
    }

    public static void removeKeyboardfromScreen(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void cancelCurrentPendingIntent(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
        appendLog("Alarm cancelled");
    }

    /**
     * Check whether Google Play Services are available.
     *
     * If not, then display dialog allowing user to update Google Play Services
     *
     * @return true if available, or false if not
     */
    public static boolean checkGooglePlayServicesAvailable(Activity context) {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        else {
            Toast.makeText(context,"Please install or update google play services",Toast.LENGTH_LONG).show();
        }

//        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
//            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, context, 1);
//            if (errorDialog != null) {
//                errorDialog.show();
//            }
//        }


        return false;
    }

    public static String getDeviceId(Context context){
        if (TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getDeviceId(context))){
            TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceID=telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(deviceID)){
                deviceID= Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            TransiltiyInvntoryAppSharedPref.setDeviceId(deviceID,context);
            return deviceID;
        }
        else {
            return TransiltiyInvntoryAppSharedPref.getDeviceId(context);
        }
    }

    public static void appendLog(String text) {
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
            buf.append(text+"\n");
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            printHandledException(e);
        }
    }

    /**
     * Method call to clean the database
     */
    public static void clearPrefrences(){
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(InventoryManagment.getContext(),"");
    }

}
