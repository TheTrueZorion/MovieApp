package dk.touchlogic.laso.movieprojectlaso.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.Movie.Review;
import dk.touchlogic.laso.movieprojectlaso.Movie.Trailer;

/**
 * Created by lasse_sorensen on 07/07/2017.
 */

public class OpenMovieJsonUtils {

    private static boolean checkErrorCode(int errorCode){

        switch (errorCode) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                return false;
            default:
                    /* Server probably down */
                return false;
        }
    }

    private static void updateMovieReview(JSONObject reviewJson, Movie movie)
    throws JSONException{
        JSONArray reviewArray = reviewJson.getJSONArray("results");
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i< reviewArray.length(); i++){
            JSONObject review = reviewArray.getJSONObject(i);
            String reviewUrl = review.getString("url");
            String reviewAuthor = review.getString("author");
            reviewList.add(new Review(reviewAuthor,reviewUrl));
        }
        movie.setReviews(reviewList);
    }

    private static void updateMovieTrailers(JSONObject trailersJson, Movie movie)
            throws JSONException{
        JSONArray trailersArray = trailersJson.getJSONArray("results");
        List<Trailer> trailersList = new ArrayList<>();
        for (int i = 0; i< trailersArray.length(); i++){
            JSONObject trailer = trailersArray.getJSONObject(i);
            String trailerUrl = trailer.getString("key");
            String trailerTitle = trailer.getString("name");

            trailersList.add(new Trailer(trailerTitle,trailerUrl));
        }
        movie.setTrailers(trailersList);
    }

    public static void updateTrailerAndReviewList(Movie movie, String jsonReview, String jsonTrailers)
        throws JSONException{
            final String OWM_MESSAGE_CODE = "cod";
            JSONObject reviewJson = new JSONObject(jsonReview);
            JSONObject trailersJson = new JSONObject(jsonTrailers);

            if (reviewJson.has(OWM_MESSAGE_CODE) && trailersJson.has(OWM_MESSAGE_CODE)) {
                int errorCodeVideos = reviewJson.getInt(OWM_MESSAGE_CODE);
                int errorCodeReview = trailersJson.getInt(OWM_MESSAGE_CODE);

                if(!checkErrorCode(errorCodeReview) || !checkErrorCode(errorCodeVideos))
                    return;
            }
        updateMovieReview(reviewJson, movie);
        updateMovieTrailers(trailersJson, movie);
    }
    public static Movie[] getMovieListFromJson(String movieJsonStr)
        throws JSONException{
        Movie[] movieList;
        final String OWM_MESSAGE_CODE = "cod";
        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            if(!checkErrorCode(errorCode))
                return null;
        }

        JSONArray movieArray = movieJson.getJSONArray("results");
        movieList = new Movie[movieArray.length()];

        for(int i = 0; i<movieArray.length();i++){
            String title;
            String titleOriginal;
            String poster_url;
            String plot;
            double rating;
            String releaseData;
            int movieID;

            JSONObject jsonMovie = movieArray.getJSONObject(i);
            title = jsonMovie.get("title").toString();
            titleOriginal = jsonMovie.get("original_title").toString();
            releaseData = jsonMovie.getString("release_date");
            poster_url = jsonMovie.get("poster_path").toString();
            rating = jsonMovie.getDouble("vote_average");
            plot = jsonMovie.getString("overview");
            movieID = jsonMovie.getInt("id");

            Movie movie = new Movie(plot,title, titleOriginal, rating, releaseData, poster_url, movieID);
            movieList[i] = movie;
        }


    return movieList;
    }

}
