package com.transility.tim.android;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.transility.tim.android.Utilities.Utility;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devicepolicymanager.MyDeviceAdminReciver;

public class MasterPasswordScreen extends Activity {

    public final static int REQUESTCODE_FROMAPP=501;

    protected Button masterpasswordEntredBtn,continueWithAdminPolicy;
    protected EditText passwordFieldEt;
    private ComponentName truitonDevicePolicyAdmin;
    protected static final int REQUEST_ENABLE = 1;

    private WindowManager winManager;
    private ViewGroup wrapperView;
    private TextView error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View activityView=attacheViewWithIdToWindow(R.layout.layout_master_password_screen);
        masterpasswordEntredBtn= (Button) activityView.findViewById(R.id.masterpasswordEntredBtn);
        continueWithAdminPolicy= (Button) activityView.findViewById(R.id.continueWithAdminPolicy);
        passwordFieldEt= (EditText) activityView.findViewById(R.id.passwordFieldEt);
        error_message= (TextView) activityView.findViewById(R.id.error_message);
        truitonDevicePolicyAdmin = new ComponentName(this,
                MyDeviceAdminReciver.class);
        masterpasswordEntredBtn.setOnClickListener(onClickListener);
        continueWithAdminPolicy.setOnClickListener(onClickListener);
    }

    protected View attacheViewWithIdToWindow(int layoutId) {
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        winManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        this.winManager.addView(wrapperView, localLayoutParams);
        return View.inflate(this, layoutId, wrapperView);
    }

    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.continueWithAdminPolicy:
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
                if (calclualteMasterPassword().equals(passwordFieldEt.getText())){
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
    public void onBackPressed() {

    }

    private String calclualteMasterPassword(){
        String masterPasswordString=null;
        masterPasswordString=  applyLamPortAlgoRithmUsingDateOnImei(Utility.getDeviceId(this));
        return masterPasswordString;
    }

    protected String applyLamPortAlgoRithmUsingDateOnImei(String imeiNumber){

        Calendar calendar=Calendar.getInstance();
        int dayOfMonth= calendar.get(Calendar.DAY_OF_MONTH);
        int monthNumber=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        long imeiNumberNumeric=0;

        if (!TextUtils.isDigitsOnly(imeiNumber)){
            Pattern pattern=Pattern.compile("\\D");
            Matcher matcher=pattern.matcher(imeiNumber);
            String temp;

            while (matcher.find()){
                temp=imeiNumber.replace(matcher.group(),((int)matcher.group().charAt(0))+"");
                imeiNumber=temp;
            }
        }

        if (imeiNumber.length()>15){
            imeiNumber=imeiNumber.substring(0,15);
        }
        imeiNumberNumeric=Long.parseLong(imeiNumber);
        Utility.logError(MasterPasswordScreen.class.getSimpleName(),"Imei Number>>>"+imeiNumberNumeric+"");

        for (int i=0;i<5;i++){
            long temp;
            temp=(imeiNumberNumeric-(imeiNumberNumeric/2))+dayOfMonth;
            temp=temp+(imeiNumberNumeric/2)+monthNumber;
            temp=temp+year;
            imeiNumberNumeric=temp;
        }
        String masterPassword=imeiNumberNumeric+"";
        Utility.logError(MasterPasswordScreen.class.getSimpleName(),"Master Password>>>"+masterPassword+"");
        if (masterPassword.length()>15){
            return masterPassword.substring(0,15);
        }
        else{
            return masterPassword;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_ENABLE:
                    finish();
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode){
                case REQUEST_ENABLE:
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        winManager.removeView(wrapperView);
    }
}
