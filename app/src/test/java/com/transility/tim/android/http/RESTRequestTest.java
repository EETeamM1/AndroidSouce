package com.transility.tim.android.http;

import com.transility.tim.android.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import com.transility.tim.android.Constants;


/**
 * Created by himanshu bapna on 21/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RESTRequestTest {

    @Test
    public void testfillOutParameters(){
        String uri = "http://test.example.com/Im/api/deviceIssue/deviceTimeLineReport?";
        RESTRequest restRequest = new RESTRequest(RESTRequest.Method.GET, uri, null, null);

        final Map<String,Object> queryParams = new HashMap<String,Object>();
        queryParams.put("deviceId", "12345655474255");
        queryParams.put("beginDate", "12072016");
        queryParams.put("endDate", "13072016");

        String actualValue = restRequest.fillOutParameters(uri+ Constants.NAME_QUERY_PARAMS, queryParams);
        String expectedVaue = uri+"endDate=13072016&beginDate=12072016&deviceId=12345655474255";

        Assert.assertEquals("URI is not correct", expectedVaue, actualValue );
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
