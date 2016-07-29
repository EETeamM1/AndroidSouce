package com.transility.tim.android;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.transility.tim.android.Dialogs.SingleButtonAlertDialog;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.MyDeviceAdminReciver;

public class DeviceAdminActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;

    protected Switch enableDeviceApp;
    protected Button logoutBtn, reportsBtn;
    private TextView messageLineTv;
    private SingleButtonAlertDialog singleButtonAlertDialog;
    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private final int REQUEST_CHECK_SETTINGS=101;
    protected static final int REQUEST_ENABLE = 1;

    private GoogleApiClient mGoogleApiClient;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            createLocationSettingsRequest();
            if (mGoogleApiClient != null) {
                checkLocationSettings();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {


        }
    };
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutBtn:
                    if (!Utility.checkInternetConnection(DeviceAdminActivity.this)){
                        clearPrefAndLogoutFromApp();
                    }
                    else{
                        String sessionToken = TransiltiyInvntoryAppSharedPref.getSessionToken(DeviceAdminActivity.this);
                        if (!TextUtils.isEmpty(sessionToken)){
                            String json = Logout.writeLogoutJson(sessionToken);
                            String loginRequest = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_logout);
                            restRequestFactoryWrapper.callHttpRestRequest(loginRequest, json, RESTRequest.Method.POST);
                            Utility.appendLog("Logout API Request="+loginRequest+" json="+json+" Call Type="+RESTRequest.Method.POST);
                        }
                        else {
                            clearPrefAndLogoutFromApp();
                        }
                    }
                    break;

                case R.id.reportsBtn:
                    Intent reportsIntent=new Intent(DeviceAdminActivity.this,ReportsActivity.class);
                    startActivity(reportsIntent);

                    break;
            }
        }
    };


    /**
     * Clear the pref and logout from device.
     */
    private void clearPrefAndLogoutFromApp(){
        Utility.clearPrefrences();
        Utility.cancelCurrentPendingIntent(DeviceAdminActivity.this);
        Intent intent1 = new Intent(DeviceAdminActivity.this, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DeviceAdminActivity.this.startActivity(intent1);
        Utility.appendLog("Offline Logout");
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_app_home_page);

        truitonDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, restResponseShowFeedbackInterface);
        truitonDevicePolicyAdmin = new ComponentName(this, MyDeviceAdminReciver.class);
        enableDeviceApp = (Switch) findViewById(R.id.enableDeviceApp);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        reportsBtn = (Button) findViewById(R.id.reportsBtn);

        messageLineTv = (TextView) findViewById(R.id.messageLineTv);
        logoutBtn.setOnClickListener(onClickListener);
        reportsBtn.setOnClickListener(onClickListener);



        intiateGooglePlayService();

    }

    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setInterval(30000).setFastestInterval(30000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
        mLocationSettingsRequest = builder.build();

    }


    private void intiateGooglePlayService() {

        if (Utility.checkGooglePlayServicesAvailable(this)) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(onConnectionFailedListener)
                    .build();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(locationSettingsResultResultCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getSessionToken(this))) {
            logoutBtn.setVisibility(View.GONE);
            reportsBtn.setVisibility(View.GONE);
        } else {
            logoutBtn.setVisibility(View.VISIBLE);
            reportsBtn.setVisibility(View.VISIBLE);
        }

        if (isMyDevicePolicyReceiverActive()) {
            enableDeviceApp.setChecked(true);
            messageLineTv.setText(getString(R.string.textAdminAppWhenEnabled));

        } else {
            enableDeviceApp.setChecked(false);
            messageLineTv.setText(getString(R.string.textAdminAppTextWhenDisabled));

        }

        enableDeviceApp
                .setOnCheckedChangeListener(onCheckedChangeListener);
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                enableDeviceApp.setOnCheckedChangeListener(null);
                if (!isMyDevicePolicyReceiverActive()) {

                    if (Utility.checkInternetConnection(DeviceAdminActivity.this)) {
                        enableDeviceAdminApp();
                    } else {

                        if (! TextUtils.isEmpty(TransiltiyInvntoryAppSharedPref.getSessionToken(DeviceAdminActivity.this))) {
                            enableDeviceAdminApp();
                        } else {
                            enableDeviceApp.setChecked(false);
                            singleButtonAlertDialog = SingleButtonAlertDialog.newInstance(getString(R.string.textPleaseEnableNetwork));

                            singleButtonAlertDialog.show(getFragmentManager(), SingleButtonAlertDialog.class.getSimpleName());
                        }
                    }

                }
            } else {
                truitonDevicePolicyManager.removeActiveAdmin(truitonDevicePolicyAdmin);

            }
            enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    };




    /**
     * Function calls the activity that initates the enabling of the apllication as device admin app.
     */
    private void enableDeviceAdminApp() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, truitonDevicePolicyAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
        startActivityForResult(intent, REQUEST_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case MasterPasswordActivity.REQUESTCODE_FROMAPP:
                    enableDeviceApp.setOnCheckedChangeListener(null);
                    enableDeviceApp.setChecked(false);
                    enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);


                    break;

                case REQUEST_ENABLE:

                    finish();
                    break;

            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {

                case MasterPasswordActivity.REQUESTCODE_FROMAPP:
                    enableDeviceApp.setOnCheckedChangeListener(null);
                    enableDeviceApp.setChecked(true);
                    enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);
                    break;
                case REQUEST_ENABLE:
                    enableDeviceApp.setOnCheckedChangeListener(null);
                    enableDeviceApp.setChecked(false);
                    enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);
                    break;

            }

        }


    }

    /**
     * Check whther device admin is active
     *
     * @return is Device Admin active
     */
    private boolean isMyDevicePolicyReceiverActive() {
        return truitonDevicePolicyManager.isAdminActive(truitonDevicePolicyAdmin);
    }

    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface = new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {
            Utility.appendLog("Response Logout API="+reposeJson.getText());
            Utility.logError(DeviceAdminActivity.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            Utility.clearPrefrences();
        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {
            Utility.appendLog("Response Logout API="+reposeJson.getText());
            Utility.logError(DeviceAdminActivity.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            Utility.clearPrefrences();

        }

        @Override
        public void onSuccessInForeGroundOperation(RESTResponse restResponse) {



            Utility.cancelCurrentPendingIntent(DeviceAdminActivity.this);
            Intent intent1 = new Intent(DeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DeviceAdminActivity.this.startActivity(intent1);

            finish();
        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {


            Utility.cancelCurrentPendingIntent(DeviceAdminActivity.this);
            Intent intent1 = new Intent(DeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DeviceAdminActivity.this.startActivity(intent1);

            finish();
        }
    };



    ResultCallback<LocationSettingsResult>  locationSettingsResultResultCallback=new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Utility.logError(DeviceAdminActivity.class.getSimpleName(), "All location settings are satisfied.");

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Utility.logError(DeviceAdminActivity.class.getSimpleName(), "Location settings are not satisfied. Show the user a dialog to" +
                            "upgrade location settings ");

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(DeviceAdminActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Utility.logError(DeviceAdminActivity.class.getSimpleName(), "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Utility.logError(DeviceAdminActivity.class.getSimpleName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                            "not created.");
                    break;
            }
        }
    };



}
