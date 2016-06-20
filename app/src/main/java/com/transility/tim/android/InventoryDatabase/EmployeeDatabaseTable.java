package com.transility.tim.android.InventoryDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.transility.tim.android.bean.EmployeeInfoBean;

/**
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class EmployeeDatabaseTable {




    /**
     * Empty constrouctor might be used in future.
     */
    public EmployeeDatabaseTable(){
    }

    /**
     * Create a empty data base table
     * @param sqLiteDatabase
     */
    public void createEmployeeDatabaseTable(SQLiteDatabase sqLiteDatabase){

        String createEmployeeDatabeseTable="CREATE TABLE "+this.getClass().getSimpleName()+" ("
            +EmployeeInfoBean.USER_EMAIL+" VARCHAR NOT NULL,"+EmployeeInfoBean.MASTER_PASSWORD+" VARCHAR NOT NULL,"+EmployeeInfoBean.TIMEOUT_PERIOD+" INTEGER NOT NULL"+")";
            sqLiteDatabase.execSQL(createEmployeeDatabeseTable);

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

        String[] columns={EmployeeInfoBean.USER_EMAIL,EmployeeInfoBean.TIMEOUT_PERIOD,EmployeeInfoBean.MASTER_PASSWORD};
        EmployeeInfoBean employeeInfoBean=new EmployeeInfoBean();
        Cursor cursor=sqLiteDatabase.query(true,this.getClass().getSimpleName(),columns,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            employeeInfoBean.setUserEmail(cursor.getString(cursor.getColumnIndex(EmployeeInfoBean.USER_EMAIL)));

            employeeInfoBean.setTimeOutPeriod(cursor.getInt(cursor.getColumnIndex(EmployeeInfoBean.TIMEOUT_PERIOD)));
            employeeInfoBean.setMasterPassword(cursor.getString(cursor.getColumnIndex(EmployeeInfoBean.MASTER_PASSWORD)));
        }



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


       int numberOfRowsAffected=  sqLiteDatabase.delete(this.getClass().getSimpleName(),"1",null);


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
        ContentValues contentValues=new ContentValues();

        contentValues.put(EmployeeInfoBean.MASTER_PASSWORD,employeeInfoBean.getMasterPassword());
        contentValues.put(EmployeeInfoBean.TIMEOUT_PERIOD,employeeInfoBean.getTimeOutPeriod());
        contentValues.put(EmployeeInfoBean.USER_EMAIL,employeeInfoBean.getUserEmail());
        long status= sqLiteDatabase.insert(this.getClass().getSimpleName(),null,contentValues);
        if (status!=-1){
            insertEmployeeToDatabase=true;
        }

        return insertEmployeeToDatabase;
    }

}
