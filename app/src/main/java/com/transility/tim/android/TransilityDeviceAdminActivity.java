package com.transility.tim.android;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.transility.tim.android.Dialogs.SingleButtonAlertDialog;
import com.transility.tim.android.Utilities.Utility;

import devicepolicymanager.MyDeviceAdminReciver;
import devicepolicymanager.SessionTimeOutReciever;

public class TransilityDeviceAdminActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;
    private Switch enableDeviceApp;
    private Button logoutBtn,reportsBtn;
//    private boolean isMasterScreenCalled=false;
    protected static final int REQUEST_ENABLE = 1;
    private AlarmManager alarmManager;
    private TextView messageLineTv;
    private SingleButtonAlertDialog singleButtonAlertDialog;

    private InventoryManagment inventoryManagment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_app_home_page);


        truitonDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        truitonDevicePolicyAdmin = new ComponentName(this,
                                                 MyDeviceAdminReciver.class);
        enableDeviceApp = (Switch) findViewById(R.id.enableDeviceApp);
        logoutBtn= (Button) findViewById(R.id.logoutBtn);
        reportsBtn= (Button)findViewById(R.id.reportsBtn);
        messageLineTv= (TextView) findViewById(R.id.messageLineTv);
        logoutBtn.setOnClickListener(onClickListener);
        reportsBtn.setOnClickListener(onClickListener);


}


    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.logoutBtn:
                    cancelCurrentPendingIntent(TransilityDeviceAdminActivity.this);
                    Intent intent1=new Intent(TransilityDeviceAdminActivity.this, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TransilityDeviceAdminActivity.this.startActivity(intent1);

                    finish();
                    break;
                case R.id.reportsBtn:

                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        inventoryManagment  =((InventoryManagment)TransilityDeviceAdminActivity.this.getApplication());
        int rowCount=inventoryManagment.getInventoryDatabasemanager().getEmployeeDataTable().getEmployeeTableRowCount(inventoryManagment.getSqliteDatabase());

        if (rowCount!=0){

            logoutBtn.setVisibility(View.VISIBLE);
            reportsBtn.setVisibility(View.VISIBLE);



        }
        else{
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


private CompoundButton.OnCheckedChangeListener onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
        if (isChecked) {
            enableDeviceApp.setOnCheckedChangeListener(null);
            if (!isMyDevicePolicyReceiverActive()){


                int rowCount=inventoryManagment.getInventoryDatabasemanager().getEmployeeDataTable().getEmployeeTableRowCount(inventoryManagment.getSqliteDatabase());
                if (Utility.checkInternetConnection(TransilityDeviceAdminActivity.this)){
                    enableDeviceAdminApp();
                }
                else {

                    if (rowCount!=0){
                        enableDeviceAdminApp();
                    }
                    else
                    {
                        enableDeviceApp.setChecked(false);
                        singleButtonAlertDialog=SingleButtonAlertDialog.newInstance(getString(R.string.textPleaseEnableNetwork));

                        singleButtonAlertDialog.show(getFragmentManager(),SingleButtonAlertDialog.class.getSimpleName());

                    }
                }


            }

        } else {


            truitonDevicePolicyManager.removeActiveAdmin(truitonDevicePolicyAdmin);


        }

        enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);
    }
};

    private void cancelCurrentPendingIntent(Context context){
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionTimeOutReciever.class);
        PendingIntent  alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);

    }


    /**
     * Function calls the activity that initates the enabling of the apllication as device admin app.
     */
    private void enableDeviceAdminApp(){
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
        }
        else if (resultCode==RESULT_CANCELED){
            switch (requestCode){

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
     * @return is Device Admin active
     */
    private boolean isMyDevicePolicyReceiverActive() {
        return truitonDevicePolicyManager
                .isAdminActive(truitonDevicePolicyAdmin);
    }
}
