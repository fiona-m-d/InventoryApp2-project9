package com.example.android.inventoryapp;

/**
 * Created by fiona on 05/08/2018.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.inventoryapp.Data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the item data loader
    private static final int EXISTING_ITEM_LOADER = 0;

    //Content URI for the existing item (null if it's a new entry)
    private Uri mCurrentItemUri;

    //EditText field to enter item's name
    private EditText mNameEditText;

    // EditText field to enter the item's price
    private EditText mPriceEditText;

    // EditText field to enter the item's quantity
    private EditText mQuantityEditText;

    // EditText field to enter the supplier name
    private EditText mSupplierEditText;

    // EditText field to enter the supplier's phone number
    private EditText mPhoneEditText;

    //Boolean to track whether an item entry has been edited or not
    private boolean mItemHasChanged = false;

    //Variable for tracking current stock
    String quantity;

    //Variable for assigning updated stock
    int newQuantity;

    //OnTouchListener that listens for list selection, and changes mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Button increase = (Button) findViewById(R.id.increase_button);
        Button decrease = (Button) findViewById(R.id.decrease_button);
        Button callSupplier = (Button) findViewById(R.id.call_supplier_button);
        Button increaseButton = (Button) findViewById(R.id.increase_button);
        Button decreaseButton = (Button) findViewById(R.id.decrease_button);

        // Determine from intent used to launch activity whether adding or editing an item
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain an inventory content URI, then new item is being created
        if (mCurrentItemUri == null) {
            // This is a new entry, so app bar should read 'Enter new inventory details'
            setTitle(getString(R.string.editor_activity_title_new_entry));
            //The Quantity and Call Supplier buttons should also be hidden
            increase.setVisibility(View.GONE);
            decrease.setVisibility(View.GONE);
            callSupplier.setVisibility(View.GONE);

            // Hide Delete from menu when adding a new item
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing entry, so change app bar to say "Edit Entry"
            setTitle(getString(R.string.editor_activity_title_edit_entry));

            // Initialize a loader to read data from the database and display the current values
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views for reading user input
        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier);
        mPhoneEditText = (EditText) findViewById(R.id.edit_phone);


        // Setup OnTouchListeners on all input fields, to determine if they've been tapped by user
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        //Set up an onClickListener to track when the Call Supplier button is pressed
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mPhoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(R.string.phone_label + number));
                if (ActivityCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditorActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 10);
                    return;
                } else {
                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), R.string.phone_no_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

         //Set up an OnclickListener to track when the Increase Quantity button is pressed

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemHasChanged = true;
                quantity = mQuantityEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(quantity)) {
                    newQuantity = Integer.parseInt(quantity);
                    mQuantityEditText.setText(String.valueOf(newQuantity + 1));
                } else {
                    Toast.makeText(EditorActivity.this, getString(R.string.quantity_zero_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Set up an OnclickListener to track when the Decrease Quantity button is pressed
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemHasChanged = true;
                quantity = mQuantityEditText.getText().toString().trim();
                if(mCurrentItemUri == null && TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, getString(R.string.quantity_zero_message),
                            Toast.LENGTH_SHORT).show();
                } else {
                    newQuantity = Integer.parseInt(quantity);
                    if(!TextUtils.isEmpty(quantity) && newQuantity >= 1) {
                        mQuantityEditText.setText(String.valueOf(newQuantity - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, getString(R.string.quantity_zero_message),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //Get user input from editor and save entry into database
    private void saveEntry() {
        // Read from input fields and trim leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // Check if new entry and check if all the fields in the editor are blank
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(phoneString)) {
            // Since no fields were modified, return early without creating a new entry
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values (convert values where needed)

        //Check whether there is anything in the Price field, and only parse the value if != null
        double parsedPrice;
        if(priceString!=null){
        parsedPrice = Double.parseDouble(priceString);}
        else {parsedPrice=0;}

        //Check whether there is anything in the Quantity field, and only parse the value if != null
        double parsedQuantity;
        if(quantityString!=null){
        parsedQuantity = Integer.parseInt(quantityString);}
        else {parsedQuantity=0;}

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, parsedPrice);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, parsedQuantity);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONENUMBER, phoneString);

        // Determine if this is a new or existing entry by checking if mCurrentItemtUri is null
        if (mCurrentItemUri == null) {
            // This is a new entry, so pass into the provider and return the content URI
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // This is an existing entry, so update with content URI: mCurrentItemUri and pass in
            // //the new ContentValues (null for the selection and selection args as not needed
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null,
                    null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //Method for updating the menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new entry hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicks on a menu option
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save the entry to thr database
                saveEntry();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If entry hasn't changed continue with navigating up
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // If there are unsaved changes warn user and get confirmation before discarding
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // If the user selects the "Discard" option
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that informs the user that they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the entry hasn't changed proceed with navigating back
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        //  If there are unsaved changes warn user and get confirmation before discarding
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show a dialog that informs the user that they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define projection for fields needed to display in the Edit view (all fields)
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_PHONENUMBER};

        // Loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Check and finish if cursor is null/there's fewer than 1 row in the db
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Read data from the first (and only) row of the cursor
        if (cursor.moveToFirst()) {
            // Find the columns of each of the attributes
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONENUMBER);

            // Read the attributes from the Cursor for the current item
            String productName = cursor.getString(nameColumnIndex);
            double productPrice = cursor.getDouble(priceColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            String productSupplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(phoneColumnIndex);

            // Update the TextViews with the attributes for the current item
            mNameEditText.setText(productName);
            mPriceEditText.setText(Double.toString(productPrice));
            mQuantityEditText.setText(Integer.toString(productQuantity));
            mSupplierEditText.setText(productSupplier);
            mPhoneEditText.setText(supplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners for dialog buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User selected "Keep editing" button, so dismiss the dialog and continue
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for dialog buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteEntry() {
        // Only perform the delete if this is an existing enry
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the entry at the given content URI
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            //Verify whether the entry deleted successfully or not
            if (rowsDeleted == 0) {
                // If no rows were deleted we can assume that there was an error with the delete
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Delete was successful
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
