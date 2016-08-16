package com.transility.tim.android;

import android.app.Application;
import android.text.TextUtils;

import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

/**
 * Added Comment Ambesh Kurkeja: This application file is created to keep all the resources that should be available in application scope.
 * Created by Himanshu Bapna.
 */
public class InventoryManagement extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultMasterCredential();

    }

    /**
     * Set default Master username and password in shared preference.
     *
     */
    private void setDefaultMasterCredential() {
        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(this, getString(R.string.masterUserName));
        if(TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getMasterPassword(this))){
            TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(this, getString(R.string.masterPassword));
        }
        Utility.appendLog("Default Admin Password =" + TransiltiyInvntoryAppSharedPref.getMasterPassword(this)
                + "" + "and UserName=" + TransiltiyInvntoryAppSharedPref.getUserName(this));
    }


//==================================================================
// Section - Context access
//==================================================================

    private static InventoryManagement appContext = null;

    public InventoryManagement() {
        appContext = this;
    }

    /**
     * Provides access to the application context.  Handy if you need to
     * pull a Context out of thin air.
     * Guidelines for obtaining a Context:
     *   (1) When in an Activity, use the "this" pointer as some UI
     *   operations will choke if given anything else.
     *   (2) When outside of an Activity, use this.
     * @return - The current application context
     */
    public static InventoryManagement getContext() {
        return appContext;
    }
}
