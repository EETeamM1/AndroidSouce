package com.transility.tim.android;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
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

import devicepolicymanager.MyDeviceAdminReciver;

public class MasterPasswordActivity extends Activity {

    public final static int REQUESTCODE_FROMAPP=501;
    protected Button masterpasswordEntredBtn,continueWithAdminPolicy;
    protected EditText passwordFieldEt;
    private ComponentName truitonDevicePolicyAdmin;
    protected static final int REQUEST_ENABLE = 1;

    private WindowManager winManager;
    private ViewGroup wrapperView;
    private TextView error_message;
    private TextView deviceIdTv;


    private final View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utility.removeKeyboardfromScreen(v);
        switch (v.getId()){
            case R.id.continueWithAdminPolicy:
                winManager.removeView(wrapperView);
                enableDeviceAdminApp();
                setResult(RESULT_CANCELED);
                break;
            case R.id.masterpasswordEntredBtn:
                error_message.setText("");
                if (TextUtils.isEmpty(passwordFieldEt.getText())){
                    passwordFieldEt.setError(getString(R.string.textEmptyPassword));
                    return;
                }
                if (passwordFieldEt.getText().length()>15){
                    passwordFieldEt.setError(getString(R.string.textMaterPasswordExceedDigit));
                    return;
                }
                if (calclualteMasterPassword().equals(passwordFieldEt.getText().toString())){
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

       attacheViewWithIdToWindowAndIntialiseViews();

        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(this, true);


    }

    /**
     * Attach the current device window with view of Master Password
     */
    protected View attacheViewWithIdToWindowAndIntialiseViews() {

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        winManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        this.winManager.addView(wrapperView, localLayoutParams);
        View activityView= View.inflate(this, R.layout.layout_master_password_screen, wrapperView);
        masterpasswordEntredBtn = (Button) activityView.findViewById(R.id.masterpasswordEntredBtn);
        continueWithAdminPolicy = (Button) activityView.findViewById(R.id.continueWithAdminPolicy);
        passwordFieldEt = (EditText) activityView.findViewById(R.id.passwordFieldEt);
        error_message = (TextView) activityView.findViewById(R.id.error_message);
        truitonDevicePolicyAdmin = new ComponentName(this,
                MyDeviceAdminReciver.class);
        deviceIdTv = (TextView) activityView.findViewById(R.id.deviceIdTv);
        deviceIdTv.setText(getString(R.string.textDeviceId) + Utility.getDeviceId(this));
        masterpasswordEntredBtn.setOnClickListener(onClickListener);
        continueWithAdminPolicy.setOnClickListener(onClickListener);

        return activityView;

    }

    @Override
    public void onBackPressed() {}

    private String calclualteMasterPassword(){
        String masterPasswordString=  applyLamPortAlgoRithmUsingDateOnImei(Utility.getDeviceId(MasterPasswordActivity.this), Calendar.getInstance());
        return masterPasswordString;
    }

    protected String applyLamPortAlgoRithmUsingDateOnImei(String imeiNumber, Calendar calendar){
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_ENABLE:

                    finish();
                    break;

            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {

                case REQUEST_ENABLE:
                   attacheViewWithIdToWindowAndIntialiseViews();

                    break;

            }

        }


    }

    /**
     * Function calls the activity that initates the enabling of the apllication as device admin app.
     */
    private void enableDeviceAdminApp(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, truitonDevicePolicyAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
        startActivityForResult(intent, REQUEST_ENABLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TransiltiyInvntoryAppSharedPref.setIsMasterPasswordScreenVisible(this, false);
        try{
            winManager.removeView(wrapperView);
        }
        catch (RuntimeException w){
        w.printStackTrace();
        }

    }
}
