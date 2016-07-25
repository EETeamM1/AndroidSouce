package com.transility.tim.android;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import org.robolectric.util.ActivityController;

/**
 * This test has been created to check the various functionality fo LoginActivity.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class LoginActivityTest {

    ActivityController<LoginActivity> activityController;
    Context context;
    private LoginActivity mLoginActivity;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getBaseContext();
        activityController = Robolectric.buildActivity(LoginActivity.class).create();
        mLoginActivity = activityController.start().resume().visible().get();
    }

    @After
    public void tearDown() {
        context = null;
        activityController = null;
        mLoginActivity = null;
    }

    @Test
    public void testCheckUI() {

        View activityView = mLoginActivity.attacheViewWithIdToWindow(R.layout.activity_login);

        String userNameEtText = mLoginActivity.getString(R.string.textUserName);
        EditText mUserNameEt = (EditText) activityView.findViewById(R.id.username);
        Assert.assertEquals("The text on User Name is incorrect.", userNameEtText, mUserNameEt.getHint());

        String passwordTvText = mLoginActivity.getString(R.string.textPassword);
        EditText mPasswordEt = (EditText) activityView.findViewById(R.id.password);
        Assert.assertEquals("The text shown on Password is incorrect.", passwordTvText, mPasswordEt.getHint());

        String loginTxt = mLoginActivity.getString(R.string.action_sign_in);
        Button login = (Button) activityView.findViewById(R.id.login);
        Assert.assertEquals("The text on login button is incorrect.", loginTxt, login.getText());

        ProgressBar login_progress = (ProgressBar) activityView.findViewById(R.id.login_progress);
        Assert.assertTrue("Progress Bar should not be visible.", login_progress.getVisibility() == View.GONE);

        TextView mErrorTv = (TextView) activityView.findViewById(R.id.error_message);
        String errorTvText = "";
        Assert.assertEquals("No text should be shown on error response text view.", errorTvText, mErrorTv.getText());

        Assert.assertTrue("Login scren visbile prefrence is not correct", TransiltiyInvntoryAppSharedPref.getWasLoginScreenVisible(mLoginActivity));

    }

    @Test
    public void testAuthenticateUserThroughMasterPassword() {

        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(context, mLoginActivity.getString(R.string.masterUserName));
        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(context, mLoginActivity.getString(R.string.masterPassword));
        Assert.assertTrue("Master user is not authenticate",
                mLoginActivity.authenticateMasterUser(mLoginActivity.getString(R.string.masterPassword), mLoginActivity.getString(R.string.masterUserName)));
        Assert.assertFalse("Empty user is authenticate as Master", mLoginActivity.authenticateMasterUser("", ""));
    }

    @Test
    public void testLoginButtonOnClick() {

        //TODO add test case for missing username and password after change logic
        mLoginActivity.username.setText("user");
        mLoginActivity.password.setText("password");

        TransiltiyInvntoryAppSharedPref.setUserNameToSharedPref(context, "user");
        TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(context, "password");

        mLoginActivity.loginButton.performClick();
        Assert.assertEquals("Error message is incorrect", mLoginActivity.getString(R.string.textWindowWarning), mLoginActivity.errorMessage.getText());
    }


}