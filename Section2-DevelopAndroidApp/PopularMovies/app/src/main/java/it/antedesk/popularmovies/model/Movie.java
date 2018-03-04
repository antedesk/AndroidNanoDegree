package it.antedesk.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Antedesk on 01/03/2018.
 *
 * This Class models the entity Movie for this object identifying it through the required
 * attributes:
 * - title is the title of the film,
 * - release date is day when the film was released,
 * - movie poster is represented by posterPath that is the path where the poster can be retrieved
 * - vote average represents the mean users evaluation,
 * - plot synopsis is represented by the overview attribute.
 *
 * This class implements Parcelable interface in order to save the state of the application
 * in the savedInstanceState boundle and to pass the objects between two activities
 */

public class Movie implements Parcelable {

    // define the attributes of Movie object
    private long id;
    private String title;
    private String releaseDate;
    private String posterPath;
    private double voteAvarage;
    private String overview;

    // class constructor
    public Movie(){}

    public Movie(long id, String title, String releaseDate, String posterPath,
                 double voteAvarage, String overview) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAvarage = voteAvarage;
        this.overview = overview;
    }

    // Getter and Setter for this class

    protected Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAvarage = in.readDouble();
        overview = in.readString();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAvarage() {
        return voteAvarage;
    }

    public void setVoteAvarage(int voteAvarage) {
        this.voteAvarage = voteAvarage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    // I added the toString method because it's always usefull for debugging
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", voteAvarage=" + voteAvarage +
                ", overview='" + overview + '\'' +
                '}';
    }


    // implementing Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(voteAvarage);
        dest.writeString(overview);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
