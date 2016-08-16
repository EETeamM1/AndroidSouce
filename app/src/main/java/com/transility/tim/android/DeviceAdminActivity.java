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

import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;

import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.MyDeviceAdminReceiver;

public class DeviceAdminActivity extends AppCompatActivity {

   private DevicePolicyManager inventoryDevicePolicyManager;
   private ComponentName inventoryDevicePolicyAdmin;

    private Switch enableDeviceApp;
    private Button logoutBtn, reportsBtn;
    private TextView messageLineTv;

    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private LocationSettingsRequest mLocationSettingsRequest;

    private static final int REQUEST_ENABLE = 1;

    private GoogleApiClient mGoogleApiClient;
    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
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
    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        }
    };
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
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
                            restRequestFactoryWrapper.callHttpRestRequest(loginRequest, null,json, RESTRequest.Method.POST);
                            Utility.appendLog("Logout API Request="+loginRequest+" json="+json+" Call Type="+RESTRequest.Method.POST);
                        }
                        clearPrefAndLogoutFromApp();
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
        Utility.clearPreviousSessionToken();
        Utility.cancelCurrentAlarmToLaunchTheLoginScreen(DeviceAdminActivity.this);
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
        TextView deviceIdTv= (TextView) findViewById(R.id.deviceIdTv);
        String deviceId=getString(R.string.textDeviceId)+Utility.getDeviceId(this);

        deviceIdTv.setText(deviceId);
        inventoryDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, null);
        inventoryDevicePolicyAdmin = new ComponentName(this, MyDeviceAdminReceiver.class);
        enableDeviceApp = (Switch) findViewById(R.id.enableDeviceApp);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        reportsBtn = (Button) findViewById(R.id.reportsBtn);

        messageLineTv = (TextView) findViewById(R.id.messageLineTv);
        logoutBtn.setOnClickListener(onClickListener);
        reportsBtn.setOnClickListener(onClickListener);



        initiateGooglePlayService();

    }

    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setInterval(30000).setFastestInterval(30000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
        mLocationSettingsRequest = builder.build();

    }


    private void initiateGooglePlayService() {

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

    private void checkLocationSettings() {
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


    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

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
                            SingleButtonAlertDialog  singleButtonAlertDialog = SingleButtonAlertDialog.newInstance(getString(R.string.textPleaseEnableNetwork));

                            singleButtonAlertDialog.show(getFragmentManager(), SingleButtonAlertDialog.class.getSimpleName());
                        }
                    }

                }
            } else {
                inventoryDevicePolicyManager.removeActiveAdmin(inventoryDevicePolicyAdmin);

            }
            enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    };




    /**
     * Function calls the activity that initiates the enabling of the applications as device admin app.
     */
    private void enableDeviceAdminApp() {
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                inventoryDevicePolicyAdmin);
        intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "");
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
     * Check whether device admin is active
     *
     * @return is Device Admin active
     */
    private boolean isMyDevicePolicyReceiverActive() {
        return inventoryDevicePolicyManager.isAdminActive(inventoryDevicePolicyAdmin);
    }





  private  final ResultCallback<LocationSettingsResult>  locationSettingsResultResultCallback=new ResultCallback<LocationSettingsResult>() {
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
                        int REQUEST_CHECK_SETTINGS=101;
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
