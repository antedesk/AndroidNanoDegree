package it.antedesk.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.adapter.MovieImageAdapter;
import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.utilities.JsonUtils;
import it.antedesk.popularmovies.utilities.NetworkUtils;

import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.*;

public class HomeActivity extends AppCompatActivity implements OnItemSelectedListener,
        AdapterView.OnItemClickListener, LoaderCallbacks<List<Movie>> {

    // ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;
    private Spinner mCriteriaSpinner;
    private GridView mMoviesGridView;
    private LinearLayout mConnectionErrorLayout;

    // This number will uniquely identify our Loader and is chosen arbitrarily.
    private static final int MOVIES_LOADER = 22;
    private static final String MOVIES_LOADER_CRITERIUM = "load_criterium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mCriteriaSpinner = findViewById(R.id.spinner_sort_criteria);
        mMoviesGridView = findViewById(R.id.movies_list_sv);
        mConnectionErrorLayout = findViewById(R.id.connection_error_layout);

        if(NetworkUtils.isOnline(this)){

            getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);

            initilizeSortingCriteriaSpinner();
            if(savedInstanceState!=null && savedInstanceState.containsKey(SORT_CRITERIUM)){
                mCriteriaSpinner.setSelection(
                        savedInstanceState.getInt(SORT_CRITERIUM, 0));
            }
            String sortCriterium = mCriteriaSpinner.getSelectedItem().toString();
            Log.d(SORT_CRITERIUM, "Current criterium: "+sortCriterium);
        } else{
            showErrorMessage();
        }
    }

    /**
     * This method initializes the spinner to allow the end-user to select the critiria to
     * sort the movies list.
     */
    private void initilizeSortingCriteriaSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.criteria_array, R.layout.spinner_row);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCriteriaSpinner.setAdapter(adapter);
        mCriteriaSpinner.setOnItemSelectedListener(this);
    }

    /**
     * This method launches the AsyncTask to retrieve the data via the APIs defined in NetworkUtils
     * @param sortCriterium is the criteria used to perform the API request and get a list of
     *                      sorted movies according to the given criteria
     */
    private void loadMoviesData(String sortCriterium) {
        Bundle criteriumBundle = new Bundle();
        criteriumBundle.putString(MOVIES_LOADER_CRITERIUM, sortCriterium);
        LoaderManager loaderManager = getSupportLoaderManager();

        // Get the Loader by calling getLoader and passing the ID we specified
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIES_LOADER);
        // If the Loader was null, initialize it. Else, restart it.
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER, criteriumBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, criteriumBundle, this);
        }
    }

    private void showMoviesDataView(List<Movie> movies){
        mMoviesGridView.setAdapter(new MovieImageAdapter(this, movies));
        mMoviesGridView.setOnItemClickListener(this);
    }

    public void showErrorMessage(){
        mConnectionErrorLayout.setVisibility(View.VISIBLE);
        mMoviesGridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving the current state of the spinner
        outState.putInt(SORT_CRITERIUM, mCriteriaSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * This method loads a list of movies according to the criterium selected
     * by the end-user through the spinner.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // getting the current criterium
        String sortCriterium = parent.getItemAtPosition(position).toString();
        loadMoviesData(sortCriterium);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* do nothing*/ }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the selected movie
        Movie movie = (Movie) mMoviesGridView.getItemAtPosition(position);
        Log.d(MOVIE_TAG, movie.toString());

        // create a new intent, add the selected movie, start the detail activity
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_TAG, movie);
        startActivity(intent);
    }

    /**
     * In order to get the list of popular/top_rated movies, I define an AsyncTaskLoader class
     * to load the movies list in background.
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        // return a new AsyncTaskLoader<List<Movie>>
        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }

                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                String sortCriterium = args.getString(MOVIES_LOADER_CRITERIUM);
                if (sortCriterium == null || TextUtils.isEmpty(sortCriterium)) {
                    return null;
                }

                // getting the sorted movies list
                List<Movie> movies = null;
                URL moviesListRequestUrl = NetworkUtils.buildMovieListUrl(sortCriterium, API_KEY);
                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(moviesListRequestUrl);

                    movies = (ArrayList<Movie>) JsonUtils.getMovieList(jsonMovieResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        // hiding the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data != null) {
            showMoviesDataView(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) { /* do nothing */ }

}

