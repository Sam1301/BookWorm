package com.example.sam.bookworm;

/**
 * {@link Book} class to store books data.
 */
public class Book {
    // title of book
    String mTitle;

    // authors of book
    String[] mAuthors;

    // rating of book
    float mRating;

    // price of book
    float mPrice;

    // currency code of price of book
    String mCurrencyCode;

    /**
     * Constuctor to initialize {@link Book} object fields
     *
     * @param title title of book {@link Book#mTitle}
     * @param authors authors of book {@link Book#mAuthors}
     * @param rating rating of book {@link Book#mRating}
     * @param price price of book {@link Book#mPrice}
     * @param currencyCode currency code of price of book {@link Book#mCurrencyCode}
     */
    public Book(String title, String[] authors, float rating, float price, String currencyCode) {
        mTitle = title;
        mAuthors = authors;
        mRating = rating;
        mPrice = price;
        mCurrencyCode = currencyCode;
    }

    /**
     * Returns authors of book
     * @return {@link Book#mAuthors}
     */
    public String[] getAuthors() {
        return mAuthors;
    }

    /**
     * Returns title of book
     * @return {@link Book#mTitle}
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns rating of book
     *
     * @return {@link Book#mRating}
     */
    public float getRating() {
        return mRating;
    }

    /**
     * Returns price of book
     *
     * @return {@link Book#mPrice}
     */
    public float getPrice() {
        return mPrice;
    }

    /**
     * Returns currency code of price of book
     *
     * @return {@link Book#mCurrencyCode}
     */
    public String getCurrencyCode() {
        return mCurrencyCode;
    }
}
