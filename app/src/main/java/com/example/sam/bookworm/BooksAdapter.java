package com.example.sam.bookworm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;

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

        // find TextView with id book_rating and set value from currentBook on it
        TextView ratingTextView = (TextView) convertView.findViewById(R.id.book_rating);
        // if rating is not available, remove star icon, otherwise set the rating value
        // to the rating TextView
        ImageView starIcon = (ImageView) convertView.findViewById(R.id.star_icon);
        if (Float.isNaN(currentBook.getRating())) {
            starIcon.setVisibility(View.INVISIBLE);
            ratingTextView.setText("");
        } else {
            starIcon.setVisibility(View.VISIBLE);
            ratingTextView.setText(String.format(getContext().getString(R.string.string_format),
                    currentBook.getRating()));
        }

        // find TextView with id book_price and set value from currentBook on it
        TextView priceTextView = (TextView) convertView.findViewById(R.id.book_price);
        priceTextView.setText(getTextForPrice(currentBook.getPrice(),
                currentBook.getCurrencyCode()));

        // return the view for current position
        return convertView;
    }

    /**
     * Helper method which returns text to be displayed for price {@link Book#mPrice}
     * for current Book {@link Book} object.
     *
     * @param price          {@link Book#mPrice}
     * @param currencySymbol {@link Book#mCurrencyCode}
     * @return text to be displayed for price of book
     */
    private String getTextForPrice(float price, String currencySymbol) {
        if (price == -1.0f)
            return getContext().getString(R.string.not_available);
        else if (price == 0.0f)
            return getContext().getString(R.string.free);
        else {
            // get the right currency symbol and concatenate with price
            Currency currency = Currency.getInstance(currencySymbol);
            return currency.getSymbol() + " " + price;
        }
    }

    /**
     * Helper method which returns text to be displayed for author(s) {@link Book#mAuthors}
     * for current Book {@link Book} object.
     *
     * @param authors {@link Book#mAuthors}
     * @return text to be displayed for author(s) of book
     */
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
