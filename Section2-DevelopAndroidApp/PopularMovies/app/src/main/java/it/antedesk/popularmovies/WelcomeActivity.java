package it.antedesk.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.popularmovies.adapter.MovieViewAdapter;
import it.antedesk.popularmovies.data.MovieContract.MovieEntry;
import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.utilities.JsonUtils;
import it.antedesk.popularmovies.utilities.NetworkUtils;

import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.API_KEY;
import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.MOVIE_TAG;
import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.SORT_CRITERIUM;
import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.SORT_CRITERIUM_POPULAR;
import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.SORT_CRITERIUM_TOP_RATED;

public class WelcomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MovieViewAdapter.MovieViewAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view)NavigationView navigationView;

    // ProgressBar variable to show and hide the progress bar
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.movies_list_rv)  RecyclerView mRecyclerView;
    private  MovieViewAdapter mMovieViewAdapter;
    @BindView(R.id.connection_error_layout)  LinearLayout mConnectionErrorLayout;

    // This number will uniquely identify our Loader and is chosen arbitrarily.
    private static final int MOVIES_LOADER = 22;
    private static final String MOVIES_LOADER_CRITERIUM = "load_criterium";

    private List<Movie> movies;
    private int menuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //start Loader for getting movies list
        getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);

        // creating a LinearLayoutManager
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, calculateNoOfColumns(this));

        // setting the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieViewAdapter = new MovieViewAdapter(this);
        mRecyclerView.setAdapter(mMovieViewAdapter);

        menuItemId = savedInstanceState!=null
                && savedInstanceState.containsKey(SORT_CRITERIUM) ? savedInstanceState.getInt(SORT_CRITERIUM) : 0;
        navigationView.getMenu().getItem(menuItemId).setChecked(true);
        navigationView.getMenu().performIdentifierAction(navigationView.getMenu().getItem(menuItemId).getItemId(), 0);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_popular_movie) {
            menuItemId = 0;
            if(NetworkUtils.isOnline(this)){
                loadMoviesData(SORT_CRITERIUM_POPULAR);
                Log.d(SORT_CRITERIUM, "Current criterium: "+SORT_CRITERIUM_POPULAR);
            } else{
                showErrorMessage();
            }
        } else if (id == R.id.nav_top_rated) {
            menuItemId = 1;
            if(NetworkUtils.isOnline(this)){
                loadMoviesData(SORT_CRITERIUM_TOP_RATED);
                Log.d(SORT_CRITERIUM, "Current criterium: "+SORT_CRITERIUM_TOP_RATED);
            } else{
                showErrorMessage();
            }
        } else if (id == R.id.nav_favorite) {
            menuItemId = 2;
            if(!NetworkUtils.isOnline(this)){
                mConnectionErrorLayout.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            loadMoviesData(null);
            Log.d(SORT_CRITERIUM, "Current criterium: "+"favorites");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        mMovieViewAdapter.setMoviesData(movies);
    }

    public void showErrorMessage(){
        mConnectionErrorLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving the current state of the spinner
        outState.putInt(SORT_CRITERIUM, menuItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Log.d(MOVIE_TAG, selectedMovie.toString());

        // create a new intent, add the selected movie, start the detail activity
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_TAG, selectedMovie);
        startActivity(intent);
    }
    /**
     * LOADER SECTION
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

                // getting the movies list
                movies = null;
                if (sortCriterium == null || TextUtils.isEmpty(sortCriterium)) {
                    movies = retrieveFavorites();
                } else {
                    URL moviesListRequestUrl = NetworkUtils.buildMovieListUrl(sortCriterium, API_KEY);
                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(moviesListRequestUrl);

                        movies = JsonUtils.getMovieList(jsonMovieResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    /**
     * returns a list of user's favorite movies
     * @return movies
     */
    private List<Movie> retrieveFavorites(){
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if(cursor != null && cursor.getCount() >= 1) {
            Log.d("Cursor", String.valueOf(cursor.getCount()));
            try {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                    String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
                    double voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVARAGE));
                    String overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                    long voteCount = cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT));
                    movies.add(
                            new Movie(id,title,releaseDate,posterPath,voteAverage,overview,voteCount)
                    );
                }
            } finally {
                cursor.close();
            }
            Log.d("ContentProvider", "isFavorite "+movies.size());
        }
        return movies;
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
}
