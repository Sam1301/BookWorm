package com.example.sam.bookworm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter {@link ArrayAdapter} for ListView to display a list of books
 */
public class BooksAdapter extends ArrayAdapter<Book> {

    /**
     * Constructor which does not mirror the super class constructor
     *
     * @param context {@link Context}
     * @param books   list of book {@link Book} objects
     */
    public BooksAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a view for the current position in the ListView {@link android.widget.ListView}
     *
     * @param position    position in list
     * @param convertView recycled view {@link View}
     * @param parent      parent AdapterView {@link android.widget.AdapterView}
     * @return a view for the current position in ListView {@link android.widget.ListView}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convertView is null, inflate it using list_item.xml layout, otherwise use recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // find Book object for current position
        Book currentBook = getItem(position);

        // find TextView with id book_title and set value from currentBook on it
        TextView titleTextView = (TextView) convertView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        // find TextView with id book_author and set value from currentBook on it
        TextView authorTextView = (TextView) convertView.findViewById(R.id.book_author);
        authorTextView.setText(getTextForAuthors(currentBook.getAuthors()));

        // return the view for current position
        return convertView;
    }

    private String getTextForAuthors(String[] authors) {
        String authorText = "";
        if (authors != null) {
            for (int i = 0, n = authors.length; i < n; i++) {
                if (i != n - 1) {
                    authorText += authors[i] + ", ";
                } else {
                    authorText += authors[i];
                }
            }
        }
        return authorText;
    }
}
