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
import android.widget.Toast;


import devicepolicymanager.MyDeviceAdminReciver;
import devicepolicymanager.SessionTimeOutReciever;

public class TransilityDeviceAdminActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;
    private Switch enableDeviceApp;
    private Button logoutBtn,reportsBtn;
    private boolean isMasterScreenCalled=false;
    protected static final int REQUEST_ENABLE = 1;
    private AlarmManager alarmManager;


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
        logoutBtn.setOnClickListener(onClickListener);
        reportsBtn.setOnClickListener(onClickListener);

        if (isMyDevicePolicyReceiverActive()) {
            enableDeviceApp.setChecked(true);
        } else {
            enableDeviceApp.setChecked(false);
        }

}


    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.logoutBtn:
                    Toast.makeText(TransilityDeviceAdminActivity.this,"Comming Soon",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.reportsBtn:
                    Toast.makeText(TransilityDeviceAdminActivity.this,"Comming Soon",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (isMasterScreenCalled){
            isMasterScreenCalled=false;
            return;
        }


        enableDeviceApp
                .setOnCheckedChangeListener(onCheckedChangeListener);
    }


private CompoundButton.OnCheckedChangeListener onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
        if (isChecked) {
            if (!isMyDevicePolicyReceiverActive()){
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

        } else {

            Intent masterPasswrodScreenIntent=new Intent(TransilityDeviceAdminActivity.this,MasterPasswordScreen.class);
            masterPasswrodScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            isMasterScreenCalled=true;
            startActivityForResult(masterPasswrodScreenIntent,MasterPasswordScreen.REQUESTCODE_FROMAPP);

        }
    }
};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:

                     Intent intent=new Intent(TransilityDeviceAdminActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MasterPasswordScreen.REQUESTCODE_FROMAPP:
                        truitonDevicePolicyManager.removeActiveAdmin(truitonDevicePolicyAdmin);
                    Intent alarmIntent = new Intent(TransilityDeviceAdminActivity.this, SessionTimeOutReciever.class);
                    PendingIntent alarmIntentPi = PendingIntent.getBroadcast(TransilityDeviceAdminActivity.this, 0, alarmIntent, 0);

                    alarmManager.cancel(alarmIntentPi);
                    enableDeviceApp.setOnCheckedChangeListener(null);
                        enableDeviceApp.setChecked(false);
                    enableDeviceApp.setOnCheckedChangeListener(onCheckedChangeListener);

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
