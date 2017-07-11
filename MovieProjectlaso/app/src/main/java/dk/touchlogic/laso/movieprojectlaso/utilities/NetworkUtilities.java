package dk.touchlogic.laso.movieprojectlaso.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import dk.touchlogic.laso.movieprojectlaso.BuildConfig;
/**
 * Created by lasse_sorensen on 07/07/2017.
 */

public class NetworkUtilities{

    private static final String API_KEY= BuildConfig.THE_MOVIE_DB_API_TOKEN;
    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String TOP_RATED ="top_rated";
    private static final String POPULAR_MOVIE ="popular";
    private static final String QUERY ="?api_key=";

    public enum MovieSearch implements Serializable{TOP,POPULAR}

    public static URL buildURLMovieList(MovieSearch search){
        String urlCompleted;
        URL url = null;
        try {
            switch (search) {
                case POPULAR: {
                    urlCompleted = MOVIES_BASE_URL+POPULAR_MOVIE+QUERY+API_KEY;
                    break;
                }
                case TOP: {
                    urlCompleted = MOVIES_BASE_URL+TOP_RATED+QUERY+API_KEY;
                    break;
                }
                default: urlCompleted = MOVIES_BASE_URL+POPULAR_MOVIE+QUERY+API_KEY; break;

            }
            url = new URL(urlCompleted);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
