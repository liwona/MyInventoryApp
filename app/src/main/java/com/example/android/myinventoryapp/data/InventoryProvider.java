package com.example.android.myinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.myinventoryapp.R;
import com.example.android.myinventoryapp.data.InventoryContract.InventoryEntry;

/**
 * {@link ContentProvider} for InventoryApp.
 */
public class InventoryProvider extends ContentProvider {


    /** Tag for the log messages */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the book table */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /** Database helper object */
    private InventoryDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Notify all listeners that the data has changed for the Pet content URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_BOOK_TITLE);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    name_required), Toast.LENGTH_SHORT).show();
            return null;
        }

        // Check that the price is provided and greater than 0
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_BOOK_PRICE);
        if (price == null || price < 0) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    price_required), Toast.LENGTH_SHORT).show();
            return null;
        }

        // Check that the quantity is provided and greater than or equal to 0
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || quantity < 0) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    quantity_required), Toast.LENGTH_SHORT).show();
            return null;
        }

        // Check that the supplier name is provided
        String supplierName = values.getAsString(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName.isEmpty()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    supplier_name_required), Toast.LENGTH_SHORT).show();
            return null;
        }


        // Check that the quantity is provided and greater than or equal to 0
        String phoneNumber = values.getAsString(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        Integer supplier_phone_number;
        if (phoneNumber.isEmpty()) {
            supplier_phone_number = 0;
        } else {
            supplier_phone_number = values.getAsInteger(InventoryEntry.
                    COLUMN_BOOK_SUPPLIER_PHONE);
        }

        if (supplier_phone_number == null || !(supplier_phone_number > 99999999 &&
                supplier_phone_number < 1000000000)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    supplier_phone_required), Toast.LENGTH_SHORT).show();
            return null;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // If the {@link Inventory#COLUMN_BOOK_TITLE} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_BOOK_TITLE)) {
            String title = contentValues.getAsString(InventoryEntry.COLUMN_BOOK_TITLE);
            if (title.isEmpty()) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.name_required),
                        Toast.LENGTH_SHORT).show();
                return 0;
//                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // If the {@link Inventory#COLUMN_BOOK_PRICE} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_BOOK_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_BOOK_PRICE);
            if (price == null || price <= 0) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                        price_required), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        // If the {@link Inventory#COLUMN_BOOK_QUANTITY} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_BOOK_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || quantity < 0) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                        quantity_required), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        // If the {@link Inventory#COLUMN_BOOK_SUPPLIER_NAME} key is present,
        // check that the name value is not null.
        if (contentValues.containsKey(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            // Check that the quantity is greater than or equal to 0
            String supplierName = contentValues.getAsString(InventoryEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName.isEmpty()) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                        supplier_name_required), Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        // Check that the quantity is provided and greater than or equal to 0
        String phoneNumber = contentValues.getAsString(InventoryEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        Integer supplier_phone_number;
        if (phoneNumber.isEmpty()) {
            supplier_phone_number = 0;
        } else {
            supplier_phone_number = contentValues.getAsInteger(InventoryEntry.
                    COLUMN_BOOK_SUPPLIER_PHONE);
        }

        if (supplier_phone_number == null || !(supplier_phone_number > 99999999 &&
                supplier_phone_number < 1000000000)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.
                    supplier_phone_required), Toast.LENGTH_SHORT).show();
            return 0;
        }

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        // If some or the fields are empty return or wrong, return earlier

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, contentValues, selection,
                selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
