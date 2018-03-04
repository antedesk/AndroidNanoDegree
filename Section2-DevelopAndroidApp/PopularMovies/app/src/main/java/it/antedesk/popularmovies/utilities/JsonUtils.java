package it.antedesk.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.model.Movie;

/**
 * Created by Antedesk on 01/03/2018.
 * This class is inspired to the one implemented in the Sandwich Club project and
 * it allows parsing the JSON data retrived through the API.
 */

public class JsonUtils {

    // For log
    private static String JSON_PARSING = "JSON_PARSING";

    // JSON key for parsing the retrieved JSON
    public static final String JSON_RESULTS_KEY = "results";
    public static final String JSON_ID_KEY = "id";
    public static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    public static final String JSON_TITLE_KEY = "title";
    public static final String JSON_POSTER_PATH_KEY = "poster_path";
    public static final String JSON_OVERVIEW_KEY = "overview";
    public static final String JSON_RELEASE_DATE_KEY = "release_date";

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
                    long id = movieJSONObj.optInt(JSON_ID_KEY, 0);
                    String title = movieJSONObj.optString(JSON_TITLE_KEY);
                    String releaseDate = movieJSONObj.optString(JSON_RELEASE_DATE_KEY);
                    String posterPath = movieJSONObj.optString(JSON_POSTER_PATH_KEY);
                    double voteAvarage = movieJSONObj.optDouble(JSON_VOTE_AVERAGE_KEY,0);
                    String overview = movieJSONObj.optString(JSON_OVERVIEW_KEY);

                    // creating the movie obj
                    Movie movie = new  Movie(id,
                                            title,
                                            releaseDate,
                                            posterPath,
                                            voteAvarage,
                                            overview);
                    Log.d(JSON_PARSING, movie.toString());

                    movies.add(movie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the results: "+json);
        }
        return movies;
    }
}
