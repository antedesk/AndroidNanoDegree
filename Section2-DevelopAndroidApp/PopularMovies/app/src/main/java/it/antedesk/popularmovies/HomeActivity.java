package it.antedesk.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.utilities.JsonUtils;
import it.antedesk.popularmovies.utilities.NetworkUtils;

public class HomeActivity extends AppCompatActivity  implements OnItemSelectedListener {

    // ProgressBar variable to show and hide the progress bar
    private ProgressBar mLoadingIndicator;
    private Spinner mCriteriaSpinner;
    private LinearLayout mConnectionErrorLayout;
    private ScrollView mMoviesListScrollView;

    // the movie API key loaded from BuildConfig
    private static final String API_KEY = BuildConfig.API_KEY;

    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mConnectionErrorLayout = findViewById(R.id.connection_error_layout);
        mMoviesListScrollView =  findViewById(R.id.movies_list_sv);
        mCriteriaSpinner = findViewById(R.id.spinner_sort_criteria);

        if(NetworkUtils.isOnline(this)){
            initilizeSortingCriteriaSpinner();

            String sortCriteria = "popular";
            loadMoviesData(sortCriteria);
        } else{
            mConnectionErrorLayout.setVisibility(View.VISIBLE);
            mMoviesListScrollView.setVisibility(View.INVISIBLE);
        }

    }

    private void initilizeSortingCriteriaSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.criteria_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCriteriaSpinner.setAdapter(adapter);

        Log.d("sortCriteria", "inizializzato");
    }

    private void loadMoviesData(String sortCriteria) {
        new FetchMoviesTask().execute(sortCriteria);
    }

     void launchDetailActivity(View view) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Movie movie = movies.get(0);
        Log.d(MovieDetailActivity.MOVIE_TAG, movie.toString());
        intent.putExtra(MovieDetailActivity.MOVIE_TAG, movie);
        startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString("sortCriteria", "INSERT SORT CRITIRIA");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sortCriteria = mCriteriaSpinner.getItemAtPosition(position).toString();
        Log.d("sortCriteria", "Current Pos: "+position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Log.d("sortCriteria", "ANY CLICK");
    }

    /**
     * In order to get the list of popular/top_rated movies, I define an AsyncTask class to load
     * the movies list in background.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

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
            movies = null;
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

            // DO SOMETHING
            /*
            if (movies != null) {
                showWeatherDataView();
            } else {
                // COMPLETED (10) If the weather data was null, show the error message
                showErrorMessage();
            }
            */
        }
    }
}

