package com.transility.tim.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transility_application_splash_screen);



        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent=new Intent(SplashActivity.this,TransilityDeviceAdminActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();

    }
}
