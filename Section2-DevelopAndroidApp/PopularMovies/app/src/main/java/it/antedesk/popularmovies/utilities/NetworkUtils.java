package it.antedesk.popularmovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Antedesk on 03/03/2018.
 *
 * This class is inspired to the NetworkUtils class defined in Sunshine project to
 * fetch the data
 */

public class NetworkUtils {

    // getting the the class name to use as tag in Log method
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // defining the base url to get the data
    private static final String BASIC_URL =
            "http://api.themoviedb.org/3/movie/";

    // Basic path of image url
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/";
    //
    private final static String API_KEY_QUERY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movie server using a sort. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param sortMode the modality to sort the movies list
     * @param apiKey the api key to access the movie server services
     * @return The URL to use to query the movie server
     *
     */
    public static URL buildMovieListUrl(String sortMode, String apiKey) {
        Uri builtUri = Uri.parse(BASIC_URL).buildUpon()
                .appendPath(sortMode)
                .appendQueryParameter(API_KEY_QUERY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * This method is from Lesson 2 - Connect to the Internet.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    /**
     * Allows to check if the app is connected to internet or not.
     * source of the below code https://goo.gl/q7gpMi
     * @param activity is the current activity that invokes this method.
     * @return a boolean value that states if the app is connected to internet or not
     */
    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
