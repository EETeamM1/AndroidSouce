package com.transility.tim.android.bean;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;

import com.transility.tim.android.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Himanshu Bapna on 18/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class LogonTest {

    private final String masterPassword = "test123";
    private final String sessionToken = "user11465378323910";
    private final int timeout = 30;
    private final String username = "user1";
    private final String password = "impetus";
    private final String deviceId = "12345655474255";

    @Test
    public void testParseLogon() {
        Logon logon = Logon.parseLogon(buildLogonJSON());
        validateData(logon);
    }

    @Test
    public void testParceling() {
        Logon logon = Logon.parseLogon(buildLogonJSON());
        Parcel parcel = Parcel.obtain();
        logon.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        logon = Logon.CREATOR.createFromParcel(parcel);
        validateData(logon);
    }

    @Test
    public void testWriteLogonJSON() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(22.68);
        location.setLongitude(75.87);
        String logonJSON = Logon.writeLogonJSON(username, password, location, deviceId);
        Assert.assertEquals("Logon JSON is not created correct", getLogonWriteJSON(), logonJSON);

        location = null;
        logonJSON = Logon.writeLogonJSON(username, password, location, deviceId);
        String expectedJSON = "{\"parameters\":{\"userId\":\"user1\",\"password\":\"impetus\",\"deviceId\":\"12345655474255\",\"osVersion\":\"5.1.1_r9\",\"latitude\":\"\",\"longitude\":\"\"}}";
        Assert.assertEquals("Logon JSON is not created correct", expectedJSON, logonJSON);
    }

    private String buildLogonJSON() {
        String logonJSON = "{" +
                "  \"result\": {" +
                "    \"sessionToken\": \"user11465378323910\"," +
                "     \"masterPassword\": \"test123\"," +
                "    \"timeout\": 30" +
                "  }," +
                "  \"responseCode\": {" +
                "    \"code\": 200," +
                "    \"message\": \"Success\"" +
                "  }" +
                "}";
        return logonJSON;
    }

    private String getLogonWriteJSON() {
        return "{" + "\"parameters\":{" +
                "\"userId\":\"user1\"," +
                "\"password\":\"impetus\"," +
                "\"deviceId\":\"12345655474255\"," +
                "\"osVersion\":\"5.1.1_r9\"," +
                "\"latitude\":22.68," +
                "\"longitude\":75.87" +
                "}" +
                "}";
    }

    private void validateData(Logon logon) {
        Assert.assertEquals("Master password is incorrect during logon parsing", masterPassword, logon.getMasterPassword());
        Assert.assertEquals("session Token is incorrect during logon parsing", sessionToken, logon.getSessionToken());
        Assert.assertEquals("Timeout is incorrect during logon parsing", timeout, logon.getTimeout());
    }

}
