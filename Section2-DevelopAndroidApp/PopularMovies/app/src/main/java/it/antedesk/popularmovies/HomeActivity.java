package it.antedesk.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class HomeActivity extends AppCompatActivity implements OnItemSelectedListener, AdapterView.OnItemClickListener {

    // ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;
    private Spinner mCriteriaSpinner;
    private GridView mMoviesGridView;
    private LinearLayout mConnectionErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mCriteriaSpinner = findViewById(R.id.spinner_sort_criteria);
        mMoviesGridView = findViewById(R.id.movies_list_sv);
        mConnectionErrorLayout = findViewById(R.id.connection_error_layout);

        if(NetworkUtils.isOnline(this)){
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
     * @param sortCriteria is the criteria used to perform the API request and get a list of
     *                     sorted movies according to the given criteria
     */
    private void loadMoviesData(String sortCriteria) {
        new FetchMoviesTask().execute(sortCriteria);
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
     * This overrided method loads a list of movies according to the criterium selected
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
     * In order to get the list of popular/top_rated movies, I define an AsyncTask class to load
     * the movies list in background.
     */
    private class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showing the loading indicator
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            // checking if the params are passed to the AsyncTask
            if (params.length == 0) {
                return null;
            }

            // retriving the sort modality chosen by the user
            String sortMode = params[0];
            URL moviesListRequestUrl = NetworkUtils.buildMovieListUrl(sortMode, API_KEY);
            // getting the sorted movies list
            List<Movie> movies = null;
            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesListRequestUrl);

                movies = (ArrayList<Movie>) JsonUtils.getMovieList(jsonMovieResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            // hiding the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movies != null) {
                showMoviesDataView(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}

