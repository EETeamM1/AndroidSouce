package com.transility.tim.android.Adapters;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.transility.tim.android.BuildConfig;

import com.transility.tim.android.R;
import com.transility.tim.android.ReportsActivity;
import com.transility.tim.android.bean.DeviceReport;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;

import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Test case to check the adapters.
 * Created by ambesh.kukreja on 8/9/2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReportsAdapterTest {

    private ActivityController<ReportsActivity> activity;


    @Before
    public void setUp(){
        activity = Robolectric.buildActivity(ReportsActivity.class).create();

    }

    @After
    public void tearDown(){

        activity=null;
    }

    @Test
    public void test001CheckIfCountOfItemsIsCorrect(){

        ArrayList<DeviceReport> deviceReports=new ArrayList<>();
        DeviceReport deviceReport1=new DeviceReport();
        deviceReport1.setUserName("Ambesh");
        deviceReport1.setInTime("1234567891055");
        deviceReport1.setInTime("1234567891355");

        deviceReports.add(deviceReport1);
        DeviceReport deviceReport2=new DeviceReport();
        deviceReport2.setUserName("Surjeet");
        deviceReport2.setInTime("1234567891055");
        deviceReport2.setInTime("1234567891355");
        deviceReports.add(deviceReport2);
        ReportsAdapter reportsAdapter=new ReportsAdapter(deviceReports);

        Assert.assertEquals("Count is incorrect",deviceReports.size(),reportsAdapter.getItemCount());

    }


    @Test
    public void test002CheckIfUserDataisCorrectShown(){

        ArrayList<DeviceReport> deviceReports=new ArrayList<>();
        DeviceReport deviceReport1=new DeviceReport();
        deviceReport1.setUserName("Ambesh");
        deviceReport1.setInTime("1470759158");
        deviceReport1.setOutTime("1470759158");

        deviceReports.add(deviceReport1);
        DeviceReport deviceReport2=new DeviceReport();
        deviceReport2.setUserName("Surjeet");
        deviceReport2.setInTime("1470759158");
        deviceReport2.setOutTime("1470759158");
        deviceReports.add(deviceReport2);
        ReportsAdapter reportsAdapter=new ReportsAdapter(deviceReports);
        RecyclerView reportsScreenRv= (RecyclerView) activity.get().findViewById(R.id.reportsScreenRv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity.get());
        reportsScreenRv.setAdapter(reportsAdapter);
        reportsScreenRv.setLayoutManager(layoutManager);
        ReportsAdapter.ReportsRecyclerViewHolder reportsRecyclerViewHolder =reportsAdapter.onCreateViewHolder(reportsScreenRv,0);
        reportsAdapter.onBindViewHolder(reportsRecyclerViewHolder,0);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a", Locale.US);

        Assert.assertEquals(simpleDateFormat.format(new Date(Long.parseLong(deviceReport1.getInTime()))),reportsRecyclerViewHolder.intTimeTv.getText());
        Assert.assertEquals(simpleDateFormat.format(new Date(Long.parseLong(deviceReport1.getOutTime()))),reportsRecyclerViewHolder.outTimeTv.getText());
        Assert.assertEquals(deviceReport1.getUserName(),reportsRecyclerViewHolder.userNameTv.getText());

    }
}
