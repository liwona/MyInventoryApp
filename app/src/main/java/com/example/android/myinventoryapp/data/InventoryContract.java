package com.example.android.myinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    private InventoryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.books";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at book data. content://com.example.android.book/crew/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "crew".
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static abstract class InventoryEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** Name of database table for books */
        public static final String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         *
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_TITLE = "book_title";

        /**
         * Price of the book.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_PRICE = "price";

        /**
         * Quantity of the books.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * Supplier name.
         *
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the supplier
         *
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone_number";

//        /**
//         * Possible values for the quantity 0- Out of Stock, 1: <5, 2: >=5
//         */
//        public static final int QUANTITY_OUT_OF_STOCK = 0;
//        public static final int QUANTITY_LESS_THAN = 1;
//        public static final int QUANTITY_MORE_THAN = 2;
    }

}
