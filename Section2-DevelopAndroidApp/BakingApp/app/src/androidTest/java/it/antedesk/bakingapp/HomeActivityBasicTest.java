package it.antedesk.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Antedesk on 19/06/2018.
 */

@RunWith(AndroidJUnit4.class)
public class HomeActivityBasicTest {
    public static final String  STEP_TITLE= "Show Ingredients";
    public static final String RECIPE_NUTELLA_PIE = "Nutella Pie";
    public static final String RECIPE_BROWNIES = "Brownies";
    private IdlingResource mIdlingResource;

    @Rule public ActivityTestRule<HomeActivity> mActivityTestRule =
            new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);

    }

    @Test
    public void clickOnRecipeCardNutellaPie_OpensRecipeDetailActivity(){
        onView(withId(R.id.recipes_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.step_title)).check(matches(withText(STEP_TITLE)));
        //onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_NUTELLA_PIE)));
    }

    @Test
    public void clickOnRecipeCardBrownies_OpensRecipeDetailActivity(){
        onView(withId(R.id.recipes_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.step_title)).check(matches(withText(STEP_TITLE)));
        //onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_BROWNIES)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
