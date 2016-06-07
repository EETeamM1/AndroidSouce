package com.transility.tim.android.Utilities;

import android.util.Log;

/**
 * Logger class to log application info and print handled exception.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class LoggerClass {
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

}
