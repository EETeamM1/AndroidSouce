package com.transility.tim.android;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.gms.ShadowGooglePlayServicesUtil;
import org.robolectric.util.ActivityController;

import devicepolicymanager.MyDeviceAdminReciver;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by ambesh.kukreja on 7/14/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class DeviceAdminActivityTest {

    private  ActivityController<DeviceAdminActivity> transilityDeviceAdminActivityActivityController;
    private DeviceAdminActivity deviceAdminActivity;
    private InventoryManagment inventoryManagment;
    private Context context;

    @Before
    public void setUp() {

        context = RuntimeEnvironment.application.getBaseContext();
        inventoryManagment= (InventoryManagment) RuntimeEnvironment.application;
        ShadowApplication shadowApplication= org.robolectric.Shadows.shadowOf(RuntimeEnvironment.application);

        transilityDeviceAdminActivityActivityController = Robolectric.buildActivity(DeviceAdminActivity.class).create();
        ShadowGooglePlayServicesUtil.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
    }

    @After
    public void tearDown() {
        context = null;
        inventoryManagment = null;
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

        Assert.assertTrue("Enable Admin app swith is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertFalse("Enable Admin app should be disable by default",enableDeviceApp.isChecked());
        Assert.assertTrue("Logut button should not be visible",logoutBtn.getVisibility()==View.GONE);
        Assert.assertTrue("Reports button should not be visible",reportsBtn.getVisibility()==View.GONE);
        Assert.assertEquals("Message is incorrect", messageLineTv.getText(), deviceAdminActivity.getString(R.string.textAdminAppTextWhenDisabled));

    }
    @Test
    public void test002CheckUiWhenUserHasPerfromedLogin(){
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();

        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(context, "Test");
        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(context, "Test Session Token");
        TransiltiyInvntoryAppSharedPref.setSessionTimeoutToSharedPref(context, 10);

        Switch enableDeviceApp= (Switch) deviceAdminActivity.findViewById(R.id.enableDeviceApp);
        Button logoutBtn= (Button) deviceAdminActivity.findViewById(R.id.logoutBtn);
        Button reportsBtn= (Button) deviceAdminActivity.findViewById(R.id.reportsBtn);

        TextView messageLineTv= (TextView) deviceAdminActivity.findViewById(R.id.messageLineTv);

        Assert.assertTrue("Enable Admin app swith is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertTrue("Logut button should not be visible", logoutBtn.getVisibility() == View.GONE);
        Assert.assertTrue("Reports button should not be visible", reportsBtn.getVisibility() == View.GONE);
//        Assert.assertEquals("Message is incorrect",messageLineTv.getText(),deviceAdminActivity.getString(R.string.textAdminAppWhenEnabled));
    }

    @Test
    public void testLogoutButtonOnClick() {
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();

        TransiltiyInvntoryAppSharedPref.setSessionTokenToSharedPref(context, "");
        deviceAdminActivity.logoutBtn.performClick();

        ShadowActivity shadowActivity = shadowOf(deviceAdminActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Expected activity is not started", LoginActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
        Assert.assertTrue("Device Admin activity is not finish", deviceAdminActivity.isFinishing());

    }


    @Test
    public void testReportButtonOnClick() {
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();
        deviceAdminActivity.reportsBtn.performClick();

        ShadowActivity shadowActivity = shadowOf(deviceAdminActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Expected activity is not started", ReportsActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testEnableDeviceAppSwitch() {
        deviceAdminActivity = transilityDeviceAdminActivityActivityController.start().resume().visible().get();
        deviceAdminActivity.enableDeviceApp.setChecked(true);

        ShadowActivity shadowActivity = shadowOf(deviceAdminActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Intent action is incorrect", DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN, startedIntent.getAction());
        ComponentName truitonDevicePolicyAdmin = startedIntent.getParcelableExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN);
        Assert.assertEquals("Intent parceable value is incorrect", MyDeviceAdminReciver.class.getName(), truitonDevicePolicyAdmin.getClassName());
    }



//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    @Test
//    public void test003ChekIfUserIsLogoutFromScreenWhenInternetIsConnected(){
//
//        deviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();
//        InventoryDatabaseManager inventoryDatabaseManager=inventoryManagment.getInventoryDatabasemanager();
//        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
//        employeeInfoBean.setMasterPassword("Test");
//        employeeInfoBean.setSessionToken("Test Session Token");
//        employeeInfoBean.setTimeOutPeriod(10);
//        inventoryDatabaseManager.getEmployeeDataTable().insertEmployeeInfoToEmployeeInfoTable(inventoryManagment.getSqliteDatabase(),employeeInfoBean);
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    @Test
//    public void test004CheckIfUserLogoutFromScreenWhenInternetIsConnected(){
//
//        deviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();
//        InventoryDatabaseManager inventoryDatabaseManager=inventoryManagment.getInventoryDatabasemanager();
//        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
//        employeeInfoBean.setMasterPassword("Test");
//        employeeInfoBean.setSessionToken("Test Session Token");
//        employeeInfoBean.setTimeOutPeriod(10);
//        inventoryDatabaseManager.getEmployeeDataTable().insertEmployeeInfoToEmployeeInfoTable(inventoryManagment.getSqliteDatabase(),employeeInfoBean);
//        Assert.assertTrue("Device Admin activity should get destroyed",deviceAdminActivity.isDestroyed());
//    }
}
