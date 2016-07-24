package com.example.sam.bookworm;

/**
 * {@link Book} class to store books data.
 */
public class Book {
    // title of book
    String mTitle;

    // authors of book
    String[] mAuthors;

    /**
     * Constuctor to initialize {@link Book} object fields
     * @param title title of book {@link Book#mTitle}
     * @param authors authors of book {@link Book#mAuthors}
     */
    public Book (String title, String[] authors) {
        mTitle = title;
        mAuthors = authors;
    }

    /**
     * Returns authors of book
     * @return {@link Book#mAuthors}
     */
    public String[] getAuthor() {
        return mAuthors;
    }

    /**
     * Returns title of book
     * @return {@link Book#mTitle}
     */
    public String getTitle() {
        return mTitle;
    }
}
