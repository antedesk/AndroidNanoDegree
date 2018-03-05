package it.antedesk.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.antedesk.popularmovies.R;
import it.antedesk.popularmovies.model.Movie;
import it.antedesk.popularmovies.utilities.NetworkUtils;

/**
 * Created by Antedesk on 04/03/2018.
 * This class was developed by exploiting the code provided in the official guide.
 * Link:
 * https://developer.android.com/guide/topics/ui/layout/gridview.html
 */

public class MovieImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> moviesList;
    private LayoutInflater layoutInflater;


    public MovieImageAdapter(Context c, List<Movie> movies) {
        mContext = c;
        moviesList = movies;
        layoutInflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return moviesList.size();
    }

    public Object getItem(int position) {
        return moviesList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.movie_item, null);
            imageView = (ImageView) convertView.findViewById(R.id.grid_poster_iv);
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = this.moviesList.get(position);

        String size = "w500";
        String imageURL = NetworkUtils.IMAGE_URL+size+movie.getPosterPath();
        Picasso.with(mContext)
                .load(imageURL)
                // image powered by Grace Baptist (http://gbchope.com/events-placeholder/)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);

        return imageView;
    }


}