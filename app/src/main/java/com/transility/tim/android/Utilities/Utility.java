package com.transility.tim.android.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conMgr.getActiveNetworkInfo();
        if (networkInfo!=null&&networkInfo.isConnected()){
            return true;
        }
        else {
            return  false;
        }
    }

}
