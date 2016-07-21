package com.transility.tim.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.transility.tim.android.InventoryDatabase.InventoryDatabaseManager;
import com.transility.tim.android.bean.EmployeeInfoBean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ActivityController;

/**
 * Created by ambesh.kukreja on 7/14/2016.
 */
public class TransilityDeviceAdminActivityTest {
   private  ActivityController<TransilityDeviceAdminActivity> transilityDeviceAdminActivityActivityController;
    private TransilityDeviceAdminActivity transilityDeviceAdminActivity;
    private InventoryManagment inventoryManagment;
    @Before
    public void setUp() {
        inventoryManagment = (InventoryManagment) RuntimeEnvironment.application;
        transilityDeviceAdminActivityActivityController = Robolectric.buildActivity(TransilityDeviceAdminActivity.class).create();

    }

    @After
    public void tearDown() {
        transilityDeviceAdminActivityActivityController.stop().destroy();
    }

    @Test
    public void test001CheckUiWhenUserFirstLaunchTheApplication(){
        transilityDeviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();


         Switch enableDeviceApp= (Switch) transilityDeviceAdminActivity.findViewById(R.id.enableDeviceApp);

         Button logoutBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.logoutBtn);

           Button reportsBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.reportsBtn);

        TextView messageLineTv= (TextView) transilityDeviceAdminActivity.findViewById(R.id.messageLineTv);

        Assert.assertTrue("Enable Admin app swith is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertFalse("Enable Admin app should be disable by default",enableDeviceApp.isChecked());
        Assert.assertTrue("Logut button should not be visible",logoutBtn.getVisibility()==View.GONE);
        Assert.assertTrue("Reports button should not be visible",reportsBtn.getVisibility()==View.GONE);
        Assert.assertEquals("Message is incorrect",messageLineTv.getText(),transilityDeviceAdminActivity.getString(R.string.textAdminAppTextWhenDisabled));

    }
    @Test
    public void test002CheckUiWhenUserHasPerfromedLogin(){
        transilityDeviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();

        InventoryDatabaseManager inventoryDatabaseManager=inventoryManagment.getInventoryDatabasemanager();
        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
        employeeInfoBean.setMasterPassword("Test");
        employeeInfoBean.setSessionToken("Test Session Token");
        employeeInfoBean.setTimeOutPeriod(10);


        inventoryDatabaseManager.getEmployeeDataTable().insertEmployeeInfoToEmployeeInfoTable(inventoryManagment.getSqliteDatabase(),employeeInfoBean);
        Switch enableDeviceApp= (Switch) transilityDeviceAdminActivity.findViewById(R.id.enableDeviceApp);

        Button logoutBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.logoutBtn);

        Button reportsBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.reportsBtn);

        TextView messageLineTv= (TextView) transilityDeviceAdminActivity.findViewById(R.id.messageLineTv);

        Assert.assertTrue("Enable Admin app swith is not visisble",enableDeviceApp.getVisibility()==View.VISIBLE);
        Assert.assertTrue("Logut button should not be visible",logoutBtn.getVisibility()==View.VISIBLE);
        Assert.assertTrue("Reports button should not be visible",reportsBtn.getVisibility()==View.VISIBLE);
//        Assert.assertEquals("Message is incorrect",messageLineTv.getText(),transilityDeviceAdminActivity.getString(R.string.textAdminAppWhenEnabled));
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    public void test003ChekIfUserIsLogoutFromScreenWhenInternetIsConnected(){

        transilityDeviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();
        InventoryDatabaseManager inventoryDatabaseManager=inventoryManagment.getInventoryDatabasemanager();
        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
        employeeInfoBean.setMasterPassword("Test");
        employeeInfoBean.setSessionToken("Test Session Token");
        employeeInfoBean.setTimeOutPeriod(10);
        inventoryDatabaseManager.getEmployeeDataTable().insertEmployeeInfoToEmployeeInfoTable(inventoryManagment.getSqliteDatabase(),employeeInfoBean);
        Button logoutBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.logoutBtn);
        logoutBtn.performClick();
        try{
            Thread.sleep(10000);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        Assert.assertTrue("Data base is not cleared on logout",inventoryDatabaseManager.
                getEmployeeDataTable().
                getEmployeeTableRowCount(inventoryManagment
                        .getSqliteDatabase())==0);

        Assert.assertTrue("Device Admin activity should get destroyed",transilityDeviceAdminActivity.isDestroyed());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    public void test004CheckIfUserLogoutFromScreenWhenInternetIsConnected(){

        transilityDeviceAdminActivity= transilityDeviceAdminActivityActivityController.start().resume().visible().get();
        InventoryDatabaseManager inventoryDatabaseManager=inventoryManagment.getInventoryDatabasemanager();
        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
        employeeInfoBean.setMasterPassword("Test");
        employeeInfoBean.setSessionToken("Test Session Token");
        employeeInfoBean.setTimeOutPeriod(10);
        inventoryDatabaseManager.getEmployeeDataTable().insertEmployeeInfoToEmployeeInfoTable(inventoryManagment.getSqliteDatabase(),employeeInfoBean);
        Button logoutBtn= (Button) transilityDeviceAdminActivity.findViewById(R.id.logoutBtn);
        logoutBtn.performClick();
        try{
            Thread.sleep(10000);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        Assert.assertTrue("Data base is not cleared on logout",inventoryDatabaseManager.
                getEmployeeDataTable().
                getEmployeeTableRowCount(inventoryManagment
                        .getSqliteDatabase())==0);

        Assert.assertTrue("Device Admin activity should get destroyed",transilityDeviceAdminActivity.isDestroyed());
    }
}
