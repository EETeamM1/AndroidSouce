package com.transility.tim.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transility.tim.android.Adapters.ReportsAdapter;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.DeviceReport;
import com.transility.tim.android.bean.Report;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RESTResponseHandler;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {
    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private Report report;
    private RecyclerView reportsScreenRv;
    private LinearLayout parentContainerLv;
    private TextView errorMessageTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reports_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reportScreenTb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.textReportScreen);

        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorMaron));
        restRequestFactoryWrapper=new RestRequestFactoryWrapper(this,restResponseShowFeedbackInterface);
        reportsScreenRv= (RecyclerView) findViewById(R.id.reportsScreenRv);
        parentContainerLv= (LinearLayout) findViewById(R.id.parentContainerLv);
        errorMessageTv= (TextView) findViewById(R.id.errorMessageTv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        callGetRequestAndFetchReportsOfDevice();
    }


    /**
     * Api called to call reports API and show on device.
     */
    private void callGetRequestAndFetchReportsOfDevice(){
        if (Utility.checkInternetConnection(this)){
            Map<String,Object> queryParams=new HashMap<>();
            queryParams.put("deviceId",Utility.getDeviceId(this));
            restRequestFactoryWrapper.callHttpRestRequest(getString(R.string.baseUrl)+getString(R.string.deviceReports),null,queryParams, RESTRequest.Method.GET);
        }
        else
        {
            parentContainerLv.setVisibility(View.GONE);
            errorMessageTv.setText(getString(R.string.textNetworkNotAvaliable));
        }
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

        if (report!=null&&!report.getDeviceReportList().isEmpty()){

            ReportsAdapter reportsAdapter=new ReportsAdapter(report.getDeviceReportList());
            RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
            reportsScreenRv.setLayoutManager(layoutManager);
            reportsScreenRv.setAdapter(reportsAdapter);
        }
        else {
            parentContainerLv.setVisibility(View.GONE);
            errorMessageTv.setText(getString(R.string.textNoReportAvaliable));

        }




    }

    @Override
    public void onErrorInForeGroundOperation(RESTResponse restResponse) {
        parentContainerLv.setVisibility(View.GONE);
        errorMessageTv.setText(getString(R.string.textErrorOccured));
    }
};

}
