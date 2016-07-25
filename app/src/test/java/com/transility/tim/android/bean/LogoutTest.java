package com.transility.tim.android.bean;

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
public class LogoutTest {

    @Test
    public void testwriteLogoutJson() {
        String logoutJSON = Logout.writeLogoutJson("user21465885257421");
        Assert.assertEquals("Logon JSON is not created correct", getLogoutJSON(), logoutJSON);

    }

    private String getLogoutJSON() {
        return "{" + "\"parameters\":{" +
                "\"sessionToken\":\"user21465885257421\"" +
                "}" +
                "}";
    }

}
