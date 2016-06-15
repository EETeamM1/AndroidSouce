package com.transility.tim.android;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.widget.Toast;

import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;

/**
 * Added Comment Ambesh Kurkeja: This application file is created to keep all the reources that shwould be avaliable in application scope.
 * Created by Himanshu Bapna.
 */
public class InventoryManagment extends Application {

    public static final int SERVER_ERROR_DIALOG_ID = 1001;

    private static Context mContext;
    private SQLiteDatabase sqLiteDatabase;

    private InventoryDatabaseManager inventoryDatabaseManager;
    @Override
    public void onCreate() {
        super.onCreate();
        initiateDatabase();
    }

    /**
     * Initialise the database for internal access.
     */
    private void initiateDatabase() {
        /**
         * Currently version number is harcoded need to change it to take from build config file.
         */
        inventoryDatabaseManager=new InventoryDatabaseManager(this,this.getString(R.string.app_name),null,1,null);
        sqLiteDatabase=inventoryDatabaseManager.getWritableDatabase();
    }


    public void handleServerError() {
        handleServerError((String)null);
    }

    public void handleServerError(String message) {
        showServerError(message, SERVER_ERROR_DIALOG_ID);
        initateAllowPolicyToApplication();
    }

    private void initateAllowPolicyToApplication() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);


        }

    }

    private void showServerError(String message, int dialogId) {

        //TODO error handling wil do here
        Toast.makeText(this, "error  server not responding " +
                message, Toast.LENGTH_LONG).show();

    }


    /**
     * Returns the current instance of Database manager
     * @return
     */
    public InventoryDatabaseManager getInventoryDatabasemanager(){
        return  inventoryDatabaseManager;
    }

    /**
     * Returns the object of Sqlite Database object.
     * @return
     */
    public SQLiteDatabase getSqliteDatabase(){
        return sqLiteDatabase;
    }
}
