package com.transility.tim.android.InventoryDatabase;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Customized Helper class to manage Inventory Database
 * Created by ambesh.kukreja on 6/7/2016.
 */
public class InventoryDatabaseManager extends SQLiteOpenHelper{


    private EmployeeDatabaseTable employeeDatabaseTable;

    public InventoryDatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        employeeDatabaseTable=new EmployeeDatabaseTable();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }
}
