package com.transility.tim.android;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.CompoundButton;
import android.widget.Switch;


import devicepolicymanager.MyDeviceAdminReciver;

public class TransilityDeviceLoginActivity extends AppCompatActivity {
    private final static String LOG_TAG = "DevicePolicyAdmin";
    DevicePolicyManager truitonDevicePolicyManager;
    ComponentName truitonDevicePolicyAdmin;
    private Switch enableDeviceApp;
    protected static final int REQUEST_ENABLE = 1;
    protected static final int SET_PASSWORD = 2;
    private AlarmManager alarmMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_app_home_page);


    truitonDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    truitonDevicePolicyAdmin = new ComponentName(this,
                                                 MyDeviceAdminReciver.class);

        enableDeviceApp = (Switch) findViewById(R.id.enableDeviceApp);
        enableTheAlarm();
}

    private void enableTheAlarm() {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(TransilityDeviceLoginActivity.this,MyDeviceAdminReciver.class);
        intent.setAction(MyDeviceAdminReciver.MAHEVENT);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                2 * 60 * 1000,
                2 * 60 * 1000, PendingIntent.getBroadcast(TransilityDeviceLoginActivity.this,0,intent,0));
        System.out.println("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyDevicePolicyReceiverActive()) {
            enableDeviceApp.setChecked(true);
        } else {
            enableDeviceApp.setChecked(false);
        }
        enableDeviceApp
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            Intent intent = new Intent(
                                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(
                                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                    truitonDevicePolicyAdmin);
                            intent.putExtra(
                                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "Tere mere beech mai kaisa hai bandhan anjana!!!!!!!");
                            startActivityForResult(intent, REQUEST_ENABLE);
                        } else {
                            truitonDevicePolicyManager
                                    .removeActiveAdmin(truitonDevicePolicyAdmin);
                        }
                    }
                });
    }


    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:

                    truitonDevicePolicyManager.setMaximumTimeToLock(
                            truitonDevicePolicyAdmin, 30000L);
                    truitonDevicePolicyManager.setMaximumFailedPasswordsForWipe(
                            truitonDevicePolicyAdmin, 5);
                    truitonDevicePolicyManager.setPasswordQuality(
                            truitonDevicePolicyAdmin,
                            DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);


                    Intent intent=new Intent(TransilityDeviceLoginActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

            }
        }
    }

    private boolean isMyDevicePolicyReceiverActive() {
        return truitonDevicePolicyManager
                .isAdminActive(truitonDevicePolicyAdmin);
    }
}
