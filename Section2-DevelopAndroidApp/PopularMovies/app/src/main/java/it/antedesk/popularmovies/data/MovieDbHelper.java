package it.antedesk.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import it.antedesk.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Antedesk on 02/05/2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "+
                MovieEntry.TABLE_NAME+" ("+
                MovieEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieEntry.COLUMN_MOVIE_ID +" INTEGER NOT NULL,"+
                MovieEntry.COLUMN_TITLE +" INTEGER NOT NULL,"+
                MovieEntry.COLUMN_RELEASE_DATE +" TEXT NOT NULL,"+
                MovieEntry.COLUMN_POSTER_PATH +" TEXT NOT NULL,"+
                MovieEntry.COLUMN_VOTE_AVARAGE +" INTEGER NOT NULL,"+
                MovieEntry.COLUMN_OVERVIEW +" TEXT NOT NULL,"+
                MovieEntry.COLUMN_VOTE_COUNT +" INTEGER NOT NULL"+
                ");";
        Log.d("TAG", "Creating table"+SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        Log.d("TAG", "Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
