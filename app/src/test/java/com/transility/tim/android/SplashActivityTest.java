package com.transility.tim.android;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by himanshu bapna on 22/07/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SplashActivityTest {

    private SplashActivity  activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(SplashActivity.class).create().visible().get();
    }

    @After
    public void tearDown() {
        activity = null;
    }

    @Test
    public void testOnCreate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Assert.assertTrue("Test case interrupted", false);
        }
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals("Expected activity is not started",DeviceAdminActivity.class.getCanonicalName(), startedIntent.getComponent().getClassName());
        Assert.assertTrue("Splash screen is not finish", activity.isFinishing());

    }

}
