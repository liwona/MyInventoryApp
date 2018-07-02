package com.example.android.myinventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.myinventoryapp.data.InventoryContract.InventoryEntry;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        final int rowId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        TextView nameTextVIew = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);

        // Find the columns of the book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_BOOK_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        // Populate fields with extracted properties
        nameTextVIew.setText(bookName);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);

        Button decrement = (Button) view.findViewById(R.id.minus_button);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = quantityTextView.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                quantity = quantity - 1;
                if (quantity < 0) {
                    // Show an error message on toast
//                    Toast.makeText(MainActivity.this, context.getString(R.string.quantity_limitation), Toast.LENGTH_SHORT ).show();
                    // Exit this method early because no possible to order less cups of coffee
                    return;
                }

                quantityTextView.setText(Integer.toString(quantity));
                // Creating a ContentValues object where column names are the keys,
                // and book's attributes are the values.
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_BOOK_QUANTITY, Integer.toString(quantity));

                Uri currentBookUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, rowId);
                v.getContext().getContentResolver().update(currentBookUri, values, null,
                        null);
            }
        });
    }
}
