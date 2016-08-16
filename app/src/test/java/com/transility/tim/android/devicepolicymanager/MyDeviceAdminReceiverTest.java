package com.transility.tim.android.devicepolicymanager;

import android.content.Intent;

import com.transility.tim.android.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import devicepolicymanager.MyDeviceAdminReceiver;
import devicepolicymanager.SessionTimeOutReceiver;

/**
 * Unit test for MyDeviceAdminReciver
 * Created by ambesh.kukreja on 8/16/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MyDeviceAdminReceiverTest {
    @Test
    public void test001IsReceiverRegistred(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);
        List<ShadowApplication.Wrapper> registeredReceivers = shadowApplication.getRegisteredReceivers();

        Assert.assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = MyDeviceAdminReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        Assert.assertTrue("MyDeviceAdminReceiver Not Found",receiverFound);

    }
    @Test
    public void test002CheckIfAllTheActionsAreRecived(){
        ShadowApplication shadowApplication= Shadows.shadowOf(RuntimeEnvironment.application);
        Intent deviceAdminIntent = new Intent("android.app.action.DEVICE_ADMIN_ENABLED");
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(deviceAdminIntent));
        deviceAdminIntent = new Intent("android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED");
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(deviceAdminIntent));
        deviceAdminIntent = new Intent("android.app.action.DEVICE_ADMIN_DISABLED");
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(deviceAdminIntent));
        deviceAdminIntent = new Intent("android.intent.action.ACTION_SHUTDOWN");
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(deviceAdminIntent));
        deviceAdminIntent = new Intent("android.intent.action.BOOT_COMPLETED");
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(deviceAdminIntent));
    }



}
