package it.antedesk.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.antedesk.popularmovies.R;
import it.antedesk.popularmovies.model.Movie;

import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.IMAGE_URL;
import static it.antedesk.popularmovies.utilities.SupportVariablesDefinition.SIZE_W185;

/**
 * Created by Antedesk on 09/03/2018.
 * This class extends RecyclerView class to re-use the already created view.
 */

public class MovieViewAdapter extends RecyclerView.Adapter<MovieViewAdapter.MovieAdapterViewHolder> {

    private List<Movie> movieList;
    private Context parentContex;

    private final MovieViewAdapterOnClickHandler mClickHandler;

    // COMPLETED (1) Add an interface called ForecastAdapterOnClickHandler
    // COMPLETED (2) Within that interface, define a void method that access a String as a parameter
    /**
     * The interface that receives onClick messages.
     */
    public interface MovieViewAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }


    public MovieViewAdapter(MovieViewAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(parentContex);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, final int position) {
        Movie movie = movieList.get(position);
        String imageURL = IMAGE_URL+SIZE_W185+movie.getPosterPath();
        Picasso.with(parentContex)
                .load(imageURL)
                // image powered by Grace Baptist (http://gbchope.com/events-placeholder/)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_poster)
                .into(holder.mPosterIV);
    }

    @Override
    public int getItemCount() {
        if (movieList.isEmpty()) return 0;
        return movieList.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final ImageView mPosterIV;

        // Using view.findViewById to get a reference to this layout's ImageView
        // and save it to movie poster
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterIV = itemView.findViewById(R.id.grid_poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = movieList.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    public void setMoviesData(List<Movie> moviesData) {
        movieList = moviesData;
        notifyDataSetChanged();
    }
}
