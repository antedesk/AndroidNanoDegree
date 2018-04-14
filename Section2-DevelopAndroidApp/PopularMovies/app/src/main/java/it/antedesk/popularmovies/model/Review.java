package it.antedesk.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Antedesk on 10/03/2018.
 * This class model the review movie concept
 */

public class Review  implements Parcelable {

    // attributes
    private long id;
    private String author;
    private String content;

    // constructor
    public Review(long id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public Review(Parcel in) {
        id = in.readLong();
        author = in.readString();
        content = in.readString();
    }

    // getter and setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(author);
        dest.writeString(content);
    }


    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
