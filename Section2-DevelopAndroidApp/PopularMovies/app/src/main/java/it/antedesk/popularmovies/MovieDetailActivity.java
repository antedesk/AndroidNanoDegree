package it.antedesk.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.utilities.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity {

    // Basic path of image url
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/";

    // TAG for intent and log
    protected static final String MOVIE_TAG = "selectedMovie";

    // UI elements
    private ImageView mPosterIv;
    private TextView mReleaseDateTv;
    private TextView mRatingTv;
    private TextView mPlotSynopsisTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // finding UI elements
        mPosterIv = findViewById(R.id.poster_iv);
        mReleaseDateTv = findViewById(R.id.release_date_tv);
        mRatingTv = findViewById(R.id.vote_average_tv);
        mPlotSynopsisTv = findViewById(R.id.overview_tv);

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
        Movie selectedMovie = intent.getParcelableExtra(MOVIE_TAG);

        // populate the UI
        populateUI(selectedMovie);
    }

    // This method is used to populate the UI by using the given movie
    private void populateUI(Movie movie) {
        setTitle(movie.getTitle());
        String size = "w500";
        String imageURL = IMAGE_URL+size+movie.getPosterPath();
        Picasso.with(this)
                .load(imageURL)
                // image powered by Grace Baptist (http://gbchope.com/events-placeholder/)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(mPosterIv);
        mReleaseDateTv.setText(movie.getReleaseDate());
        mRatingTv.setText(""+movie.getVoteAvarage());
        mPlotSynopsisTv.setText(movie.getOverview());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
