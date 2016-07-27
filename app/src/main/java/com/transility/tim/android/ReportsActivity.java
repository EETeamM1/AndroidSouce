package com.transility.tim.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Report;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RESTResponseHandler;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

public class ReportsActivity extends AppCompatActivity {

    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private Report report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_reports_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reportScreenTb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.textReportScreen);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorMaron));
        restRequestFactoryWrapper=new RestRequestFactoryWrapper(this,restResponseShowFeedbackInterface);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callGetRequestAndFetchReportsOfDevice();
    }


    /**
     * Api called to call reports API and show on device.
     */
    private void callGetRequestAndFetchReportsOfDevice(){

        restRequestFactoryWrapper.callHttpRestRequest(getString(R.string.baseUrl)+getString(R.string.deviceReports)+ Utility.getDeviceId(this),null, RESTRequest.Method.GET);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Events which will get called when reports API completed the execution.
     */
    private RestResponseShowFeedbackInterface restResponseShowFeedbackInterface=new RestResponseShowFeedbackInterface() {
    @Override
    public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {

        String json=reposeJson.getText();
        report=Report.parseDeviceReport(json);

    }

    @Override
    public void onErrorInBackgroundOperation(RESTResponse reposeJson) {

    }

    @Override
    public void onSuccessInForeGroundOperation(RESTResponse restResponse) {

        if (report!=null){
            report.getDeviceReportList();
        }



    }

    @Override
    public void onErrorInForeGroundOperation(RESTResponse restResponse) {

    }
};

}
