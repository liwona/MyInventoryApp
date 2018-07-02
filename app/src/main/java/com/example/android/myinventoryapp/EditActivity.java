package com.example.android.myinventoryapp;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;
import java.util.Currency;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the book's title */
    private EditText mTitleEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** TextView field to enter Currency symbol */
    private TextView mPriceCurencyTextView;

    /** EditText field to enter the quantity of the books */
    private EditText mQuantityEditText;

    /** EditText field to enter the supplier name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier's phone number*/
    private EditText mSupplierPhoneNumberNameEditText;

    /** Button to order book from Supplier*/
    private Button mOrderBook;

    /** String which contains Supplier Phone Number*/
    private String supplierPhoneString;

    /** String which contains Quantity*/
    private String quantityString;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri mCurrentBookUri;

    /** Boolean flag that keeps track of whether the book has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;

    /** Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentBookUri == null){
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.edit_activity_title_new_book));

            // Disabled Order Button
            mOrderBook = findViewById(R.id.order_button);
            mOrderBook.setVisibility(View.GONE);

//            // Invalidate the options menu, so the "Delete" menu option can be hidden.
//            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.edit_activity_title_edit_book));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mPriceCurencyTextView = (TextView) findViewById(R.id.label_price_currency);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberNameEditText= (EditText) findViewById(R.id.edit_supplier_number);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);

        // Set Currency symbol to TextView
        Currency currency = NumberFormat.getCurrencyInstance().getCurrency();
        String symbol = currency.getSymbol();
        mPriceCurencyTextView.setText(symbol);

        mOrderBook = findViewById(R.id.order_button);
        mOrderBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentCall();
            }
        });

        Button decrement = findViewById(R.id.decrement);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });

        Button increment = findViewById(R.id.increment);
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });
    }

    private void increment() {
        quantityString = mQuantityEditText.getText().toString().trim();

        if (quantityString.isEmpty()){
            quantityString = "0";
        }

        int quantity = Integer.parseInt(quantityString);

        quantity = quantity + 1;
        mQuantityEditText.setText(Integer.toString(quantity));
    }

    private void decrement() {
        quantityString = mQuantityEditText.getText().toString().trim();
        if (quantityString.isEmpty()){
            quantityString = "0";
        }

        int quantity = Integer.parseInt(quantityString);

        if (quantity <= 0) {
            // Show an error message on toast
            Toast.makeText(this, getString(R.string.quantity_limitation), Toast.LENGTH_SHORT ).show();
            // Exit this method early because no possible to order less cups of coffee
            return;
        }
        quantity = quantity - 1;
        mQuantityEditText.setText(Integer.toString(quantity));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_BOOK_TITLE,
                InventoryEntry.COLUMN_BOOK_PRICE,
                InventoryEntry.COLUMN_BOOK_QUANTITY,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME,
                InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };

        // This Loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,        // Table to query
                projection,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);


            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberNameEditText.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberNameEditText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save:
                // Save book to database
                if (saveBook() == 0){
                    return false;
                }
                Toast.makeText(this, getString(R.string.edit_update_successful),
                        Toast.LENGTH_SHORT).show();
                // Exit activity and jump back to MainActivity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        supplierPhoneString= mSupplierPhoneNumberNameEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the edit fields are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            // Toast message
            Toast.makeText(this, getString(R.string.edit_add_values),
                    Toast.LENGTH_SHORT).show();
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return 0;
        }

        // Creating a ContentValues object where column names are the keys,
        // and book's attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_BOOK_TITLE,titleString );
        values.put(InventoryEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);

        // Determining if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null)  {
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null){
                // If the new content URI is null, then there was an error with insertion.

            } else {
                Toast.makeText(this, getString(R.string.edit_insert_successful),
                        Toast.LENGTH_LONG).show();
                return 1;
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null,
                    null);

            return rowsAffected;
        } return 0;
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_msg);
            builder.setPositiveButton(R.string.discard_changes, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing_book, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
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
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsAffected = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.edit_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void intentCall() {
        supplierPhoneString= mSupplierPhoneNumberNameEditText.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhoneString));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(EditActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.CALL_PHONE
                }, 0);
            }
            startActivity(intent);
        }
    }
}
