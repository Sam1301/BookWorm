package com.example.sam.bookworm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // Log Tag for logging errors
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Google Books Api Url
    private final String BOOKS_API_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1&key=AIzaSyCP0twhbuFHue2jvx9V7VD8XxhHFoQ4Kgs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set listener on search button
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BooksAsyncTask booksAsyncTask = new BooksAsyncTask();
                booksAsyncTask.execute();
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
        TextView displayTextView = (TextView) findViewById(R.id.display_books_text_view);

        // to clear previous content of TextView
        displayTextView.setText("");

        // display books info in TextView
        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            displayTextView.append(currentBook.getTitle() + "\n" +
                    Arrays.toString(currentBook.getAuthor()));
        }

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
                e.printStackTrace();
                Log.e(LOG_TAG, "Problem opening or connecting to url");
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
            // use string builder to build json string
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
                    JSONObject volumeInfo = currentBook.optJSONObject("volumeInfo");
                    // get title of book
                    String title = volumeInfo.optString("title");

                    // get authors of book
                    JSONArray authorJSONArray = volumeInfo.optJSONArray("authors");
                    String[] authors = new String[authorJSONArray.length()];
                    for (int k = 0; k < authorJSONArray.length(); k++) {
                        authors[k] = authorJSONArray.optString(k);
                    }

                    // add current book to list
                    books.add(new Book(title, authors));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Return array list of books
            return books;
        }
    }

}
