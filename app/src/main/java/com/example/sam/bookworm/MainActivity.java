package com.example.sam.bookworm;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Log Tag for logging errors
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // To build base url
    Uri.Builder mBuilder;
    // Google Books Api Url
    private String BOOKS_API_REQUEST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set listener on search button
        final ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get search query from edit text
                EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                String searchQuery = searchEditText.getText().toString();

                // Check if something entered in edit text
                if (searchQuery.equals("")) {
                    // make default text appear
                    TextView defaultTextView = (TextView) findViewById(R.id.default_text_view);
                    defaultTextView.setVisibility(View.VISIBLE);
                } else {
                    // build the complete url with search query parameter
                    mBuilder = new Uri.Builder();
                    mBuilder.scheme("https")
                            .authority("www.googleapis.com")
                            .appendPath("books")
                            .appendPath("v1")
                            .appendPath("volumes")
                            .appendQueryParameter("key", "AIzaSyCP0twhbuFHue2jvx9V7VD8XxhHFoQ4Kgs")
                            .appendQueryParameter("q", searchEditText.getText().toString());

                    // set the vale for global variable BOOKS_API_REQUEST_URL
                    BOOKS_API_REQUEST_URL = mBuilder.build().toString();

                    // fire off booksAsyncTask to connect to the given url
                    BooksAsyncTask booksAsyncTask = new BooksAsyncTask();
                    booksAsyncTask.execute();
                }
            }
        });
    }


    /**
     * Update UI with Book {@link Book} object.
     * Display the first book in TextView {@link android.widget.TextView}
     * with id display_books_text_view.
     * @param books ArrayList {@link ArrayList} of Book {@link Book} objects
     */
    private void updateUI(ArrayList<Book> books) {
        // ListView to display list of books
        ListView listView = (ListView) findViewById(R.id.book_list);

        // hide default text
        TextView defaultTextView = (TextView) findViewById(R.id.default_text_view);
        defaultTextView.setVisibility(View.GONE);

        // custom adapter for listView
        BooksAdapter adapter = new BooksAdapter(this, books);

        // set adapter on listView to display books
        listView.setAdapter(adapter);
    }

    /**
     * {@link AsyncTask} to preform the network request on a background thread, and then update
     * the UI with the books in the response.
     */
    private class BooksAsyncTask extends AsyncTask<Void, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(Void... voids) {
            // Create URL object
            URL url = createUrl(BOOKS_API_REQUEST_URL);

            // String to store JSON response
            String jsonResponse = "";

            // Perform HTTP request to the URL and receive a JSON response
            if (url != null) {
                try {
                    jsonResponse = makeHTTPRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Problem connecting to network", e);
                }
            }

            // if json string is empty, do not parse the string and return null
            if (TextUtils.isEmpty(jsonResponse)) {
                return null;
            }

            // Extract relevant fields from the JSON response and create Book object
            // Return Book object as the result for BookAsyncTask
            return extractFeatureFromJson(jsonResponse);
        }


        /**
         * Update the screen with the given book (which was the result of the
         * {@link BooksAsyncTask}
         *
         * @param books array list of {@link Book}
         */
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            // check if books object is null
            if (books == null) {
                return;
            }

            // update the UI with the received book object
            updateUI(books);
        }

        /**
         * Returns a URL object corresponding to the passed url string
         *
         * @param urlString url string
         * @return URL object {@link URL}
         */
        private URL createUrl(String urlString) {
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Problem building URL object", e);
            }
            return url;
        }

        /**
         * Makes an HTTP request to the passed URL and returns a json response string
         *
         * @param url object {@link URL}
         * @return JSON response string
         */
        private String makeHTTPRequest(URL url) throws IOException {
            // String to store json response from server
            String jsonResponse = "";

            // InputStream
            InputStream inputStream = null;
            HttpURLConnection httpURLConnection = null;
            try {
                // Open connection to requested Url
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // set http request method
                httpURLConnection.setRequestMethod("GET");

                // set read and connection timeouts in milliseconds
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);

                // Connect to server and catch SocketTimeoutException and IOException
                httpURLConnection.connect();

                // get input stream and catch UnknownServerException and IOException
                inputStream = httpURLConnection.getInputStream();

                // read from the inputStream
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem opening or connecting to url");
                e.printStackTrace();
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Reads from InputStream {@link InputStream} and returns string read
         *
         * @param inputStream {@link InputStream}
         * @return json response string
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            // use string mBuilder to build json string
            StringBuilder jsonResponseBuilder = new StringBuilder();


            // read line by line from buffer
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                jsonResponseBuilder.append(line);
                line = reader.readLine();
            }

            // close the reader
            reader.close();

            // return json response string
            return jsonResponseBuilder.toString();

        }

        /**
         * Parses JSON response string and generates an ArrayList {@link ArrayList}
         * of Book {@link Book} objects.
         * @param jsonResponse JSON string
         * @return ArrayList {@link ArrayList} of Book {@link Book} objects.
         */
        private ArrayList<Book> extractFeatureFromJson(String jsonResponse) {
            // To store list of books
            ArrayList<Book> books = new ArrayList<>();
            try {
                JSONObject rootJsonObject = new JSONObject(jsonResponse);
                JSONArray items = rootJsonObject.optJSONArray("items");
                for (int i = 0; i < items.length() ; i++) {
                    // find current book
                    JSONObject currentBook = items.optJSONObject(i);

                    // get sale info for current book
                    JSONObject saleInfo = currentBook.optJSONObject("saleInfo");

                    // check saleability for current book and get price and currency code for
                    // current book
                    // set value for price -1 if book is not available
                    // set value for price if book is available
                    // set value for price if book is for pre order
                    // set value for price 0 if book is free
                    // set value for currency code null if book is free or not available
                    String saleability = saleInfo.optString("saleability");
                    float price = -1;
                    String currencyCode = null;
                    if (saleability.equals("FOR_SALE") || saleability.equals("FOR_PRE_ORDER")) {
                        JSONObject retailPrice = saleInfo.optJSONObject("retailPrice");
                        price = (float) retailPrice.optDouble("amount");
                        currencyCode = retailPrice.optString("currencyCode");
                    } else if (saleability.equals("FREE")) {
                        price = 0;
                    }

                    // get volume info for current book
                    JSONObject volumeInfo = currentBook.optJSONObject("volumeInfo");
                    // get title of book
                    String title = volumeInfo.optString("title");

                    // get rating of book
                    // rating is null, if not available
                    float rating = (float) volumeInfo.optDouble("averageRating");

                    // get authors of book
                    JSONArray authorJSONArray = volumeInfo.optJSONArray("authors");
                    String[] authors = null;

                    // some books don't have authors info
                    if (authorJSONArray != null) {
                        authors = new String[authorJSONArray.length()];

                        // copy authors information in array
                        for (int k = 0; k < authorJSONArray.length(); k++) {
                            authors[k] = authorJSONArray.optString(k);
                        }
                    }
                    // add current book to list
                    books.add(new Book(title, authors, rating, price, currencyCode));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Return array list of books
            return books;
        }
    }

}
