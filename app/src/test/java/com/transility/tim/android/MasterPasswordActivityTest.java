package com.transility.tim.android;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.util.Calendar;

import devicepolicymanager.MyDeviceAdminReciver;

import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by impadmin on 20/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MasterPasswordActivityTest {

    ActivityController<MasterPasswordActivity> activityController;
    private MasterPasswordActivity activity;
    Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getBaseContext();
        activityController = Robolectric.buildActivity(MasterPasswordActivity.class).create();
        activity = activityController.start().resume().visible().get();
    }

    @After
    public void tearDown() {
        context = null;
        activityController = null;
        activity = null;
    }

    @Test
    public void testCheckUI() {
        activity.attacheViewWithIdToWindowAndIntialiseViews(R.layout.layout_master_password_screen);
        Assert.assertEquals("The text on master password button is incorrect.", "Submit", activity.masterpasswordEntredBtn.getText());
        Assert.assertEquals("The text on admin policy button is incorrect.", "Continue with Admin Policies", activity.continueWithAdminPolicy.getText());
        Assert.assertEquals("The text shown on Password is incorrect.", "Enter Master Password", activity.passwordFieldEt.getHint());
    }

    @Test
    public void testAdminPolicyButtonOnClick() {

        activity.continueWithAdminPolicy.performClick();
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Intent action is incorrect", DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN, startedIntent.getAction());
        ComponentName truitonDevicePolicyAdmin = startedIntent.getParcelableExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN);
        Assert.assertEquals("Intent parceable value is incorrect", MyDeviceAdminReciver.class.getName(), truitonDevicePolicyAdmin.getClassName());
    }

    @Test
    public void testMasterPasswordButtonOnClick() {
        activity.passwordFieldEt.setText("");
        activity.masterpasswordEntredBtn.performClick();
        Assert.assertEquals("Invalid error message", "Password cannot be empty.", activity.passwordFieldEt.getError());

        activity.passwordFieldEt.setText("1234567890abcdefg");
        activity.masterpasswordEntredBtn.performClick();
        Assert.assertEquals("Invalid error message", "Master Password cannot be more than 15 characters of length.", activity.passwordFieldEt.getError());

    }

    @Test
    public void testApplyLamPortAlgoRithmUsingDateOnImei() {
        Calendar mockCalendar = Mockito.mock(Calendar.class);
        when(mockCalendar.get(Calendar.DAY_OF_MONTH)).thenReturn(10);
        when(mockCalendar.get(Calendar.MONTH)).thenReturn(8);
        when(mockCalendar.get(Calendar.YEAR)).thenReturn(2016);

        Assert.assertEquals("Algorithm value is incorrect", "1234567900293", activity.applyLamPortAlgoRithmUsingDateOnImei("1234567890123", mockCalendar));
        Assert.assertEquals("Algorithm value is incorrect", "78910815", activity.applyLamPortAlgoRithmUsingDateOnImei("1234567890abcd", mockCalendar));
        Assert.assertEquals("Algorithm value is incorrect", "78910815", activity.applyLamPortAlgoRithmUsingDateOnImei("1234567890abcd234", mockCalendar));
    }

}
