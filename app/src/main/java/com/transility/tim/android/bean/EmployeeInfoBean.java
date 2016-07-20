package com.transility.tim.android.bean;

/**
 * Employee information bean to store info of Employees.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeInfoBean {
    private String masterPassword;

    private int timeOutPeriod;
    private String sessionToken;

    public String getSessionToken(){
        return  sessionToken;
    }

    public void setSessionToken(String sessionToken){

        this.sessionToken=sessionToken;
    }
    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public int getTimeOutPeriod() {
        return timeOutPeriod;
    }

    public void setTimeOutPeriod(int timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
    }


}
