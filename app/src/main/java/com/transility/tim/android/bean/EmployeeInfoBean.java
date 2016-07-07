package com.transility.tim.android.bean;

/**
 * Employee information bean to store info of Employees.
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeInfoBean {
    private String userName;

    private int timeOutPeriod;

    private String sessionToken;




    public String getSessionToken(){
        return  sessionToken;
    }

    public void setSessionToken(String sessionToken){

        this.sessionToken=sessionToken;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userEmail) {
        this.userName = userEmail;
    }



    public int getTimeOutPeriod() {
        return timeOutPeriod;
    }

    public void setTimeOutPeriod(int timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
    }


}
