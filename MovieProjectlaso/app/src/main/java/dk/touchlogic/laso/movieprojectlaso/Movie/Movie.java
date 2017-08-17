package dk.touchlogic.laso.movieprojectlaso.Movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lasse_sorensen on 06/07/2017.
 */

public class Movie implements Parcelable{
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final String IMAGE_SIZE_LARGE = "w342";
    private final String plot;
    private final String title;
    private final String titleOriginal;
    private final double rating;
    private final String releaseDate;
    private final String posterPath;
    private final String posterPathLarge;
    private final String posterPathID;
    private final int movieID;
    private List<Trailer> trailers;
    private List<Review> reviews;


    public Movie(String plot, String title, String titleOriginal, double rating, String releaseDate, String posterPath, int movieID) {
        this.plot = plot;
        this.title = title;
        this.titleOriginal = titleOriginal;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = IMAGE_BASE_URL+IMAGE_SIZE+posterPath;
        this.posterPathLarge = IMAGE_BASE_URL+IMAGE_SIZE_LARGE+posterPath;
        this.posterPathID = posterPath;
        this.movieID = movieID;
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

    public String getPosterPathID() {
        return posterPathID;
    }

    public double getRating() { return rating; }

    public String getTitleOriginal() {
        return titleOriginal;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers){
        this.trailers = trailers;
    }
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
    }
    public int getMovieID() {
        return movieID;
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
        dest.writeString(posterPathID);
        dest.writeInt(movieID);
        dest.writeList(trailers);
        dest.writeList(reviews);


    }
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        this.plot = in.readString();
        this.title = in.readString();
        this.titleOriginal = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.posterPathLarge = in.readString();
        this.posterPathID = in.readString();
        this.movieID = in.readInt();
        in.readList(trailers,null);
        in.readList(reviews,null);
    }

}
