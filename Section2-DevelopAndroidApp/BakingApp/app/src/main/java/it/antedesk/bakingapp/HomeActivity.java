package it.antedesk.bakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.bakingapp.adapter.RecipeViewAdapter;
import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.utils.NetworkUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.HOME_ACTIVITY_LOADING;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_DATASOURCE_URL;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_RECIPE;

public class HomeActivity extends AppCompatActivity  implements RecipeViewAdapter.RecipeViewAdapterOnClickHandler{

    private ProgressDialog mProgressDialog;
    private List<Recipe> recipes;
    private OkHttpClient client = new OkHttpClient();

    @BindView(R.id.recipes_rv) RecyclerView mRecipesRecyclerView;
    private RecipeViewAdapter mRecipesViewAdapter;

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        int numberOfColumns =
                getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT ? 1 : 3;

        // creating a GridLayoutManager
        GridLayoutManager mLayoutManager
                = new GridLayoutManager(this, numberOfColumns);

        // setting the mlayoutManager on mRecyclerView
        mRecipesRecyclerView.setLayoutManager(mLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipesViewAdapter = new RecipeViewAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipesViewAdapter);
        if(savedInstanceState!=null
                && savedInstanceState.containsKey(LIST_STATE)) {
            mListState = savedInstanceState.getParcelable(LIST_STATE);
        }
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
     * Retrives the recipes from the json file by using okhttp lib with async call
     * @throws Exception
     */
    public void getRecipes() throws Exception {
        Request request = new Request.Builder()
                .url(RECIPES_DATASOURCE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, IOException e) {
                hideProgressDialog();
                e.printStackTrace();
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String responseJson = responseBody.string();
                        Type recipesType = new TypeToken<List<Recipe>>() {}.getType();
                        recipes = new Gson().fromJson(responseJson, recipesType);
                        Log.d(HOME_ACTIVITY_LOADING, recipes.get(0).toString());
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showRecipes(recipes);
                            }
                        });

                    }
                    hideProgressDialog();
                } catch (IOException e){
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    private void loadRecipesData() {
        try {
            showProgressDialog();
            getRecipes();
        } catch (Exception e) {
            showErrorMessage();
        }
    }

    private void showRecipes(List<Recipe> data) {
        mRecipesViewAdapter.setRecipesData(data);
        if (mListState!=null)
            mRecipesRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
    }

    //TODO Add error layout when network is not working
    public void showErrorMessage(){
     //   mConnectionErrorLayout.setVisibility(View.VISIBLE);
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Log.d(HOME_ACTIVITY_LOADING, selectedRecipe.toString());
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(SELECTED_RECIPE, selectedRecipe);
        startActivity(intent);
    }
}
