package com.transility.tim.android.bean;

import android.database.sqlite.SQLiteDatabase;

/**
 * Employee information bean to store info of Employees.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeInfoBean {
    public  static String USER_EMAIL="userEmail";
    public  static String EMPLOYEE_ID="employeeID";
    public  static String TIMEOUT_PERIOD="timeOutPeriod";
    public static String MASTER_PASSWORD="masterPassword";


    private String userEmail;
    private String employeeID;
    private String timeOutPeriod;

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    private String masterPassword;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getTimeOutPeriod() {
        return timeOutPeriod;
    }

    public void setTimeOutPeriod(String timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
    }
}
