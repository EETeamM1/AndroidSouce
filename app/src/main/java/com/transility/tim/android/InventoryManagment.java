package com.transility.tim.android;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

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
        inventoryDatabaseManager = new InventoryDatabaseManager(this, this.getString(R.string.app_name), null, 1, null);
        sqLiteDatabase = inventoryDatabaseManager.getWritableDatabase();
        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(this, getString(R.string.masterUserName));
        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(this, getString(R.string.masterPassword));

        Utility.appendLog("Default Admin Password =" + TransiltiyInvntoryAppSharedPref.getMasterPasswordToSharedPref(this)
                + "" + "and UserName=" + TransiltiyInvntoryAppSharedPref.getUserNameToSharedPref(this));


    }


    /**
     * Returns the current instance of Database manager
     *
     * @return
     */
    public InventoryDatabaseManager getInventoryDatabasemanager() {
        return inventoryDatabaseManager;
    }

    /**
     * Returns the object of Sqlite Database object.
     *
     * @return
     */
    public SQLiteDatabase getSqliteDatabase() {
        return sqLiteDatabase;
    }
}
