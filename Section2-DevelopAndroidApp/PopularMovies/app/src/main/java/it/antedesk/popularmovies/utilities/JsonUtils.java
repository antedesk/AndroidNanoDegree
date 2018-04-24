package it.antedesk.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.model.Cast;
import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.model.Review;
import it.antedesk.popularmovies.model.Trailer;

/**
 * Created by Antedesk on 01/03/2018.
 * This class is inspired to the one implemented in the Sandwich Club project and
 * it allows parsing the JSON data retrived through the API.
 */

public class JsonUtils {

    // For log
    private static String JSON_PARSING = "JSON_PARSING";

    // JSON keys for parsing the retrieved JSON
    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    private static final String JSON_VOTE_COUNT_KEY = "vote_count";
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";


    private static final String JSON_CAST_KEY = "cast";
    private static final String JSON_CHARACTER = "character";
    private static final String JSON_ACTOR_NAME = "name";
    private static final String JSON_PROFILE_PATH = "profile_path";

    // JSON keys for parsing the retrieved reviews JSON
    private static final java.lang.String JSON_AUTHOR_KEY = "author";
    private static final java.lang.String JSON_CONTENT_KEY = "content";

    // JSON keys for parsing the retrieved trailers JSON
    private static final java.lang.String JSON_KEY_KEY = "key";
    private static final java.lang.String JSON_NAME_KEY = "name";
    private static final java.lang.String JSON_SITE_KEY = "site";
    private static final java.lang.String JSON_SIZE_KEY = "size";
    private static final java.lang.String JSON_TYPE_KEY = "type";


    // get the list of movies
    public static List<Movie> getMovieList(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        List<Movie> movies = null;
        try {
            // initializing a new JSONObject to parse the list of movies from a JSON
           JSONObject moviesJson = new JSONObject(json);

            // parsing the json string by getting the list of movie results
            JSONArray moviesJArray = moviesJson.getJSONArray(JSON_RESULTS_KEY);
            // if the list is not null, create the aka list
            if(moviesJArray!=null) {
                // initializing the list of movies
                movies = new ArrayList<>();

                for (int i = 0; i < moviesJArray.length(); i++) {
                    // getting the current JSONObj in the list
                    JSONObject movieJSONObj = moviesJArray.getJSONObject(i);

                    // getting the movie's attributes
                    long id = movieJSONObj.optLong(JSON_ID_KEY, 0);
                    String title = movieJSONObj.optString(JSON_TITLE_KEY);
                    String releaseDate = movieJSONObj.optString(JSON_RELEASE_DATE_KEY);
                    String posterPath = movieJSONObj.optString(JSON_POSTER_PATH_KEY);
                    double voteAvarage = movieJSONObj.optDouble(JSON_VOTE_AVERAGE_KEY,0);
                    String overview = movieJSONObj.optString(JSON_OVERVIEW_KEY);
                    long voteCount = movieJSONObj.optLong(JSON_VOTE_COUNT_KEY,0);

                    // creating the movie obj
                    Movie movie = new  Movie(id,
                                            title,
                                            releaseDate,
                                            posterPath,
                                            voteAvarage,
                                            overview,
                                            voteCount);
                    Log.d(JSON_PARSING, movie.getTitle());
                    //Log.d(JSON_PARSING, movie.toString());

                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the results: "+json);
        }
        return movies;
    }

    //retrieving the list of reviews
    public static List<Review> getReviewsList(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        List<Review> reviews = null;
        try {
            // initializing a new JSONObject to parse the list of reviews from a JSON
            JSONObject reviewsJson = new JSONObject(json);

            // parsing the json string by getting the list of reviews results
            JSONArray reviewsJArray = reviewsJson.getJSONArray(JSON_RESULTS_KEY);
            // if the list is null, return null
            if(reviewsJArray==null) {
             return null;
            }

            // initializing the list of reviews
            reviews = new ArrayList<>();

            for (int i = 0; i < reviewsJArray.length(); i++) {
                // getting the current JSONObj in the list
                JSONObject reviewJSONObj = reviewsJArray.getJSONObject(i);

                // getting the movie's attributes
                long id = reviewJSONObj.optLong(JSON_ID_KEY, 0);
                String author = reviewJSONObj.optString(JSON_AUTHOR_KEY);
                String content = reviewJSONObj.optString(JSON_CONTENT_KEY);

                // creating the movie obj
                Review review = new  Review(id, author, content);
                Log.d(JSON_PARSING, review.toString());

                reviews.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the results: "+json);
        }
        return reviews;
    }

    //retrieving the list of reviews
    public static List<Cast> getCastsList(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        List<Cast> casts = null;
        try {
            // initializing a new JSONObject to parse the list of reviews from a JSON
            JSONObject reviewsJson = new JSONObject(json);

            // parsing the json string by getting the list of reviews results
            JSONArray castJArray = reviewsJson.getJSONArray(JSON_CAST_KEY);
            // if the list is null, return null
            if(castJArray==null) {
                return null;
            }

            // initializing the list of reviews
            casts = new ArrayList<>();

            for (int i = 0; i < castJArray.length(); i++) {
                // getting the current JSONObj in the list
                JSONObject reviewJSONObj = castJArray.getJSONObject(i);

                // getting the movie's attributes
                String name = reviewJSONObj.optString(JSON_ACTOR_NAME);
                String character = reviewJSONObj.optString(JSON_CHARACTER);
                String profilePicPath = reviewJSONObj.optString(JSON_PROFILE_PATH);

                // creating the movie obj
                Cast cast = new Cast(name, character, profilePicPath);
                Log.d(JSON_PARSING, cast.toString());

                casts.add(cast);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the results: "+json);
        }
        return casts;
    }

    //retrieving the list of reviews
    public static List<Trailer> getTrailersList(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        List<Trailer> trailers = null;
        try {
            // initializing a new JSONObject to parse the list of reviews from a JSON
            JSONObject reviewsJson = new JSONObject(json);

            // parsing the json string by getting the list of reviews results
            JSONArray trailersJArray = reviewsJson.getJSONArray(JSON_RESULTS_KEY);
            // if the list is null, return null
            if(trailersJArray==null) {
                return null;
            }

            // initializing the list of reviews
            trailers = new ArrayList<>();

            for (int i = 0; i < trailersJArray.length(); i++) {
                // getting the current JSONObj in the list
                JSONObject trailerJSONObj = trailersJArray.getJSONObject(i);

                // getting the movie's attributes
                long id = trailerJSONObj.optLong(JSON_ID_KEY, 0);
                String key = trailerJSONObj.optString(JSON_KEY_KEY);
                String name = trailerJSONObj.optString(JSON_NAME_KEY);
                String site = trailerJSONObj.optString(JSON_SITE_KEY);
                int size = trailerJSONObj.optInt(JSON_SIZE_KEY);
                String type = trailerJSONObj.optString(JSON_TYPE_KEY);

                // creating the movie obj
                Trailer trailer = new  Trailer(id, key, name, site, size, type);
                Log.d(JSON_PARSING, trailer.toString());

                trailers.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the results: "+json);
        }
        return trailers;
    }
}
