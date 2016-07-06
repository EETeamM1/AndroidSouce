package com.transility.tim.android;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MasterPasswordScreen extends Activity {

    public final static int REQUESTCODE_FROMAPP=501;


    private Button masterpasswordEntredBtn,continueWithAdminPolicy;

    private ComponentName truitonDevicePolicyAdmin;
    protected static final int REQUEST_ENABLE = 1;

    private WindowManager winManager;
    private ViewGroup wrapperView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        View activityView=attacheViewWithIdToWindow(R.layout.layout_master_password_screen);
        masterpasswordEntredBtn= (Button) activityView.findViewById(R.id.masterpasswordEntredBtn);
        continueWithAdminPolicy= (Button) activityView.findViewById(R.id.continueWithAdminPolicy);
        masterpasswordEntredBtn.setOnClickListener(onClickListener);
        continueWithAdminPolicy.setOnClickListener(onClickListener);




    }

    private View attacheViewWithIdToWindow(int layoutId) {
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
                setResult(RESULT_OK);
                finish();
                break;
        }
        }
    };

    @Override
    public void onBackPressed() {

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

        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        winManager.removeView(wrapperView);
    }
}
