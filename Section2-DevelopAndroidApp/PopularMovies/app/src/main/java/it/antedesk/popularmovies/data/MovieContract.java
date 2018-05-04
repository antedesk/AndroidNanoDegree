package it.antedesk.popularmovies.data;

/**
 * Created by Antedesk on 02/05/2018.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String AUTHORITY = "it.antedesk.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movies" directory
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME="movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_VOTE_AVARAGE = "voteAvarage";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
    }

}
