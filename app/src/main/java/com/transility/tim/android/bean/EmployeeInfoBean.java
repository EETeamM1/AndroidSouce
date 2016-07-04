package com.transility.tim.android.bean;

/**
 * Employee information bean to store info of Employees.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeInfoBean {
    private String userEmail;

    private int timeOutPeriod;
    private String masterPassword;
    private String sessionToken;
    private String masterUser;

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String getSessionToken(){
        return  sessionToken;
    }

    public void setSessionToken(String sessionToken){

        this.sessionToken=sessionToken;
    }
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }



    public int getTimeOutPeriod() {
        return timeOutPeriod;
    }

    public void setTimeOutPeriod(int timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
    }

    public String getMasterUser(){
        return masterUser;
    }
    public void setMasterUser(String masterUser){
        this.masterUser=masterUser;

    }
}
