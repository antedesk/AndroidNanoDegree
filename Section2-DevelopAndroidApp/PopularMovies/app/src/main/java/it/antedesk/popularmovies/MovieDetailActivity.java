package it.antedesk.popularmovies;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.ReviewDialog;
import it.antedesk.popularmovies.adapter.ReviewViewAdapter;
import it.antedesk.popularmovies.adapter.TrailerViewAdapter;
import it.antedesk.popularmovies.data.MovieContract.MovieEntry;
import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.model.Review;
import it.antedesk.popularmovies.model.Trailer;
import it.antedesk.popularmovies.utilities.JsonUtils;
import it.antedesk.popularmovies.utilities.NetworkUtils;

import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.*;

public class MovieDetailActivity extends AppCompatActivity implements LoaderCallbacks<Movie>,
        ReviewViewAdapter.ReviewViewAdapterOnClickHandler,
        TrailerViewAdapter.TrailerViewAdapterOnClickHandler,
        YouTubePlayer.OnInitializedListener {

    private static final String DIALOG_FRAGMENT = "DIALOG_FRAGMENT";

    private static final String REVIEW_LIST_STATE = "revListState";
    private static final String TRAILER_LIST_STATE = "trailerListState";
    private static final String SCROLL_POSITION = "SCROLL_POSITION";

    // UI elements
    @BindView(R.id.poster_iv) ImageView mPosterIv;
    @BindView(R.id.release_date_tv) TextView mReleaseDateTv;
    @BindView(R.id.vote_average_tv) TextView mRatingTv;
    @BindView(R.id.overview_tv) TextView mPlotSynopsisTv;
    @BindView(R.id.favorite_iv) ImageView mFavoriteIv;
    @BindView(R.id.scroll) NestedScrollView mScroll;
    @BindView(R.id.reviews_list_rv) RecyclerView mReviewsRecyclerView;
    private ReviewViewAdapter mReviewViewAdapter;
    @BindView(R.id.trailers_list_rv) RecyclerView mTrailerRecyclerView;
    private TrailerViewAdapter mTrailerViewAdapter;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    private YouTubePlayerSupportFragment mYuoTubePlayerFrag;
    private YouTubePlayer mYouTubePlayer;

    // This number will uniquely identify our Loader and is chosen arbitrarily.
    private static final int MOVIES_LOADER = 22;
    private static final int RECOVERY_REQUEST = 1;

    private boolean isFavorite = false;

    private Movie movie;
    private int[] scrollPosition = null;

    private Parcelable mTrailerListState = null;
    private Parcelable mReviewListState  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // finding UI elements
        mYuoTubePlayerFrag =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // setting the layoutManager on mRecyclerView
        mReviewsRecyclerView.setLayoutManager(layoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewViewAdapter = new ReviewViewAdapter(this);

        LinearLayoutManager trailerLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // setting the layoutManager on mRecyclerView
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerViewAdapter = new TrailerViewAdapter(this);

        mReviewsRecyclerView.setAdapter(mReviewViewAdapter);
        mTrailerRecyclerView.setAdapter(mTrailerViewAdapter);
        if(savedInstanceState!=null) {
            if (savedInstanceState.containsKey(REVIEW_LIST_STATE)){
                mReviewListState = savedInstanceState.getParcelable(REVIEW_LIST_STATE);
            }
            if (savedInstanceState.containsKey(TRAILER_LIST_STATE)) {
                mTrailerListState = savedInstanceState.getParcelable(TRAILER_LIST_STATE);
            }
            if (savedInstanceState.containsKey(SCROLL_POSITION)) {
                scrollPosition = savedInstanceState.getIntArray(SCROLL_POSITION);
            }
        }
        // Checking the internet connnection.
        if(!NetworkUtils.isOnline(this)){
            closeOnError();
        }
        // getting the intent to extract the data
        Intent intent = getIntent();
        // checking if it is null, if so close the activity
        if (intent == null) {
            closeOnError();
        }
        // retriving the movie form intent
        final Movie mMovie = intent.getParcelableExtra(MOVIE_TAG);
        if (mMovie == null) {
            closeOnError();
        }

        loadAdditionalInfo(mMovie);

        final CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar_layout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    findViewById(R.id.movie_toolbar).setVisibility(View.INVISIBLE);
                    mCollapsingToolbarLayout.setTitle("");
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    findViewById(R.id.movie_toolbar).setVisibility(View.VISIBLE);
                    mCollapsingToolbarLayout.setTitle(mMovie.getTitle());
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mReviewListState = mReviewsRecyclerView.getLayoutManager().onSaveInstanceState();
        mTrailerListState = mTrailerRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(REVIEW_LIST_STATE, mReviewListState);
        outState.putParcelable(TRAILER_LIST_STATE, mTrailerListState);

        outState.putIntArray(SCROLL_POSITION,
                new int[]{ mScroll.getScrollX(), mScroll.getScrollY()});

    }

    private void loadAdditionalInfo(Movie movie) {
        // creating a new bundle and adding the current movie's info
        Bundle movieBundle = new Bundle();
        movieBundle.putParcelable(MOVIE_TAG, movie);
        // initialize loaderManager
        LoaderManager loaderManager = getSupportLoaderManager();
        // Get the Loader by calling getLoader and passing the ID we specified
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIES_LOADER);
        // If the Loader was null, initialize it. Else, restart it.
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER, movieBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, movieBundle, this);
        }
    }

    // This method is used to populate the UI by using the given movie
    private void populateUI(Movie movie) {
        String imageURL = IMAGE_URL+SIZE_W185+movie.getPosterPath();
        Picasso.with(this)
                .load(imageURL)
                // image powered by Grace Baptist (http://gbchope.com/events-placeholder/)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(mPosterIv);
        if(isFavorite) mFavoriteIv.setImageResource(R.mipmap.star);
        mReleaseDateTv.setText(movie.getReleaseDate());
        mRatingTv.setText(String.format("%s / 10", movie.getVoteAverage()));
        mPlotSynopsisTv.setText(movie.getOverview());

        if (scrollPosition != null)
            mScroll.post(new Runnable() {
                public void run() {
                    mScroll.scrollTo(scrollPosition[0], scrollPosition[1]);
                }
            });
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {
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
            public Movie loadInBackground() {
                Movie movie = args.getParcelable(MOVIE_TAG);

                URL trailersListRequestUrl = NetworkUtils.buildItemsListUrl(movie.getId(),VIDEOS,API_KEY);
                URL reviewsListRequestUrl = NetworkUtils.buildItemsListUrl(movie.getId(),REVIEWS,API_KEY);
                //URL castsListRequestUrl = NetworkUtils.buildItemsListUrl(movie.getId(),CASTS,API_KEY);
                try {
                    String jsonTrailersResponse = NetworkUtils
                            .getResponseFromHttpUrl(trailersListRequestUrl);
                    String jsonReviewsResponse = NetworkUtils
                            .getResponseFromHttpUrl(reviewsListRequestUrl);
                    //String jsonCastsResponse = NetworkUtils
                    //        .getResponseFromHttpUrl(castsListRequestUrl);

                    List<Trailer> trailers = JsonUtils.getTrailersList(jsonTrailersResponse);
                    // sort the list of trailer by desc Type. This allows app to display the latest trailer
                    Collections.sort(trailers, new Comparator<Trailer>(){
                        public int compare(Trailer obj1, Trailer obj2) {
                            return obj2.getType().compareToIgnoreCase(obj1.getType());
                        }
                    });

                    List<Review> reviews = JsonUtils.getReviewsList(jsonReviewsResponse);
                    //List<Cast> casts = JsonUtils.getCastsList(jsonCastsResponse);
                    movie.setTrailers(trailers);
                    movie.setReviews(reviews);
                    //movie.setCasts(casts);

                    //check if the current movie is a favorite one or not
                    isFavorite = isFavoriteMovie(movie.getId());
                    Log.d("isFavorite", ""+isFavorite);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return movie;
            }
        };
    }

    private void showMoviesDataView(Movie movie){
        populateUI(movie);
        if(!NetworkUtils.isOnline(this)){
            mTrailerRecyclerView.setVisibility(View.INVISIBLE);
            mReviewsRecyclerView.setVisibility(View.INVISIBLE);
            findViewById(R.id.separator2_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.separator3_view).setVisibility(View.INVISIBLE);
        } else if(movie.getTrailers().size() == 0){
            mReviewViewAdapter.setReviewsData(movie.getReviews());
            findViewById(R.id.separator3_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.trailers_list_rv).setVisibility(View.INVISIBLE);
        } else if(movie.getReviews().size() ==0){
            mTrailerViewAdapter.setTrailersData(movie.getTrailers());
            findViewById(R.id.separator3_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.reviews_list_rv).setVisibility(View.INVISIBLE);
        } else{
            mReviewViewAdapter.setReviewsData(movie.getReviews());
            mTrailerViewAdapter.setTrailersData(movie.getTrailers());
        }

        if (mReviewListState!=null)
            mReviewsRecyclerView.getLayoutManager().onRestoreInstanceState(mReviewListState);

        if (mTrailerListState!=null)
            mTrailerRecyclerView.getLayoutManager().onRestoreInstanceState(mTrailerListState);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        // hiding the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            movie = data;
            showMoviesDataView(data);
            mYuoTubePlayerFrag.initialize(YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) { /*DO NOTHING*/ }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
                                        boolean wasRestored) {
        if (!wasRestored) {
            mYouTubePlayer = youTubePlayer;
            if(movie!=null && movie.getTrailers()!=null && !movie.getTrailers().isEmpty())
                youTubePlayer.cueVideo(movie.getTrailers().get(0).getKey());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_REQUEST) {
            getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        }
    }

    @Override
    public void onClick(Review selectedReview) {
        Log.d(MOVIE_TAG, selectedReview.toString());
        FragmentManager fm = getSupportFragmentManager();
        ReviewDialog editNameDialogFragment =
                ReviewDialog.newInstance(selectedReview.getAuthor(), selectedReview.getContent());
        editNameDialogFragment.show(fm, DIALOG_FRAGMENT);
    }

    @Override
    public void onClick(Trailer selectedTrailer) {
        Log.d(MOVIE_TAG, selectedTrailer.toString());
        if (mYuoTubePlayerFrag != null && mYouTubePlayer != null) {
            //load the selected video
            mYouTubePlayer.cueVideo(selectedTrailer.getKey());

            mScroll.scrollTo(0, 0);;
            AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
            appBarLayout.setExpanded(true);
        }
    }

    private boolean addFavoriteMovie(Movie movie){
        boolean added = true;
        ContentValues cv =  new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(MovieEntry.COLUMN_VOTE_AVARAGE, movie.getVoteAverage());
        cv.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

        Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, cv);
        if(uri == null) {
            added = false;
            String error = getString(R.string.add_favorite_error);
            Toast.makeText(getBaseContext(),error, Toast.LENGTH_LONG).show();
        }
        Log.d("ContentProvider", "added "+added);
        return added;
    }

    private boolean removeFavoriteMovie(long movieId){
        boolean removed = true;
        Uri uri = MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        int moviesDeleted = getContentResolver().delete(uri, null, null);
        if(moviesDeleted < 0) {
            removed = false;
            Toast.makeText(getBaseContext(), "Error: movie not removed from favorites", Toast.LENGTH_LONG).show();
        }
        Log.d("ContentProvider", "removed "+removed);
        return removed;
    }

    private boolean isFavoriteMovie(long movieId){
        boolean isFavorite = false;

        Cursor cursor = getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_MOVIE_ID+" = ?",
                new String[]{String.valueOf(movieId)},
                null
        );
        if(cursor != null && cursor.getCount() >= 1 && cursor.moveToFirst()) {
            isFavorite = true;
            cursor.close();
        }
        Log.d("ContentProvider", "isFavorite "+isFavorite);
        return isFavorite;
    }

    /**
     * allows user to add/remove the current movie to/from db.
     *
     * @param view
     */
    public void addRemoveFavorite(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));
        if(isFavorite){
            mFavoriteIv.setImageResource(R.mipmap.star_empty);
            if(removeFavoriteMovie(movie.getId()))
                isFavorite=false;
        } else {
            mFavoriteIv.setImageResource(R.mipmap.star);
            if(addFavoriteMovie(movie))
                isFavorite=true;
        }
    }
}
