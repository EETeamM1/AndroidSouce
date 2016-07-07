package com.transility.tim.android;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.transility.tim.android.Dialogs.SingleButtonAlertDialog;
import com.transility.tim.android.InventoryDatabase.EmployeeDatabaseTable;
import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Logon;
import com.transility.tim.android.bean.Logout;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.MyDeviceAdminReciver;
import devicepolicymanager.SessionTimeOutReciever;

public class TransilityDeviceAdminActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;

    private Switch enableDeviceApp;
    private Button logoutBtn, reportsBtn;

    protected static final int REQUEST_ENABLE = 1;

    private TextView messageLineTv;
    private SingleButtonAlertDialog singleButtonAlertDialog;
    private RestRequestFactoryWrapper restRequestFactoryWrapper;

    private InventoryManagment inventoryManagment;

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
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logoutBtn:
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

            Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
            Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TransilityDeviceAdminActivity.this.startActivity(intent1);

            finish();
        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {

            Utility.cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
            Intent intent1 = new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TransilityDeviceAdminActivity.this.startActivity(intent1);

            finish();
        }
    };
}
