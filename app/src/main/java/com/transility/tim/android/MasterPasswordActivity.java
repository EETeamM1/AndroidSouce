package com.transility.tim.android;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.transility.tim.android.Utilities.TransiltiyInvntoryAppSharedPref;
import com.transility.tim.android.Utilities.Utility;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devicepolicymanager.MasterPasswordScreenLauncherBroadcast;
import devicepolicymanager.MyDeviceAdminReceiver;

public class MasterPasswordActivity extends Activity {

    public final static int REQUESTCODE_FROMAPP=501;
    protected Button masterPasswordEntredBtn,continueWithAdminPolicy;
    protected EditText passwordFieldEt;
    private ComponentName inventoDevicePolicyAdmin;
    protected static final int REQUEST_ENABLE = 1;

    private WindowManager winManager;
    private ViewGroup wrapperView;
    private TextView error_message;



    private final View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utility.removeKeyboardFromScreen(v);
        switch (v.getId()){
            case R.id.continueWithAdminPolicy:
                winManager.removeView(wrapperView);
                enableDeviceAdminApp();
                setResult(RESULT_CANCELED);
                break;
            case R.id.masterPasswordEnteredBtn:
                error_message.setText("");
                if (TextUtils.isEmpty(passwordFieldEt.getText())){
                    passwordFieldEt.setError(getString(R.string.textEmptyPassword));
                    return;
                }
                if (passwordFieldEt.getText().length()>15){
                    passwordFieldEt.setError(getString(R.string.textMaterPasswordExceedDigit));
                    return;
                }
                if (calculateMasterPassword().equals(passwordFieldEt.getText().toString())){
                 setResult(RESULT_OK);
                 finish();
                } else{
                    error_message.setText(getString(R.string.textMasterPasswordIncorrect));
                }
                break;
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       attacheViewWithIdToWindowAndInitializeViews();




    }

    @Override
    protected void onStart() {
        super.onStart();
        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(this, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(this, false);
    }

    /**
     * Attach the current device window with view of Master Password
     */
    protected View attacheViewWithIdToWindowAndInitializeViews() {

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        winManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        this.winManager.addView(wrapperView, localLayoutParams);
        View activityView= View.inflate(this, R.layout.layout_master_password_screen, wrapperView);
        masterPasswordEntredBtn = (Button) activityView.findViewById(R.id.masterPasswordEnteredBtn);
        continueWithAdminPolicy = (Button) activityView.findViewById(R.id.continueWithAdminPolicy);
        passwordFieldEt = (EditText) activityView.findViewById(R.id.passwordFieldEt);
        error_message = (TextView) activityView.findViewById(R.id.error_message);
        inventoDevicePolicyAdmin = new ComponentName(this,
                MyDeviceAdminReceiver.class);
        TextView deviceIdTv;
        deviceIdTv = (TextView) activityView.findViewById(R.id.deviceIdTv);
        String deviceId=getString(R.string.textDeviceId)+Utility.getDeviceId(this);

        deviceIdTv.setText(deviceId);
        masterPasswordEntredBtn.setOnClickListener(onClickListener);
        continueWithAdminPolicy.setOnClickListener(onClickListener);

        return activityView;

    }

    @Override
    public void onBackPressed() {}

    /**
     * Calculate the Master Password of the device.
     * @return deviceId of the device.
     */
    private String calculateMasterPassword(){
        return applyLamPortAlgorithmUsingDateOnImei(Utility.getDeviceId(MasterPasswordActivity.this), Calendar.getInstance());

    }

    protected String applyLamPortAlgorithmUsingDateOnImei(String imeiNumber, Calendar calendar){
        int dayOfMonth= calendar.get(Calendar.DAY_OF_MONTH);
        int monthNumber=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        long imeiNumberNumeric;
        if (!TextUtils.isDigitsOnly(imeiNumber)) {
            Pattern pattern=Pattern.compile("\\D");
            Matcher matcher=pattern.matcher(imeiNumber);
            String temp;

            while (matcher.find()) {
                temp=imeiNumber.replace(matcher.group(),((int)matcher.group().charAt(0))+"");
                imeiNumber=temp;
            }
        }

        if (imeiNumber.length()>15) {
            imeiNumber=imeiNumber.substring(0,15);
        }
        imeiNumberNumeric=Long.parseLong(imeiNumber);
        Utility.logError(MasterPasswordActivity.class.getSimpleName(),"Imei Number>>>"+imeiNumberNumeric+"");


        for (int i = 0; i < 5; i++) {
            long temp;
            temp = (imeiNumberNumeric - (imeiNumberNumeric / 2)) + dayOfMonth;
            temp = temp + (imeiNumberNumeric / 2) + monthNumber;
            temp = temp + year;
            imeiNumberNumeric = temp;
        }
        String masterPassword = imeiNumberNumeric + "";
        Utility.logError(MasterPasswordActivity.class.getSimpleName(), "Master Password>>>" + masterPassword + "");
        if (masterPassword.length() >=15) {
            return masterPassword.substring(0, 14).substring(6, 14);
        } else {
            return masterPassword;
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utility.logError("Master Password Activity",resultCode+"");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_ENABLE:

                    finish();
                    break;

            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {

                case REQUEST_ENABLE:
                   attacheViewWithIdToWindowAndInitializeViews();

                    break;

            }

        }


    }

    /**
     * Function calls the activity that initiates the enabling of the application as device admin app.
     */
    private void enableDeviceAdminApp(){
        startAlarmToLaunchMasterPasswordScreen();
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, inventoDevicePolicyAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");

        startActivityForResult(intent, REQUEST_ENABLE);
    }

    /**
     * Start the Alarm which Lunches master password screen  Master Password Screen .
     */
    private void startAlarmToLaunchMasterPasswordScreen() {
        Utility.cancelCurrentAlarmToLaunchTheMasterPasswordScreen(this);
        PendingIntent masterPasswordScreenPi=PendingIntent.getBroadcast(this,0,new Intent(this, MasterPasswordScreenLauncherBroadcast.class),PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ (this.getResources().getInteger(R.integer.defaultTimeOutPeriodForMasterPasswordInSeconds)*1000)
                , (this.getResources().getInteger(R.integer.defaultTimeOutPeriodForMasterPasswordInSeconds)*1000), masterPasswordScreenPi);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            winManager.removeView(wrapperView);
        }
        catch (RuntimeException w){
           Utility.printHandledException(w);
        }

    }
}
