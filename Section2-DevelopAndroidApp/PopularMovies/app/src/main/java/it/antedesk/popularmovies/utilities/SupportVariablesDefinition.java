package it.antedesk.popularmovies.utilities;

import it.antedesk.popularmovies.BuildConfig;

/**
 * Created by Antedesk on 06/03/2018.
 *
 * This static class contains all the variable used across the app's classes
 */

public final class SupportVariablesDefinition {
    // the movie API key loaded from BuildConfig
    public static final String API_KEY = BuildConfig.API_KEY;

    public static final String SIZE_W185 = "w185";
    public static final String SIZE_W92 = "w92";

    // tag for storing in the savedInstanceState the criteria to sort movies
    public static final String SORT_CRITERIUM = "sortCriterium";


    // getting the the class name to use as tag in Log method
    public static final String TAG = NetworkUtils.class.getSimpleName();

    // defining the base url to get the data
    public static final String BASIC_URL =
            "http://api.themoviedb.org/3/movie/";

    // Basic path of image url
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/";

    // TAG for intent and log
    public static final String MOVIE_TAG = "selectedMovie";


}
