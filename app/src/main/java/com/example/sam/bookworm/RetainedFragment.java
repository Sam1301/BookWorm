package com.example.sam.bookworm;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Fragment {@link android.app.Fragment} to retain Book {@link Book}
 * ArrayList {@link java.util.ArrayList} generated from the network call.
 */
public class RetainedFragment extends Fragment {
    // mBooks array list we want to retain
    private ArrayList<Book> mBooks;

    // this method is called only once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retain this fragment
        setRetainInstance(true);
    }

    /**
     * Returns books data
     *
     * @return {@link RetainedFragment#mBooks}
     */
    public ArrayList<Book> getBooks() {
        return mBooks;
    }

    /**
     * Sets value for books data
     *
     * @param books books {@link Book} array list {@link ArrayList}
     */
    public void setBooks(ArrayList<Book> books) {
        mBooks = books;
    }
}
