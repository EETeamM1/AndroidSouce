package com.transility.tim.android.devicepolicymanager;

import android.content.Intent;

import com.transility.tim.android.BuildConfig;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import devicepolicymanager.LogoutServiceClient;
import devicepolicymanager.MasterPasswordScreenLauncherBroadcast;
import devicepolicymanager.SessionTimeOutReceiver;

/**
 *Unit Test of SessionTimeOutReciever
 * Created by ambesh.kukreja on 8/16/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SessionTimeOutReciverTest {

    @Test
    public void test001IsReciverRegistred(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);
        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();

        Assert.assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = SessionTimeOutReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        Assert.assertTrue("SessionTimeOutReciverNotFound",receiverFound);

    }

    @Test
    public void test002IsLogOutServiceCalled(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);

        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();
        ShadowApplication.Wrapper reciver=null;
        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound){
                receiverFound = SessionTimeOutReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
                reciver=wrapper;
            }

        }

        reciver.broadcastReceiver.onReceive(shadowApplication.getApplicationContext(),new Intent());

        Intent serviceIntent = shadowApplication.peekNextStartedService();
        Assert.assertEquals("Expected the MyBroadcast service to be invoked",
                LogoutServiceClient.class.getCanonicalName(),
                serviceIntent.getComponent().getClassName());
    }
}
