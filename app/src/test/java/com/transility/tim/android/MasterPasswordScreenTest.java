package com.transility.tim.android;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.transility.tim.android.Utilities.Utility;

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

import devicepolicymanager.MyDeviceAdminReciver;

import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by impadmin on 20/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MasterPasswordScreenTest {

    ActivityController<MasterPasswordScreen> activityController;
    private MasterPasswordScreen  masterPasswordScreenActivity;
    Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getBaseContext();
        activityController = Robolectric.buildActivity(MasterPasswordScreen.class).create();
        masterPasswordScreenActivity= activityController.start().resume().visible().get();
    }

    @After
    public void tearDown() {
        context = null;
        activityController = null;
        masterPasswordScreenActivity = null;
    }

    @Test
    public void testCheckUI() {
        View activityView = masterPasswordScreenActivity.attacheViewWithIdToWindow(R.layout.layout_master_password_screen);

        Button masterpasswordEntredBtn= (Button) activityView.findViewById(R.id.masterpasswordEntredBtn);
        Assert.assertEquals("The text on master password button is incorrect.", "Submit", masterpasswordEntredBtn.getText());

        Button continueWithAdminPolicy= (Button) activityView.findViewById(R.id.continueWithAdminPolicy);
        Assert.assertEquals("The text on admin policy button is incorrect.", "Continue with Admin Policies", continueWithAdminPolicy.getText());

        EditText passwordFieldEt= (EditText) activityView.findViewById(R.id.passwordFieldEt);
        Assert.assertEquals("The text shown on Password is incorrect.", "Enter Master Password", passwordFieldEt.getHint());
    }

    @Test
    public void testAdminPolicyButtonOnClick() {

        masterPasswordScreenActivity.continueWithAdminPolicy.performClick();
        ShadowActivity shadowActivity = shadowOf(masterPasswordScreenActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Intent action is incorrect", DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN, startedIntent.getAction());
        ComponentName truitonDevicePolicyAdmin = startedIntent.getParcelableExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN);
        Assert.assertEquals("Intent parceable value is incorrect", MyDeviceAdminReciver.class.getName(), truitonDevicePolicyAdmin.getClassName());
    }

    @Test
    public void testMasterPasswordButtonOnClick() {
        masterPasswordScreenActivity.passwordFieldEt.setText("");
        masterPasswordScreenActivity.masterpasswordEntredBtn.performClick();
        Assert.assertEquals("Invalid error message", "Password cannot be empty.", masterPasswordScreenActivity.passwordFieldEt.getError());

        masterPasswordScreenActivity.passwordFieldEt.setText("1234567890abcdefg");
        masterPasswordScreenActivity.masterpasswordEntredBtn.performClick();
        Assert.assertEquals("Invalid error message", "Master Password cannot be more than 15 characters of length.", masterPasswordScreenActivity.passwordFieldEt.getError());

    }

    @Test
    public void testApplyLamPortAlgoRithmUsingDateOnImei() {
        Assert.assertEquals("Algorithm value is incorrect", "1234567900333", masterPasswordScreenActivity.applyLamPortAlgoRithmUsingDateOnImei("1234567890123"));
        Assert.assertEquals("Algorithm value is incorrect", "123456789108199", masterPasswordScreenActivity.applyLamPortAlgoRithmUsingDateOnImei("1234567890abcd"));
        Assert.assertEquals("Algorithm value is incorrect", "123456789108199", masterPasswordScreenActivity.applyLamPortAlgoRithmUsingDateOnImei("1234567890abcd234"));
    }

}
