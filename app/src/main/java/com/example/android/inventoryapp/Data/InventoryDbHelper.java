package com.example.android.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by fiona on 29/07/2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    //name of the database
    private static final String DATABASE_NAME = "inventory.db";

    //database version (NOTE: need to increase the version if/when the schema changes)
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        //call parent constructor (as we're subclassing from another class)
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE TABLE inventory
        //Create a String that contains the text for the SQL statement to create the table
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_PRICE + " DOUBLE NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_PHONENUMBER + " TEXT NOT NULL);";

        //now call method to actually create the table
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}