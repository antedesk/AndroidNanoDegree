package com.udacity.sandwichclub.utils;

import android.util.Log;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JsonUtils {

    private static String JSON_PARSING = "JSON_PARSING";

    public static Sandwich parseSandwichJson(String json) {
        //check if the json string is empty
        if(json==null) {
            Log.d( JSON_PARSING, "Empty json string");
            return null;
        }

        Sandwich sandwich = null;
        try {
            // initializing a new JSONObject to parse the sandwich json string
            JSONObject sandwichJson = new JSONObject(json);

            // parsing the json string
            String mainName = sandwichJson.getJSONObject("name").getString("mainName");

            // managing the list of aka
            List<String> alsoKnownAs = new ArrayList<>();
            JSONArray akaJArray = sandwichJson.getJSONObject("name")
                                                .getJSONArray("alsoKnownAs");
            if(akaJArray!=null) // if the list is not null, create the aka list
                for(int i=0; i<akaJArray.length(); i++)
                    alsoKnownAs.add(akaJArray.getString(i));

            String placeOfOrigin = sandwichJson.getString("placeOfOrigin");

            String description = sandwichJson.getString("description");

            String image = sandwichJson.getString("image");

            // managing the list of ingredients
            List<String> ingredients = new ArrayList<>();
            JSONArray ingredientsJArray = sandwichJson.getJSONArray("ingredients");
            if(ingredientsJArray!=null) // if the list is not null, create the aka list
                for(int i=0; i<ingredientsJArray.length(); i++)
                    ingredients.add(ingredientsJArray.getString(i));

            // Initializing a new Sandwich object
            sandwich = new Sandwich(mainName,
                                    alsoKnownAs,
                                    placeOfOrigin,
                                    description,
                                    image,
                                    ingredients);

            Log.d(JSON_PARSING, sandwich.getMainName().toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(JSON_PARSING, "Failed in parsing the sandwich: "+json);
        }
        return sandwich;
    }
}
