package com.transility.tim.android.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.transility.tim.android.BuildConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowInputMethodManager;
import org.robolectric.shadows.ShadowNetworkInfo;
import org.robolectric.shadows.ShadowTelephonyManager;

/**
 * Unit test to test Utility class
 * Created by ambesh.kukreja on 8/11/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UtilityTest {
    Context context;
    @Before
    public void setUp(){

        context= RuntimeEnvironment.application.getApplicationContext();
    }

    @After
    public void tearDown(){
        context=null;

    }

    @Test
    public void test001CheckInternetConnectionWorking(){
        ConnectivityManager cm = (ConnectivityManager)
                RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowNetworkInfo shadowConnectivityManager= Shadows.shadowOf(cm.getActiveNetworkInfo());
        shadowConnectivityManager.setAvailableStatus(false);
        shadowConnectivityManager.setConnectionStatus(false);


        Assert.assertFalse("Internet Shwould Not Get Connected",Utility.checkInternetConnection(context));

        shadowConnectivityManager.setAvailableStatus(true);
        shadowConnectivityManager.setConnectionStatus(true);
        Assert.assertTrue("Internet Should be Connected",Utility.checkInternetConnection(context));
    }

    @Test
    public void test002CheckInputMethodManager(){


        InputMethodManager inputMethodManager= (InputMethodManager) RuntimeEnvironment.application.getSystemService(Context.INPUT_METHOD_SERVICE);
        ShadowInputMethodManager shadowInputMethodManager=Shadows.shadowOf(inputMethodManager);
        View view =new View(context);
        shadowInputMethodManager.showSoftInput(view,0);

       Assert.assertTrue("Key Board Is Removed",Utility.removeKeyboardFromScreen(view));
    }

    @Test
    public void test004CheckIfProperDeviceIdisReturned(){
        String testDeviceId="12345678910";
        TelephonyManager telephonyManager= (TelephonyManager) RuntimeEnvironment.application.getSystemService(Context.TELEPHONY_SERVICE);
        ShadowTelephonyManager shadowTelephonyManager=Shadows.shadowOf(telephonyManager);
        shadowTelephonyManager.setDeviceId(testDeviceId);

        Assert.assertNotNull("Device id cannot be null",Utility.getDeviceId(RuntimeEnvironment.application));
        Assert.assertEquals("Test Device id and device id does not match",testDeviceId,Utility.getDeviceId(RuntimeEnvironment.application));
    }

    @Test
    public void test005CheckIfPreviousSessionIsDeleted(){
        String testSessionToken="1234567890";
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(context,testSessionToken);
        Assert.assertEquals("Session token mismatch",TransiltiyInvntoryAppSharedPref.getSessionToken(context),testSessionToken);
        Utility.clearPreviousSessionToken();
        Assert.assertEquals("Session token was not cleared",TransiltiyInvntoryAppSharedPref.getSessionToken(context),"");

    }

}
