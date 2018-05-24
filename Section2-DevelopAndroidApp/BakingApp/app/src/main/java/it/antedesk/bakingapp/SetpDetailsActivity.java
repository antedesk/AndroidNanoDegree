package it.antedesk.bakingapp;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import it.antedesk.bakingapp.fragment.StepDetailsFragment;
import it.antedesk.bakingapp.fragment.StepFragment;
import it.antedesk.bakingapp.model.Step;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_STEPS;

public class SetpDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setp_details);

        FragmentManager fragmentManager = getSupportFragmentManager();


        StepDetailsFragment stepFragment = new StepDetailsFragment();
//        Bundle stepsFragBundle = new Bundle();
//        stepsFragBundle.putParcelableArrayList(RECIPES_STEPS, (ArrayList<Step>) mRecipe.getSteps());
//        stepFragment.setArguments(stepsFragBundle);
        fragmentManager.beginTransaction().add(R.id.steps_container, stepFragment).commit();
    }

}
