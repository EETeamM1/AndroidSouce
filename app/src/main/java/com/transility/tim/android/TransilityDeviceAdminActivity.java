package com.transility.tim.android;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.MyDeviceAdminReciver;

public class TransilityDeviceAdminActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;

    private Switch enableDeviceApp;
    private Button logoutBtn, reportsBtn;
    private TextView messageLineTv;

    protected static final int REQUEST_ENABLE = 1;

    private SingleButtonAlertDialog singleButtonAlertDialog;
    private RestRequestFactoryWrapper restRequestFactoryWrapper;

    private InventoryManagment inventoryManagment;
    private GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;

    private final int REQUEST_CHECK_SETTINGS=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_app_home_page);

        truitonDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        restRequestFactoryWrapper = new RestRequestFactoryWrapper(this, restResponseShowFeedbackInterface);
        truitonDevicePolicyAdmin = new ComponentName(this,
                MyDeviceAdminReciver.class);
        enableDeviceApp = (Switch) findViewById(R.id.enableDeviceApp);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        reportsBtn = (Button) findViewById(R.id.reportsBtn);
        messageLineTv = (TextView) findViewById(R.id.messageLineTv);
        logoutBtn.setOnClickListener(onClickListener);
        reportsBtn.setOnClickListener(onClickListener);



            intiateGooglePlayService();
            createLocationSettingsRequest();

            checkLocationSettings();


    }

    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setInterval(30000).setFastestInterval(30000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
        mLocationSettingsRequest = builder.build();

    }


    private void intiateGooglePlayService() {

        if (Utility.checkGooglePlayServicesAvailable(this)){


            mGoogleApiClient = new GoogleApiClient.Builder(TransilityDeviceAdminActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(onConnectionFailedListener)
                    .build();


        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null)
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null)
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
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks=new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {


        }

        @Override
        public void onConnectionSuspended(int i) {


        }
    };

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener=new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {



        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutBtn:
                    if (!Utility.checkInternetConnection(TransilityDeviceAdminActivity.this)){
                        cleanTheDatabase();
                        Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
                        Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        TransilityDeviceAdminActivity.this.startActivity(intent1);

                        finish();
                    }
                    else{
                        InventoryDatabaseManager inventoryDatabaseManager = ((InventoryManagment) TransilityDeviceAdminActivity.this.getApplication()).getInventoryDatabasemanager();
                        String sessionToken = inventoryDatabaseManager.getEmployeeDataTable().
                                getSessionToken(((InventoryManagment) TransilityDeviceAdminActivity.this.getApplication()).getSqliteDatabase());
                        if (!TextUtils.isEmpty(sessionToken)){
                            String json = Logout.writeLogoutJson(sessionToken);
                            String loginRequest = getResources().getString(R.string.baseUrl) + getResources().getString(R.string.api_logout);
                            restRequestFactoryWrapper.callHttpRestRequest(loginRequest, json, RESTRequest.Method.POST);
                        }
                        else {
                            cleanTheDatabase();
                            Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
                            Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            TransilityDeviceAdminActivity.this.startActivity(intent1);

                            finish();

                        }
                    }


                    break;
                case R.id.reportsBtn:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        inventoryManagment = ((InventoryManagment) TransilityDeviceAdminActivity.this.getApplication());
        int rowCount = inventoryManagment.getInventoryDatabasemanager().getEmployeeDataTable().getEmployeeTableRowCount(inventoryManagment.getSqliteDatabase());

        if (rowCount != 0) {
            logoutBtn.setVisibility(View.VISIBLE);
            reportsBtn.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.GONE);
            reportsBtn.setVisibility(View.GONE);
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


                    int rowCount = inventoryManagment.getInventoryDatabasemanager().getEmployeeDataTable().getEmployeeTableRowCount(inventoryManagment.getSqliteDatabase());
                    if (Utility.checkInternetConnection(TransilityDeviceAdminActivity.this)) {
                        enableDeviceAdminApp();
                    } else {

                        if (rowCount != 0) {
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
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                truitonDevicePolicyAdmin);
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

                case MasterPasswordScreen.REQUESTCODE_FROMAPP:
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

                case MasterPasswordScreen.REQUESTCODE_FROMAPP:
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
        return truitonDevicePolicyManager
                .isAdminActive(truitonDevicePolicyAdmin);
    }

    /**
     * Method call to clean the database
     */
    private void cleanTheDatabase(){
        InventoryDatabaseManager  inventoryDatabaseManager=((InventoryManagment)TransilityDeviceAdminActivity.this.getApplication()).getInventoryDatabasemanager();
        inventoryDatabaseManager.getEmployeeDataTable().deleteEmployeeInfoFromDatabase(((InventoryManagment)TransilityDeviceAdminActivity.this.getApplication()).getSqliteDatabase());
    }

    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface = new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {
            Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            cleanTheDatabase();
        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {
            Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(),"Request Code>>"+reposeJson.status.getCode()+" Resposne Message>>"+reposeJson.getText());
            cleanTheDatabase();

        }

        @Override
        public void onSuccessInForeGroundOperation(RESTResponse restResponse) {
            Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(),"Request Code>>"+restResponse.status.getCode()+" Resposne Message>>"+restResponse.getText());
            Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
            Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TransilityDeviceAdminActivity.this.startActivity(intent1);

            finish();
        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {
            Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(),"Request Code>>"+restResponse.status.getCode()+" Resposne Message>>"+restResponse.getText());
            Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
            Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TransilityDeviceAdminActivity.this.startActivity(intent1);

            finish();
        }
    };



    ResultCallback<LocationSettingsResult>  locationSettingsResultResultCallback=new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(), "All location settings are satisfied.");

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(), "Location settings are not satisfied. Show the user a dialog to" +
                            "upgrade location settings ");

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(TransilityDeviceAdminActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(), "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Utility.logError(TransilityDeviceAdminActivity.class.getSimpleName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                            "not created.");
                    break;
            }
        }
    };



}
