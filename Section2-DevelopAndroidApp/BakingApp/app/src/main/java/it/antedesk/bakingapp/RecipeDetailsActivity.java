package it.antedesk.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import it.antedesk.bakingapp.fragment.StepFragment;
import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.model.Step;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_STEPS;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_RECIPE;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_STEPS;

public class RecipeDetailsActivity extends AppCompatActivity implements StepFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Intent intent = getIntent();
        // checking if it is null, if so close the activity
        if (intent == null) {
            closeOnError();
        }
        // retriving the movie form intent
        final Recipe mRecipe = intent.getParcelableExtra(SELECTED_RECIPE);
        if (mRecipe == null) {
            closeOnError();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        StepFragment stepFragment = new StepFragment();
        Bundle stepsFragBundle = new Bundle();
        stepsFragBundle.putParcelableArrayList(RECIPES_STEPS, (ArrayList<Step>) mRecipe.getSteps());
        stepFragment.setArguments(stepsFragBundle);
        fragmentManager.beginTransaction().add(R.id.steps_container, stepFragment).commit();


    }

    private void closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListFragmentInteraction(Step item) {
        Log.d("tag", item.toString());
    }

}
