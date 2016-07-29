package com.transility.tim.android.http;

import com.transility.tim.android.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;

/**
 * Created by himanshu bapna on 22/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RESTResponseTest {

    @Test
    public void testStatusIsSuccess() {
        RESTResponse response = new RESTResponse(RESTResponse.Status.SUCCESS_OK, null, null);
        Assert.assertTrue("Success status is not recieved", response.status.isSuccess());
    }

    @Test
    public void testStatusGetCode() {
        RESTResponse response = new RESTResponse(RESTResponse.Status.SUCCESS_OK, null, null);
        Assert.assertEquals("Status code is incorrect", HttpURLConnection.HTTP_OK, Integer.parseInt(response.status.getCode()));
    }

    @Test
    public void testStatusIsError() {
        RESTResponse response = new RESTResponse(RESTResponse.Status.CLIENT_ERROR_UNAUTHORIZED, null, null);
        Assert.assertTrue("Error status is not recieved", response.status.isError());

        response = new RESTResponse(RESTResponse.Status.CLIENT_ERROR, null, null);
        Assert.assertTrue("Error status is not recieved", response.status.isError());

        response = new RESTResponse(RESTResponse.Status.CONNECTOR_ERROR_INTERNAL, null, null);
        Assert.assertTrue("Error status is not recieved", response.status.isError());

        response = new RESTResponse(RESTResponse.Status.SUCCESS_OK, null, null);
        Assert.assertFalse("Error status is recieved", response.status.isError());
    }

    @Test
    public void testIsEmpty() {
        RESTResponse response = new RESTResponse(null, null, null);
        Assert.assertTrue("Response is empty", response.isEmpty());
    }

    @Test
    public void testRelease() {
        RESTResponse response = new RESTResponse(RESTResponse.Status.SUCCESS_OK, new ByteArrayInputStream(getJSON().getBytes()), null);
        Assert.assertNotNull("Before release call response value become null",response.value);
        response.release();
        Assert.assertNull("After release method call response value still not null", response.value);
    }

    @Test
    public void testGetText() {
        RESTResponse response = new RESTResponse(RESTResponse.Status.SUCCESS_OK, new ByteArrayInputStream(getJSON().getBytes()), null);
        Assert.assertEquals("Response value is not correct", getJSON(), response.getText());
    }

    private String getJSON (){
        return  "{" + "\"parameters\":{" +
                "\"userId\":\"user1\"," +
                "\"password\":\"impetus\"," +
                "\"deviceId\":\"12345655474255\"," +
                "\"osVersion\":\"5.1.1_r9\"," +
                "\"latitude\":22.68," +
                "\"longitude\":75.87" +
                "}" +
                "}";
    }

}
