package com.example.android.myinventoryapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.myinventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity {


    // Identifies a particular Loader being used in this component
    private static final int PET_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditActivity
        FloatingActionButton fab = findViewById(R.id.fab_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditFragment.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = (ListView) findViewById(R.id.inventory_list_view);

        // Setup the Adapter to create a list item view for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@Link EditActivity}
                Intent intent = new Intent(MainActivity.this, EditFragment.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link InventoryEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.books/books/2"
                // if the book with ID 2 was clicked on.
                Uri currentBookUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

//        // Kick off the loader
//        getLoaderManager().initLoader(PET_LOADER, null, this);

    }
}
