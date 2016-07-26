package com.transility.tim.android.Utilities;

import android.content.Context;

import com.transility.tim.android.BuildConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by himanshu bapna on 20/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TransiltiyInvntoryAppSharedPrefTest {

    Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getBaseContext();
    }

    @After
    public void tearDown() {
        context = null;
    }

    @Test
    public void testGetMasterPasswordToSharedPref() {
        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(context,"masterpassword");
        Assert.assertEquals("Master password is incorrect store in prefrences", "masterpassword", TransiltiyInvntoryAppSharedPref.getMasterPassword(context));
    }

    @Test
    public void testGetUserNameToSharedPref() {
        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(context, "impetus");
        Assert.assertEquals("Username is incorrect store in prefrences", "impetus", TransiltiyInvntoryAppSharedPref.getUserName(context));
    }

    @Test
    public void testGetWasLoginScreenVisible() {
        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context, true);
        Assert.assertTrue("Incorrect value store in prefrences", TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context));

        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(context, false);
        Assert.assertFalse("Incorrect value store in prefrences", TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(context));
    }

    @Test
    public void testGetKeyDeviceLastShutdownTime() {
        TransiltiyInvntoryAppSharedPref.setDeviceLastShutdownTime(context, 30);
        Assert.assertEquals("Device last shut down time is incorrect store in prefrences", 30, TransiltiyInvntoryAppSharedPref.getDeviceLastShutdownTime(context));
    }

    @Test
    public void testGetDeviceId() {
        TransiltiyInvntoryAppSharedPref.setDeviceId("1234567890", context);
        Assert.assertEquals("Device id is incorrect store in prefrences", "1234567890", TransiltiyInvntoryAppSharedPref.getDeviceId(context));
    }

    @Test
    public void testWasMasterPasswordScreenVisible() {
        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(context, true);
        Assert.assertTrue("Incorrect value for isMasterPasswordScreenVisible key", TransiltiyInvntoryAppSharedPref.isMasterPasswordScreenVisible(context));
    }
}
