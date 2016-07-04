package com.transility.tim.android;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 *
 * This test has been created to check the various functionality fo LoginActivity.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class LoginActivityTest implements UnitTestHelperInventoryManagementApp {


    private LoginActivity  mLoginActivity;






    @Before
    @Override
    public void setUpBeforeEachTestCase() {
        mLoginActivity= Robolectric.buildActivity(LoginActivity.class).create().resume().visible().get();
    }

    @After
    @Override
    public void tearDownAfterEachTestCase() {

    }

    @Test
    public void test001CheckIfTextOnUiComponentsIsCorrect(){
//        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//
//       WindowManager winManager = ((WindowManager)mLoginActivity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
//
//       ViewGroup wrapperView = new RelativeLayout(mLoginActivity);
//        wrapperView.setBackgroundColor(mLoginActivity.getResources().getColor(R.color.backWhite));
//
//        View activityView= View.inflate(mLoginActivity, R.layout.activity_login, wrapperView);

        View  activityView  =mLoginActivity.getWindow().peekDecorView();

        String userNameEtText=mLoginActivity.getString(R.string.textUserName);

//        activityView= mLoginActivity.getParentViewOfThisActivity();

//        EditText mUserNameEt = (EditText) activityView.findViewById(R.id.email);
//
//        assertEquals("The text on User Name is incorrect.",userNameEtText,mUserNameEt.getHint());
//
//        String passwordTvText=mLoginActivity.getString(R.string.textPassword);
//
//        EditText mPasswordEt= (EditText) activityView.findViewById(R.id.password);
//        assertEquals("The text shown on Password is incorrect.",passwordTvText,mPasswordEt.getHint());
//
//        String emailTextEt=mLoginActivity.getString(R.string.action_sign_in);
//        Button email_sign_in_button= (Button) activityView.findViewById(R.id.email_sign_in_button);
//
//        assertEquals("The text on login button is incorrect.",emailTextEt,email_sign_in_button.getText());
//
//        ProgressBar login_progress= (ProgressBar) activityView.findViewById(R.id.login_progress);
//        assertTrue("Progress Bar should not be visible.",login_progress.getVisibility()== View.GONE);
//
//        TextView mErrorTv= (TextView) activityView.findViewById(R.id.responseAndProgressMessageTv);
//
//        String errorTvText="";

//        assertEquals("No text should be shown on error response text view.",errorTvText,mErrorTv.getText());

    }

   public void test002CheckCorrectMasterPasswordIsAuthenticated(){



   }


}