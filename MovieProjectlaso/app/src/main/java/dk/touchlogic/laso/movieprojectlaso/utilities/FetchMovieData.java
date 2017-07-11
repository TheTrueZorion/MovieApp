package dk.touchlogic.laso.movieprojectlaso.utilities;

import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;

import dk.touchlogic.laso.movieprojectlaso.MainActivity;
import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;

/**
 * Created by lasse_sorensen on 11/07/2017.
 */

public class FetchMovieData extends AsyncTask<NetworkUtilities.MovieSearch, Void, Movie[]>{
    private final MainActivity.AsyncTaskCompleteListener<Movie[]> listener;

    public FetchMovieData(MainActivity.AsyncTaskCompleteListener<Movie[]> listener)
    {
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        listener.onStarting();
        super.onPreExecute();
    }

    @Override
    protected Movie[] doInBackground(NetworkUtilities.MovieSearch... params) {
        if(params.length == 0){
            return null;
        }
        Movie[] list = null;
        NetworkUtilities.MovieSearch search = params[0];
        //fetch data from data source
        URL movieRequestUrl = NetworkUtilities.buildURLMovieList(search);
        String jsonString;
        try {
            jsonString = NetworkUtilities.getResponseFromHttpUrl(movieRequestUrl);
            list = OpenMovieJsonUtils.getMovieListFromJson(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        listener.onTaskCompleted(movies);
    }
}