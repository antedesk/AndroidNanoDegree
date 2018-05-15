package it.antedesk.bakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.utils.NetworkUtils;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.HOME_ACTIVITY_LOADING;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_DATASOURCE_URL;

public class HomeActivity extends AppCompatActivity implements LoaderCallbacks<List<Recipe>> {

    private ProgressDialog mProgressDialog;
    private List<Recipe> recipes;
    private static final int RECIPES_LOADER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(NetworkUtils.isOnline(this)){
            loadRecipesData();
        } else{
            showErrorMessage();
        }

    }

    /**
     * Calculates the number of columns for the gridlayout
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * This method launches the AsyncTask to retrieve the data via the APIs defined in NetworkUtils
     */
    private void loadRecipesData() {
        LoaderManager loaderManager = getSupportLoaderManager();

        // Get the Loader by calling getLoader and passing the ID we specified
        Loader<String> recipesLoader = loaderManager.getLoader(RECIPES_LOADER);
        // If the Loader was null, initialize it. Else, restart it.
        if (recipesLoader == null) {
            loaderManager.initLoader(RECIPES_LOADER, null,this);
        } else {
            loaderManager.restartLoader(RECIPES_LOADER, null,this);
        }
    }

    private void showRecipesDataView(List<Recipe> data) {
    }

    public void showErrorMessage(){
     //   mConnectionErrorLayout.setVisibility(View.VISIBLE);
     //   mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, final @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Recipe>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                showProgressDialog();
                forceLoad();
            }

            @Nullable
            @Override
            public List<Recipe> loadInBackground() {
                recipes = null;
                String results = NetworkUtils.getHttpResponse(RECIPES_DATASOURCE_URL);
                Log.d(HOME_ACTIVITY_LOADING, results);
                if(results!= null && !results.isEmpty()) {
                    Type recipesType = new TypeToken<List<Recipe>>() {}.getType();
                    recipes = new Gson().fromJson(results, recipesType);
                }
                return recipes;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> data) {
        hideProgressDialog();
        if (data != null) {

            showRecipesDataView(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) { /* do nothing */ }
}
