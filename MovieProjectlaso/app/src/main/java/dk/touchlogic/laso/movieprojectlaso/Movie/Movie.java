package dk.touchlogic.laso.movieprojectlaso.Movie;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by lasse_sorensen on 06/07/2017.
 */

public class Movie implements Parcelable{
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final String IMAGE_SIZE_LARGE = "w342";
    private String plot;
    private String title;
    private String titleOriginal;
    private double rating;
    private String releaseDate;
    private String posterPath;
    private String posterPathLarge;

    public Movie(String plot, String title, String titleOriginal, double rating, String releaseDate, String posterPath) {
        this.plot = plot;
        this.title = title;
        this.titleOriginal = titleOriginal;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = IMAGE_BASE_URL+IMAGE_SIZE+posterPath;
        this.posterPathLarge = IMAGE_BASE_URL+IMAGE_SIZE_LARGE+posterPath;
    }



    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPosterPathLarge() {
        return posterPathLarge;
    }

    public double getRating() { return rating; }

    public String getTitleOriginal() {
        return titleOriginal;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(plot);
        dest.writeString(title);
        dest.writeString(titleOriginal);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(posterPathLarge);


    }
    static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel in) {
        this.plot = in.readString();
        this.title = in.readString();
        this.titleOriginal = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.posterPathLarge = in.readString();
    }

}
