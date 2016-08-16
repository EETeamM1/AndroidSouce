package com.transility.tim.android.devicepolicymanager;

import android.content.Intent;

import com.transility.tim.android.BuildConfig;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import devicepolicymanager.MasterPasswordScreenLauncherBroadcast;

/**
 * Unit test for MasterPasswordScreenLauncherBroadcast class
 * Created by ambesh.kukreja on 8/16/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MasterPasswordScreenLauncherBroadcastTest {

    @Test
    public void test001IsReciverRegistred(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);
        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();

        Assert.assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = MasterPasswordScreenLauncherBroadcast.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        Assert.assertTrue("MasterPasswordScreenLauncherBroadcastNotFound",receiverFound);
    }

    @Test
    public void test002CheckIfMasterPasswordScreenIsCalledSucessfull(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);


        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(shadowApplication.getApplicationContext(),false);
        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(shadowApplication.getApplicationContext(),false);
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(shadowApplication.getApplicationContext(),"123456789");
        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();
        ShadowApplication.Wrapper reciver=null;
        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound){
                receiverFound = MasterPasswordScreenLauncherBroadcast.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
                reciver=wrapper;
            }

        }

        reciver.broadcastReceiver.onReceive(shadowApplication.getApplicationContext(),new Intent());

        Assert.assertEquals(TransiltiyInvntoryAppSharedPref.getSessionToken(reciver.getContext()),"");
    }


}
