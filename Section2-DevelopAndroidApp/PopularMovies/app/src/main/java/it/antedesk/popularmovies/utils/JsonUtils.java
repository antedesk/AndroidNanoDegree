package it.antedesk.popularmovies.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.model.Movie;

/**
 * Created by Antedesk on 01/03/2018.
 * This class is inspired to the one developed in the Sandwich club project and
 * it allows me to parse the JSON data retrived through the API.
 */

public class JsonUtils {

    // For log
    private static String JSON_PARSING = "JSON_PARSING";

    // JSON key for parsing the retrieved JSON
    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_MAINNAME_KEY = "mainName";
    public static final String JSON_AKA_KEY = "alsoKnownAs";
    public static final String JSON_PLACE_ORIGIN_KEY = "placeOfOrigin";
    public static final String JSON_IMAGES_KEY = "image";
    public static final String JSON_DESCRIPTION_KEY = "description";
    public static final String JSON_INGREDIENTS_KEY = "ingredients";

    public static Movie parseSandwichJson(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        Movie movie = null;
        try {
            // initializing a new JSONObject to parse the sandwich json string
 /*           JSONObject sandwichJson = new JSONObject(json);

            // parsing the json string
            String mainName = sandwichJson.getJSONObject(JSON_NAME_KEY).getString(JSON_MAINNAME_KEY);

            // managing the list of aka
            List<String> alsoKnownAs = new ArrayList<>();
            JSONArray akaJArray = sandwichJson.getJSONObject(JSON_NAME_KEY)
                    .getJSONArray(JSON_AKA_KEY);
            if(akaJArray!=null) // if the list is not null, create the aka list
                for(int i=0; i<akaJArray.length(); i++)
                    alsoKnownAs.add(akaJArray.getString(i));

            // REVIEWER: I would suggest instead of getString() you should use optString()
            String placeOfOrigin = sandwichJson.optString(JSON_PLACE_ORIGIN_KEY);

            String description = sandwichJson.optString(JSON_DESCRIPTION_KEY);

            String image = sandwichJson.optString(JSON_IMAGES_KEY);

            // managing the list of ingredients
            List<String> ingredients = new ArrayList<>();
            JSONArray ingredientsJArray = sandwichJson.getJSONArray(JSON_INGREDIENTS_KEY);
            if(ingredientsJArray!=null) // if the list is not null, create the aka list
                for(int i=0; i<ingredientsJArray.length(); i++)
                    ingredients.add(ingredientsJArray.getString(i));
*/
            // Initializing a new Sandwich object
            movie = new Movie();

            Log.d(JSON_PARSING, movie.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the sandwich: "+json);
        }
        return movie;
    }
}
