package it.antedesk.popularmovies;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.view.Window;
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
import it.antedesk.popularmovies.adapter.ReviewViewAdapter;
import it.antedesk.popularmovies.adapter.TrailerViewAdapter;
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

    // UI elements
    @BindView(R.id.poster_iv) ImageView mPosterIv;
    @BindView(R.id.release_date_tv) TextView mReleaseDateTv;
    @BindView(R.id.vote_average_tv) TextView mRatingTv;
    @BindView(R.id.overview_tv) TextView mPlotSynopsisTv;
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

    private Movie movie;

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
        mReleaseDateTv.setText(movie.getReleaseDate());
        mRatingTv.setText(String.format("%s / 10", movie.getVoteAvarage()));
        mPlotSynopsisTv.setText(movie.getOverview());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return movie;
            }
        };
    }

    private void showMoviesDataView(Movie movie){
        populateUI(movie);
        mReviewViewAdapter.setReviewsData(movie.getReviews());
        mTrailerViewAdapter.setTrailersData(movie.getTrailers());
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
            if(movie!=null)
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);
        TextView reviewerName = dialog.findViewById(R.id.reviewer_name_tv);
        TextView reviewContent = dialog.findViewById(R.id.review_content_tv);
        reviewerName.setText(selectedReview.getAuthor());
        reviewContent.setText(selectedReview.getContent());
        dialog.show();
    }

    @Override
    public void onClick(Trailer selectedTrailer) {
        Log.d(MOVIE_TAG, selectedTrailer.toString());
        if (mYuoTubePlayerFrag != null && mYouTubePlayer != null) {
            //load the selected video
            mYouTubePlayer.cueVideo(selectedTrailer.getKey());
            NestedScrollView scroll = findViewById(R.id.scroll);
            scroll.scrollTo(0, 0);;
            AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
            appBarLayout.setExpanded(true);
        }
    }
}
