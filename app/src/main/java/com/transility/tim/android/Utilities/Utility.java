package com.transility.tim.android.Utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    }
}
