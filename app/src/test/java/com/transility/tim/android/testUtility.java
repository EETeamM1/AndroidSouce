package com.transility.tim.android;

import com.transility.tim.android.Utilities.Utility;

import org.junit.Assert;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test class to test the Utility class.
 * Created by ambesh.kukreja on 7/6/2016.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class testUtility implements UnitTestHelperInventoryManagementApp {



    InventoryManagment inventoryManagment;

    public void test001CheckInternetConnection(){


        Utility utility=new Utility();

//        Assert.assertTrue("Utility function checkInternetConnection should show internet is connected.",utility.checkInternetConnection());


    }

    @Override
    public void setUpBeforeEachTestCase() {

    }

    @Override
    public void tearDownAfterEachTestCase() {

    }
}
