package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private ImageView ingredientsIv;
    private TextView mIngredientsTV;
    private TextView mDescriptionTV;
    private TextView mPlaceOfOrginTV;
    private TextView mAlsoKnownAsTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ingredientsIv = findViewById(R.id.image_iv);
        mAlsoKnownAsTV = findViewById(R.id.also_known_tv);
        mPlaceOfOrginTV = findViewById(R.id.origin_tv);
        mIngredientsTV = findViewById(R.id.ingredients_tv);
        mDescriptionTV = findViewById(R.id.description_tv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Log.d("Sandwich", json);
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    // According to the its name, this method should populate the UI. For this reason, I moved
    // the logic to populate the UI in it and I modified the method's signature in order to accept
    // a Sandwich Object. These improvements result in a more readable code (in my opinion).
    private void populateUI(Sandwich sandwich) {

        /*
         You could try to use these two methods as shown in the sample code below (from Picasso documentation):

         Picasso.with(context)
         .load(url)
         .placeholder(R.drawable.user_placeholder)
         .error(R.drawable.user_placeholder_error)
         .into(imageView);
         Using Picasso without error() might not cause any problem. However, when it comes to some other APIs
         (unfortunately, Spotify is one of them), the chance of fighting against some strange values could get higher.
         So that's why I strongly suggest you to use these two methods in your future projects.
         */

        Picasso.with(this)
                .load(sandwich.getImage())
                // image powered by Grace Baptist (http://gbchope.com/events-placeholder/)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(ingredientsIv);

        setTitle(sandwich.getMainName());

        // converting the list in a string where each word is delimited by comma
        String aka = TextUtils.join(", ", sandwich.getAlsoKnownAs());
        mAlsoKnownAsTV.setText(aka);

        mPlaceOfOrginTV.setText(sandwich.getPlaceOfOrigin());

        // converting the list in a string where each word is delimited by comma
        String ingredients = TextUtils.join("\n", sandwich.getIngredients());
        mIngredientsTV.setText(ingredients);

        mDescriptionTV.setText(sandwich.getDescription());
    }
}
