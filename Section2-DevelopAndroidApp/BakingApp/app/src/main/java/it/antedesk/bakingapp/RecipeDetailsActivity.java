package it.antedesk.bakingapp;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import it.antedesk.bakingapp.fragment.StepDetailsFragment;
import it.antedesk.bakingapp.fragment.StepFragment;
import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.model.Step;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.CURRENT_STEP;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_STEPS;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_RECIPE;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_STEP;

public class RecipeDetailsActivity extends AppCompatActivity implements StepFragment.OnListFragmentInteractionListener {
    public static final String STEP_MASTER_FRAGMENT = "STEP_MASTER_FRAGMENT";
    public static final String STEP_DETAIL_FRAGMENT = "STEP_DETAIL_FRAGMENT";

    Recipe mRecipe;
    Step currentStep;
    String mLastSinglePaneFragment;
    boolean mDualPane = false;
    final String lastSinglePaneFragment = "lastSinglePaneFragment";
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
        mRecipe = intent.getParcelableExtra(SELECTED_RECIPE);
        if (mRecipe == null) {
            closeOnError();
        }

        mDualPane = findViewById(R.id.steps_details_container)!=null;

        if (savedInstanceState!=null) {
            mLastSinglePaneFragment = savedInstanceState.getString(lastSinglePaneFragment);
            currentStep = savedInstanceState.getParcelable(CURRENT_STEP);
        }

        FragmentManager fm = getSupportFragmentManager();

        if (!mDualPane && fm.findFragmentById(R.id.steps_list_container)==null) {
            StepFragment masterFragment = getDetatchedMasterFragment(false);
            fm.beginTransaction().add(R.id.steps_container, masterFragment, STEP_MASTER_FRAGMENT).commit();
            if (mLastSinglePaneFragment==STEP_DETAIL_FRAGMENT) {
                openSinglePaneDetailFragment();
            }
        }
        if (mDualPane && fm.findFragmentById(R.id.steps_list_container)==null) {
            StepFragment masterFragment = getDetatchedMasterFragment(true);
            fm.beginTransaction().add(R.id.steps_list_container, masterFragment, STEP_MASTER_FRAGMENT).commit();
        }
        if (mDualPane && fm.findFragmentById(R.id.steps_details_container)==null) {
            StepDetailsFragment detailFragment = getDetatchedDetailFragment();
            fm.beginTransaction().add(R.id.steps_details_container, detailFragment, STEP_DETAIL_FRAGMENT).commit();
        }
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!mDualPane && currentStep!=null) {
            outState.putString(lastSinglePaneFragment, STEP_DETAIL_FRAGMENT);
            outState.putParcelable(CURRENT_STEP, currentStep);
        }
    }

    @Override
    public void onListFragmentInteraction(Step item) {
        currentStep = item;
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailsFragment stepFragment = StepDetailsFragment.newInstance(item);

        fragmentManager.beginTransaction().replace(
                mDualPane ? R.id.steps_details_container : R.id.steps_container, stepFragment).commit();
    }

    private StepFragment getDetatchedMasterFragment(boolean popBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        StepFragment masterFragment = (StepFragment) getSupportFragmentManager().findFragmentByTag(STEP_MASTER_FRAGMENT);
        if (masterFragment == null) {
            masterFragment = new StepFragment();
            Bundle stepsFragBundle = new Bundle();
            stepsFragBundle.putParcelableArrayList(RECIPES_STEPS, (ArrayList<Step>) mRecipe.getSteps());
            masterFragment.setArguments(stepsFragBundle);
        } else {
            if (popBackStack) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fm.beginTransaction().remove(masterFragment).commit();
            fm.executePendingTransactions();
        }
        return masterFragment;
    }

    private StepDetailsFragment getDetatchedDetailFragment() {
        FragmentManager fm = getSupportFragmentManager();
        StepDetailsFragment detailFragment = (StepDetailsFragment) getSupportFragmentManager().findFragmentByTag(STEP_DETAIL_FRAGMENT);
        if (detailFragment == null) {
            currentStep = currentStep==null? mRecipe.getSteps().get(0): currentStep;
            detailFragment = StepDetailsFragment.newInstance(currentStep);
        } else {
            fm.beginTransaction().remove(detailFragment).commit();
            fm.executePendingTransactions();
        }
        return detailFragment;
    }

    private void openSinglePaneDetailFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        StepDetailsFragment detailFragment = getDetatchedDetailFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.steps_container, detailFragment, STEP_DETAIL_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
