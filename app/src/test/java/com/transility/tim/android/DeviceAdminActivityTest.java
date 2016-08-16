package com.transility.tim.android;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.gms.ShadowGooglePlayServicesUtil;
import org.robolectric.util.ActivityController;

/**
 * Created by ambesh.kukreja on 7/14/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class DeviceAdminActivityTest {

    private  ActivityController<DeviceAdminActivity> transilityDeviceAdminActivityActivityController;
    private DeviceAdminActivity deviceAdminActivity;

    private Context context;

    @Before
    public void setUp() {

        context = RuntimeEnvironment.application.getBaseContext();



        transilityDeviceAdminActivityActivityController = Robolectric.buildActivity(DeviceAdminActivity.class).create();
        ShadowGooglePlayServicesUtil.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
    }

    @After
    public void tearDown() {
        context = null;

        transilityDeviceAdminActivityActivityController = null;
        deviceAdminActivity = null;
    }

    @Test
    public void test001CheckUiWhenUserFirstLaunchTheApplication(){
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();


         Switch enableDeviceApp= (Switch) deviceAdminActivity.findViewById(R.id.enableDeviceApp);

         Button logoutBtn= (Button) deviceAdminActivity.findViewById(R.id.logoutBtn);

           Button reportsBtn= (Button) deviceAdminActivity.findViewById(R.id.reportsBtn);

        TextView messageLineTv= (TextView) deviceAdminActivity.findViewById(R.id.messageLineTv);

        Assert.assertTrue("Enable Admin app switch is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertFalse("Enable Admin app should be disable by default",enableDeviceApp.isChecked());
        Assert.assertTrue("Logout button should not be visible",logoutBtn.getVisibility()==View.GONE);
        Assert.assertTrue("Reports button should not be visible",reportsBtn.getVisibility()==View.GONE);
        Assert.assertEquals("Message is incorrect",messageLineTv.getText(), deviceAdminActivity.getString(R.string.textAdminAppTextWhenDisabled));

    }
    @Test
    public void test002CheckUiWhenUserHasPerformedLogin(){
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();

        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(context, "Test");
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(context, "Test Session Token");
        TransiltiyInvntoryAppSharedPref.setSessionTimeoutToSharedPref(context, 10);

        Switch enableDeviceApp= (Switch) deviceAdminActivity.findViewById(R.id.enableDeviceApp);
        Button logoutBtn= (Button) deviceAdminActivity.findViewById(R.id.logoutBtn);
        Button reportsBtn= (Button) deviceAdminActivity.findViewById(R.id.reportsBtn);



        Assert.assertTrue("Enable Admin app switch is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertTrue("Logout button should not be visible",logoutBtn.getVisibility()==View.GONE);
        Assert.assertTrue("Reports button should not be visible",reportsBtn.getVisibility()==View.GONE);

    }



}
