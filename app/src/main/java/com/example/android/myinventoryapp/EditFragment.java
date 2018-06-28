package com.example.android.myinventoryapp;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    /** EditText field to enter the book's title */
    private EditText mTitleEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** EditText field to enter the quantity of the books */
    private EditText mQuantityEditText;

    /** EditText field to enter the supplier name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier's phone number*/
    private EditText mSupplierPhoneNumberNameEditText;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri mCurrentBookUri;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
//
//        // Examine the intent that was used to launch this activity,
//        // in order to figure out if we're creating a new pet or editing an existing one.
//        Intent intent = getIntent();
//        mCurrentBookUri = intent.getData();
//
//        // If the intent DOES NOT contain a pet content URI, then we know that we are
//        // creating a new pet.
//        if (mCurrentBookUri == null){
//            // This is a new pet, so change the app bar to say "Add a Pet"
//            setTitle(getString(R.string.edit_activity_title_new_book));
//
////            // Invalidate the options menu, so the "Delete" menu option can be hidden.
////            // (It doesn't make sense to delete a pet that hasn't been created yet.)
//            invalidateOptionsMenu();
//        } else {
//            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
//            setTitle(getContext().getResources().getString(R.string.edit_activity_title_edit_book));
//
//            // Initialize a loader to read the pet data from the database
//            // and display the current values in the editor
//            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
//        }
//
//        // Find all relevant views that we will need to read user input from
//        mTitleEditText = (EditText) findViewById(R.id.title_edit_fragment);
//        mPriceEditText = (EditText) findViewById(R.id.edit_book_title);
//        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
//        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
//        mSupplierPhoneNumberNameEditText= (EditText) findViewById(R.id.edit_supplier_number);
//
//        // Setup OnTouchListeners on all the input fields, so we can determine if the user
//        // has touched or modified them. This will let us know if there are unsaved changes
//        // or not, if the user tries to leave the editor without saving.
//        mNameEditText.setOnTouchListener(mTouchListener);
//        mBreedEditText.setOnTouchListener(mTouchListener);
//        mWeightEditText.setOnTouchListener(mTouchListener);
//        mGenderSpinner.setOnTouchListener(mTouchListener);
    }

}
