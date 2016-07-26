package com.transility.tim.android.bean;

import android.os.Parcel;

import com.transility.tim.android.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Himanshu Bapna on 26/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReportTest {

    @Test
    public void testParseDeviceReport(){
        Report report = Report.parseDeviceReport(buildReportJSON());
        validateData(report);
    }

    @Test
    public void testParceling(){
        Report report = Report.parseDeviceReport(buildReportJSON());
        Parcel parcel = Parcel.obtain();
        report.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        report = Report.CREATOR.createFromParcel(parcel);
        validateData(report);
    }

    private String buildReportJSON(){
        String reportJSON = "{" +
                " \"result\": {" +
                " \"timeout\": 0," +
                " \"deviceReportDtoList\": [" +
                "  {" +
                " \"loginTIme\": \"7:00 AM\"," +
                " \"logOutTime\": \"7:30 AM\"," +
                " \"userId\": \"User0\"," +
                " \"userName\": \"Ankit\"" +
                " }," +
                " {" +
                "\"loginTIme\": \"4:00 PM\"," +
                "\"logOutTime\": \"4:15 PM\"," +
                "\"userId\": \"user1\"," +
                "\"userName\": \"Surjeet\"" +
                "}," +
                "{" +
                "\"loginTIme\": \"4:15 PM\"," +
                "\"userId\": \"user2\"," +
                "\"userName\": \"Raghu\"" +
                "}" +
                "]" +
                "}," +
                "\"responseCode\": {" +
                "\"code\": 200" +
                "}" +
                "}";
        return reportJSON;
    }

    private void validateData(Report report){
        Assert.assertNotNull("Report list is null", report.getDeviceReportList());
        Assert.assertEquals("Report list count is incorrect", 3, report.getDeviceReportList().size());

        DeviceReport deviceReport = report.getDeviceReportList().get(0);
        Assert.assertEquals("In time is incorrect", "7:00 AM", deviceReport.getInTime());
        Assert.assertEquals("Out time is incorrect", "7:30 AM", deviceReport.getOutTime());
        Assert.assertEquals("User Id is incorrect", "User0", deviceReport.getUserId());
        Assert.assertEquals("User name is incorrect", "Ankit", deviceReport.getUserName());

        deviceReport = report.getDeviceReportList().get(2);
        Assert.assertEquals("Out time is incorrect", "", deviceReport.getOutTime());
    }

}
