package com.transility.tim.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.widget.Toast;

import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;

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
        initateAllowPolicyToApplication ();
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
        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(this,getString(R.string.masterUserName));

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
