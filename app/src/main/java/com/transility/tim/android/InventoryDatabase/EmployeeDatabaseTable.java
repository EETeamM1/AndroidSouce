package com.transility.tim.android.InventoryDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.transility.tim.android.bean.EmployeeInfoBean;

/**
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeDatabaseTable {

    private  final static String ID = "_id";

    private final static String TABLE_EMPLOYEE = "EMPLOYEES";
    private final static String USERNAME ="userName";
    private final static String TIMEOUT_PERIOD="timeOutPeriod";
    private final static String SESSIONTOKEN="sessionToken";


    /**
     * Empty constrouctor might be used in future.
     */
    public EmployeeDatabaseTable(){}

    /**
     * Create a empty data base table
     * @param sqLiteDatabase
     */
    public void createEmployeeDatabaseTable(SQLiteDatabase sqLiteDatabase){

        sqLiteDatabase.execSQL( "CREATE TABLE "+ TABLE_EMPLOYEE+ " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +SESSIONTOKEN+" VARCHAR NOT NULL,"+
                USERNAME + " VARCHAR NOT NULL," +
                TIMEOUT_PERIOD + " INTEGER NOT NULL" + ")" );

    }

    /**
     * Function to be called to perfrom changes when the datra base is updated. Created for future perspective
     * @param sqLiteDatabase
     * @param currentVersion
     */
    public void upgradeDeviceDatabase(SQLiteDatabase sqLiteDatabase,int currentVersion){

    }

    /**
     * Retuns the current Employee information stored in device.
     * @param sqLiteDatabase
     * @return
     */
    public EmployeeInfoBean getTheInfoOfCurrentEmployee(SQLiteDatabase sqLiteDatabase){

        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
        String[] columnsName={USERNAME,TIMEOUT_PERIOD,SESSIONTOKEN};

        Cursor cursor=sqLiteDatabase.query(true,TABLE_EMPLOYEE,columnsName,null,null,null,null,null,null);

        while (cursor.moveToNext()){
            employeeInfoBean.setUserName(cursor.getString(cursor.getColumnIndex(USERNAME)));
            employeeInfoBean.setTimeOutPeriod(cursor.getInt(cursor.getColumnIndex(TIMEOUT_PERIOD)));
            employeeInfoBean.setSessionToken(cursor.getString(cursor.getColumnIndex(SESSIONTOKEN)));

        }
        cursor.close();
        return employeeInfoBean;
    }


    /**
     * Function to delete all the information of employees from the database.
     * returns true when all the information is deleted , false otherwise.
     * @param sqLiteDatabase
     * @return  boolean
     */
    public boolean deleteEmployeeInfoFromDatabase(SQLiteDatabase sqLiteDatabase){
        boolean delteEmployeeFromDatabase=false;
        int numberOfRowsAffected=  sqLiteDatabase.delete(TABLE_EMPLOYEE,null,null);

        if(numberOfRowsAffected>1){
            delteEmployeeFromDatabase=true;
        }

        return delteEmployeeFromDatabase;
    }

    /**
     *
     * Inserts the new row in employee information table.
     * @param sqLiteDatabase
     * @param employeeInfoBean
     * @return boolean
     */
    public boolean insertEmployeeInfoToEmployeeInfoTable(SQLiteDatabase sqLiteDatabase,EmployeeInfoBean employeeInfoBean){
        boolean insertEmployeeToDatabase=false;
        deleteEmployeeInfoFromDatabase(sqLiteDatabase);
        ContentValues contentValues=new ContentValues();

        contentValues.put(TIMEOUT_PERIOD,employeeInfoBean.getTimeOutPeriod());
        contentValues.put(USERNAME,employeeInfoBean.getUserName());
        contentValues.put(SESSIONTOKEN,employeeInfoBean.getSessionToken());
        long status= sqLiteDatabase.insert(TABLE_EMPLOYEE,null,contentValues);
        if (status!=-1){
            insertEmployeeToDatabase=true;
        }

        return insertEmployeeToDatabase;
    }


    /**
     * Returns the row count for table.
     * @param sqLiteDatabase
     * @return
     */
    public int getEmployeeTableRowCount(SQLiteDatabase sqLiteDatabase){

        String countQuery="Select COUNT(*) From "+TABLE_EMPLOYEE;

        int count=0;
        Cursor cursor=sqLiteDatabase.rawQuery(countQuery,null);
        if (cursor.moveToNext()){
            count=cursor.getInt(0);
        }
        cursor.close();
        return count;

    }


    /**
     * Returns the session token.
     * @param sqLiteDatabase
     * @return
     */
    public String getSessionToken(SQLiteDatabase sqLiteDatabase){
        String[] columnsName={SESSIONTOKEN};
        Cursor cursor=sqLiteDatabase.query(true,TABLE_EMPLOYEE,columnsName,null,null,null,null,null,null);
        String sessionToken=null;
        while (cursor.moveToNext()){
            sessionToken=cursor.getString(cursor.getColumnIndex(SESSIONTOKEN));
        }
        return sessionToken;
    }
}
