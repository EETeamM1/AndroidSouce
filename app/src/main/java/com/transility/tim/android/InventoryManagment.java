package com.transility.tim.android;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Himanshu Bapna.
 */
public class InventoryManagment extends Application {

    public static final int SERVER_ERROR_DIALOG_ID = 1001;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

    }



    public void handleServerError() {
        handleServerError((String)null);
    }

    public void handleServerError(String message) {
        showServerError(message, SERVER_ERROR_DIALOG_ID);
    }

    private void showServerError(String message, int dialogId) {

        //TODO error handling wil do here
        Toast.makeText(this, "error  server not responding " +
                message, Toast.LENGTH_LONG).show();

    }
}
