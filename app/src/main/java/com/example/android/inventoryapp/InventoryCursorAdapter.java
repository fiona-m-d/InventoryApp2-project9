package com.example.android.inventoryapp;

/**
 * Created by fiona on 05/08/2018.
 */


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    int productQuantity;


    // Construct a new InventoryCursorAdapter
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        //TextView supplierTextView = (TextView) view.findViewById(R.id.supplier);
        //TextView phoneTextView = (TextView) view.findViewById(R.id.phone_number);
        Button sellButton = (Button) view.findViewById(R.id.sell_button);

        // Find the columns of each of the attributes
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        //int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
        //int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONENUMBER);

        // Read the attributes from the Cursor for the current item
        final int productId = cursor.getInt(idColumnIndex);
        final String productName = cursor.getString(nameColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        productQuantity = cursor.getInt(quantityColumnIndex);
        //String productSupplier = cursor.getString(supplierColumnIndex);
        //String supplierPhone = cursor.getString(phoneColumnIndex);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(productName);
        priceTextView.setText(Double.toString(productPrice));
        quantityTextView.setText(Integer.toString(productQuantity));
        //supplierTextView.setText(productSupplier);
        //phoneTextView.setText(supplierPhone);

        // Set the a listener for the Sell button, which when clicked will decrease quantity
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //First verify that the current quantity is greater than 0
                if (productQuantity > 0) {
                    int updatedQuantity = productQuantity - 1;

                    //Identify the relevant URI needed in order to update the stock level
                    Uri quantityUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productId);

                    //Created a ContentsValue object in order to update the quantity for the product
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, updatedQuantity);

                    //use mCurrentItemUri to update the entry and pass in the new quantity value
                    int rowsAffected = context.getContentResolver().update(quantityUri, values, null,
                            null);


                }
            }
        });
    }
}


