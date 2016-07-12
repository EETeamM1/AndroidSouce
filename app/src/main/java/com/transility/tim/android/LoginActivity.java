package com.transility.tim.android;


import android.app.AlarmManager;
import android.app.PendingIntent;


import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.transility.tim.android.InventoryDatabase.EmployeeDatabaseTable;
import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.bean.EmployeeInfoBean;
import com.transility.tim.android.bean.Logon;

import com.transility.tim.android.http.RESTRequest.Method;
import com.transility.tim.android.http.RESTResponse;

import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.SessionTimeOutReciever;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    protected EditText username;
    protected EditText password;

    protected View progressView;
    protected TextView errorMessage;
    private WindowManager winManager;
    private RelativeLayout wrapperView;
    protected Button loginButton;
    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private TelephonyManager telephonyManager;
    private GoogleApiClient mGoogleApiClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        Utility.logError(LoginActivity.this.getClass().getSimpleName(), "onCreate");

        View activityView = attacheViewWithIdToWindow(R.layout.activity_login);

        restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, restResponseShowFeedbackInterface);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        loginButton = (Button) activityView.findViewById(R.id.login);
        errorMessage = (TextView) activityView.findViewById(R.id.error_message);
        password = (EditText) activityView.findViewById(R.id.password);
        username = (EditText) activityView.findViewById(R.id.username);
        progressView = activityView.findViewById(R.id.login_progress);

        loginButton.setOnClickListener(onClickListener);
        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(LoginActivity.this, true);
        intiateGooglePlayService();

    }


    private void intiateGooglePlayService() {



            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(onConnectionFailedListener)
                    .build();




    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        startLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, locationListener);
    }

    protected View attacheViewWithIdToWindow(int layoutId) {
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        winManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        this.winManager.addView(wrapperView, localLayoutParams);
        return View.inflate(this, layoutId, this.wrapperView);
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks=new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {

                startLocationUpdates();

            Utility.logError(LoginActivity.class.getSimpleName(),"onConnected");

        }

        @Override
        public void onConnectionSuspended(int i) {

            Utility.logError(LoginActivity.class.getSimpleName(),"onConnectionSuspended");
        }
    };

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener=new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            Utility.logError(LoginActivity.class.getSimpleName(),"onConnectionFailed");

        }
    };

    /**
     * Startrs the location updates
     */
    protected void startLocationUpdates() {
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(1800000)
                .setFastestInterval(1800000);
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, locationListener);

    }

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LoginActivity.this.location=location;

        }
    };
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login:
                    Utility.removeKeyboardfromScreen(v);

                    errorMessage.setText("");
                    //TODO Merge both missing username and password in common logic with showing error message.
                    if (TextUtils.isEmpty(username.getText())) {
                        username.setError(getString(R.string.textEmptyUserName));

                    } else if (TextUtils.isEmpty(password.getText())) {
                        password.setError(getString(R.string.textEmptyPassword));
                    } else if (authenticateMasterUser(password.getText().toString(), username.getText().toString())) {

                        EmployeeDatabaseTable employeeDatabaseTable = ((InventoryManagment) getApplication()).getInventoryDatabasemanager().getEmployeeDataTable();
                        EmployeeInfoBean employeeInfoBean = new EmployeeInfoBean();
                        employeeInfoBean.setUserName(username.getText().toString());
                        employeeInfoBean.setTimeOutPeriod(getResources().getInteger(R.integer.defaultSessionTimeOutPeriod));

                        employeeInfoBean.setSessionToken("");
                        employeeDatabaseTable.insertEmployeeInfoToEmployeeInfoTable(((InventoryManagment) getApplication()).getSqliteDatabase(), employeeInfoBean);

                        errorMessage.setText(getString(R.string.textWindowWarning));

                        Thread timerThread = new Thread() {
                            public void run() {
                                try {
                                    sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {

                                    intiateAlarm(LoginActivity.this.getResources().getInteger(R.integer.defaultSessionTimeOutPeriod));
                                    Utility.logError(LoginActivity.this.getClass().getSimpleName(), "Activity is about to get finished  ");
                                    finish();
                                }
                            }
                        };
                        timerThread.start();
                    } else if (Utility.checkInternetConnection(LoginActivity.this)) {
                        intiateLogin();
                    } else {
                        errorMessage.setText(getString(R.string.textNetworkNotAvaliable));
                    }
                    break;
            }
        }
    };

    /**
     * Function that fetch master password from local prefrences and authenticate the user.
     *
     * @param passwordStr
     * @param usernameStr
     * @return
     */
    protected boolean authenticateMasterUser(String passwordStr, String usernameStr) {
        return passwordStr.equals(TransiltiyInvntoryAppSharedPref.getMasterPasswordToSharedPref(this))
                && usernameStr.equals(TransiltiyInvntoryAppSharedPref.getUserNameToSharedPref(this));
    }

    /**
     * Intiate the login Request to server.
     */
    private void intiateLogin() {



        String json = Logon.writeLogonJSON(username.getText().toString(), password.getText().toString(), location, telephonyManager.getDeviceId());
        String loginRequest = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_login);

        restRequestFactoryWrapper.callHttpRestRequest(loginRequest, json, Method.POST);
        progressView.setVisibility(View.VISIBLE);
    }

    /**
     * Concrete Annotated implementation of the RestResponseShowFeedbackInterface.
     */
    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface = new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {
            String response = reposeJson.getText();
            Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(), "Request Code>>" + reposeJson.status.getCode() + " Resposne Message>>" + response);

            Logon logon = Logon.parseLogon(response);

            EmployeeDatabaseTable employeeDatabaseTable = ((InventoryManagment) getApplication()).getInventoryDatabasemanager().getEmployeeDataTable();
            EmployeeInfoBean employeeInfoBean = new EmployeeInfoBean();
            employeeInfoBean.setUserName(username.getText().toString());
            employeeInfoBean.setTimeOutPeriod(logon.getTimeout());

            employeeInfoBean.setSessionToken(logon.getSessionToken());

            TransiltiyInvntoryAppSharedPref.setMasterPasswordToSharedPref(LoginActivity.this, logon.getMasterPassword());

            employeeDatabaseTable.insertEmployeeInfoToEmployeeInfoTable(((InventoryManagment) getApplication()).getSqliteDatabase(), employeeInfoBean);
            intiateAlarm(logon.getTimeout());

        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {


        }

        @Override
        public void onSuccessInForeGroundOperation(RESTResponse restResponse) {
            progressView.setVisibility(View.GONE);

            password.setText("");
            username.setText("");
            finish();
        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {
            progressView.setVisibility(View.GONE);
            if (restResponse.status.isClientError()) {
                errorMessage.setText(getString(R.string.textUnauthorisedPerson));
            } else if (restResponse.status.isServerError()) {
                errorMessage.setText(getString(R.string.textServerisDown));
            } else {
                errorMessage.setText(getString(R.string.textSomeErrorOccured));
            }

            password.setText("");
            username.setText("");
        }


    };

    private void intiateAlarm(int timeOutPeriod) {

        AlarmManager alarmMgr = (AlarmManager) LoginActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(LoginActivity.this, SessionTimeOutReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(LoginActivity.this, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeOutPeriod * 60 * 1000)
                , timeOutPeriod * 60 * 1000, alarmIntent);
        Utility.logError(LoginActivity.this.getClass().getSimpleName(), "Alarm Time>>>>" + timeOutPeriod);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Utility.logError(LoginActivity.this.getClass().getSimpleName(), "onNewIntent");
        super.onNewIntent(intent);
    }

    /**
     * Overwited this method to disable the back button for this activity.
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * when on destroyed is called the current lock screen is removed from Device Window.
         */
        winManager.removeView(wrapperView);
        TransiltiyInvntoryAppSharedPref.setWasLoginScreenVisible(LoginActivity.this, false);


    }
}

