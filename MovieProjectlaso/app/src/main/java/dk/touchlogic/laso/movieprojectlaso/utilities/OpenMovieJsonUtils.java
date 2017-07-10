package dk.touchlogic.laso.movieprojectlaso.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;

/**
 * Created by lasse_sorensen on 07/07/2017.
 */

public class OpenMovieJsonUtils {

    public static Movie[] getMovieListFromJson(String movieJsonStr)
        throws JSONException{
        Movie[] movieList = null;
        final String OWM_MESSAGE_CODE = "cod";
        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
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

            JSONObject jsonMovie = movieArray.getJSONObject(i);
            title = jsonMovie.get("title").toString();
            titleOriginal = jsonMovie.get("original_title").toString();
            releaseData = jsonMovie.getString("release_date");
            poster_url = jsonMovie.get("poster_path").toString();
            rating = jsonMovie.getDouble("vote_average");
            plot = jsonMovie.getString("overview");


            Movie movie = new Movie(plot,title, titleOriginal, rating, releaseData, poster_url);
            movieList[i] = movie;

        }


    return movieList;
    }

}
