package com.transility.tim.android;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.transility.tim.android.Adapters.ReportsAdapter;
import com.transility.tim.android.Utilities.RestResponseShowFeedbackInterface;
import com.transility.tim.android.Utilities.Utility;
import com.transility.tim.android.bean.Report;
import com.transility.tim.android.http.RESTRequest;
import com.transility.tim.android.http.RESTResponse;
import com.transility.tim.android.http.RestRequestFactoryWrapper;

import java.util.HashMap;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {
    private RestRequestFactoryWrapper restRequestFactoryWrapper;
    private Report report;
    private RecyclerView reportsScreenRv;
    private LinearLayout parentContainerLv;
    private TextView errorMessageTv;
    private ProgressBar reportPb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reports_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reportScreenTb);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.textReports);

            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColorMaroon));
        }



        restRequestFactoryWrapper=new RestRequestFactoryWrapper(this,restResponseShowFeedbackInterface);
        reportsScreenRv= (RecyclerView) findViewById(R.id.reportsScreenRv);
        parentContainerLv= (LinearLayout) findViewById(R.id.parentContainerLv);
        errorMessageTv= (TextView) findViewById(R.id.errorMessageTv);
        reportPb= (ProgressBar) findViewById(R.id.reportPb);

        callGetRequestAndFetchReportsOfDevice();
    }


    /**
     * Api called to call reports API and show on device.
     */
    private void callGetRequestAndFetchReportsOfDevice(){
        if (Utility.checkInternetConnection(this)){
            parentContainerLv.setVisibility(View.GONE);
            Map<String,Object> queryParams=new HashMap<>();
            queryParams.put("deviceId",Utility.getDeviceId(this));
            restRequestFactoryWrapper.callHttpRestRequest(getString(R.string.baseUrl)+getString(R.string.deviceReports),queryParams,null, RESTRequest.Method.GET);
            reportPb.setVisibility(View.VISIBLE);
        }
        else
        {
            updateUiOnFailure(getString(R.string.textNetworkNotAvailable));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Events which will get called when reports API completed the execution.
     */
    private final RestResponseShowFeedbackInterface restResponseShowFeedbackInterface=new RestResponseShowFeedbackInterface() {
        @Override
        public void onSuccessOfBackGroundOperation(RESTResponse reposeJson) {

            String json = reposeJson.getText();
            report = Report.parseDeviceReport(json);

        }

        @Override
        public void onErrorInBackgroundOperation(RESTResponse reposeJson) {

        }

        @Override
        public void onSuccessInForeGroundOperation(RESTResponse restResponse) {
            reportPb.setVisibility(View.GONE);
            if (report != null &&(report.getDeviceReportList()!=null)&& !report.getDeviceReportList().isEmpty()) {
                parentContainerLv.setVisibility(View.VISIBLE);

                ReportsAdapter reportsAdapter = new ReportsAdapter(report.getDeviceReportList());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                reportsScreenRv.setAdapter(reportsAdapter);
                reportsScreenRv.setLayoutManager(layoutManager);

            } else {

                updateUiOnFailure(getString(R.string.textNoReportAvailable));

            }


        }

        @Override
        public void onErrorInForeGroundOperation(RESTResponse restResponse) {
           updateUiOnFailure(getString(R.string.textErrorOccurred));
        }
    };

    /**
     * Shows the error feedback to user.
     * @param message The error message that needs to be shown to user.
     */
    private void updateUiOnFailure(String message){
        parentContainerLv.setVisibility(View.GONE);
        errorMessageTv.setVisibility(View.VISIBLE);
        errorMessageTv.setText(message);
    }
}
