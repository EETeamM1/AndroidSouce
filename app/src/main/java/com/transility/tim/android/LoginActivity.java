package com.transility.tim.android;


import android.app.AlarmManager;
import android.app.PendingIntent;



import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.transility.tim.android.InventoryDatabase.EmployeeDatabaseTable;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.bean.EmployeeInfoBean;
import com.transility.tim.android.bean.Logon;

import com.transility.tim.android.http.RESTRequest.Method;
import com.transility.tim.android.http.RESTResponse;

import com.transility.tim.android.http.RestRequestFactoryWrapper;

import devicepolicymanager.SessionTimeOutReciever;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    private EditText mEmailView;
    private EditText mPasswordView;

    private View mProgressView;
    private View mLoginFormView;
    private TextView mResponseAndProgressMessageTv;
    private WindowManager winManager;
    private RelativeLayout wrapperView;
    private Button mEmailSignInButton,reportsBtn,logoutBtn;
    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the login form.
        Utility.logError(LoginActivity.this.getClass().getSimpleName(),"onCreate");

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));

        wrapperView = new RelativeLayout(this);
        wrapperView.setBackgroundColor(this.getResources().getColor(R.color.backWhite));
        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        View activityView= View.inflate(this, R.layout.activity_login, this.wrapperView);
//        setContentView(activityView);

        this.winManager.addView(wrapperView, localLayoutParams);
        restRequestFactoryWrapper=new RestRequestFactoryWrapper(this,restResponseShowFeedbackInterface);

        mEmailSignInButton = (Button) activityView.findViewById(R.id.email_sign_in_button);
        mResponseAndProgressMessageTv= (TextView) activityView.findViewById(R.id.responseAndProgressMessageTv);
        mEmailSignInButton.setOnClickListener(onClickListener);
        mPasswordView = (EditText)activityView.findViewById(R.id.password);

        mEmailView = (EditText) activityView.findViewById(R.id.email);
        mLoginFormView = activityView.findViewById(R.id.login_form);
        mProgressView = activityView.findViewById(R.id.login_progress);
        mResponseAndProgressMessageTv= (TextView) activityView.findViewById(R.id.responseAndProgressMessageTv);


    }


    private OnClickListener onClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.email_sign_in_button:
                    Utility.removeKeyboardfromScreen(LoginActivity.this);

                    mResponseAndProgressMessageTv.setText("");
                    boolean isNetworkConnected=Utility.checkInternetConnection(LoginActivity.this);
                    if(isNetworkConnected){

                        if (TextUtils.isEmpty(mEmailView.getText())){
                            mEmailView.setError(getString(R.string.textEmptyUserName));
                        }
                        else if (TextUtils.isEmpty(mPasswordView.getText())){
                            mPasswordView.setError(getString(R.string.textEmptyPassword));
                        }
                        if (isNetworkConnected){
                            intiateLogin();
                        }
                        else {
                            mResponseAndProgressMessageTv.setText(getString(R.string.textNetworkNotAvaliable));
                        }
                    }
                    else{

                        if (TextUtils.isEmpty(mPasswordView.getText())){
                            mResponseAndProgressMessageTv.setText(getString(R.string.textUseMasterPassword));

                        }
                        else{
                            if(authenticateUserThroughMasterPassword(mPasswordView.getText().toString())){
                                mResponseAndProgressMessageTv.setText(getString(R.string.textWindowWarning));
                                Thread timerThread = new Thread(){
                                    public void run(){
                                        try{
                                            sleep(5000);
                                        }catch(InterruptedException e){
                                            e.printStackTrace();
                                        }finally{

                                            intiaTeAlarm(LoginActivity.this.getResources().getInteger(R.integer.defaultTimeInterval));

                                            Utility.logError(LoginActivity.this.getClass().getSimpleName(),"Activity is about to get finished  ");

                                            finish();
                                        }
                                    }
                                };
                                timerThread.start();

                            }
                            else {
                                mResponseAndProgressMessageTv.setText(getString(R.string.textIncorrectMasterPassword));
                            }
                        }

                    }

                break;

            }
        }
    };

    /**
     * Function that fetch master password from data base and authenticate the user.
     * @param password
     * @return
     */
    private boolean authenticateUserThroughMasterPassword(String password){
        boolean isuserValid=false;


        EmployeeDatabaseTable  employeeDatabaseTable=((InventoryManagment) getApplication()).getInventoryDatabasemanager().getEmployeeDataTable();

        EmployeeInfoBean  employeeInfoBean=employeeDatabaseTable.getTheInfoOfCurrentEmployee(((InventoryManagment) getApplication()).getSqliteDatabase());
        if (password.equals(employeeInfoBean.getMasterPassword())){
            isuserValid=true;
        }

        return  isuserValid;
    }
    /**
     * Intiate the login Request to server.
     *
     */
    private void intiateLogin(){

        String json=Logon.writeLogonJSON(mEmailView.getText().toString(),mPasswordView.getText().toString(),null,telephonyManager.getDeviceId());
        String loginRequest=getResources().getString(R.string.baseUrl)+getResources().getString(R.string.api_login);

        restRequestFactoryWrapper.callHttpRestRequest(loginRequest,json, Method.POST);
        mProgressView.setVisibility(View.VISIBLE);

    }
    /**
     * Concrete Annotated implementation of the RestResponseShowFeedbackInterface.
     */
    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface=new RestResponseShowFeedbackInterface() {
        @Override
        public void onSucces(RESTResponse reposeJson) {

            mProgressView.setVisibility(View.GONE);
            String response=reposeJson.getText();

           if(((InventoryManagment)getApplication()).getInventoryDatabasemanager().getEmployeeDataTable()
                    .deleteEmployeeInfoFromDatabase(((InventoryManagment)getApplication()).getSqliteDatabase())){
               Logon logon=Logon.parseLogon(response);


               EmployeeDatabaseTable employeeDatabaseTable=((InventoryManagment)getApplication()).getInventoryDatabasemanager().getEmployeeDataTable();
               EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
               employeeInfoBean.setUserEmail(mEmailView.getText().toString());
               employeeInfoBean.setTimeOutPeriod(logon.getTimeout());
               employeeInfoBean.setMasterPassword(logon.getMasterPassword());



               employeeDatabaseTable.insertEmployeeInfoToEmployeeInfoTable(((InventoryManagment)getApplication()).getSqliteDatabase(),employeeInfoBean);
               intiaTeAlarm(logon.getTimeout());

               finish();

           }
            else {
               mResponseAndProgressMessageTv.setText(getString(R.string.textDataBaseErrorOccured));
           }



        }


        @Override
        public void onError(RESTResponse reposeJson) {


            mProgressView.setVisibility(View.GONE);
             if(reposeJson.status.isClientError()){
                 mResponseAndProgressMessageTv.setText(getString(R.string.textUnauthorisedPerson));

             }
            else  if (reposeJson.status.isServerError()){
                 mResponseAndProgressMessageTv.setText(getString(R.string.textServerisDown));
             }
            else {
                mResponseAndProgressMessageTv.setText(getString(R.string.textSomeErrorOccured));
             }


        }
    };
    private void intiaTeAlarm(int timeOutPeriod){


        AlarmManager  alarmMgr = (AlarmManager)LoginActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(LoginActivity.this, SessionTimeOutReciever.class);
        PendingIntent  alarmIntent = PendingIntent.getBroadcast(LoginActivity.this, 0, intent, 0);

        alarmMgr.cancel(alarmIntent);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+timeOutPeriod*60*1000
                , timeOutPeriod*60*1000, alarmIntent);
        Utility.logError(LoginActivity.this.getClass().getSimpleName(),"Alarm Time>>>>"+timeOutPeriod);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        Utility.logError(LoginActivity.this.getClass().getSimpleName(),"onNewIntent");
        super.onNewIntent(intent);
    }

    /**
     * Overwited this method to disable the back button for this activity.
     */
    @Override
    public void onBackPressed() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * when on destroyed is called the current lock screen is removed from Device Window.
         */
        winManager.removeView(wrapperView);
    }




}

