package dk.touchlogic.laso.movieprojectlaso.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import dk.touchlogic.laso.movieprojectlaso.MainActivity;
import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.data.MovieContract;

/**
 * Created by Lasse on 17-08-2017.
 */

public class FetchMovieDataFromDataBase extends AsyncTask<Void, Void, Movie[]>{
    private final static String TAG = FetchMovieDataFromDataBase.class.getSimpleName();
    private final MainActivity.AsyncTaskCompleteListener<Movie[]> listener;
    private final Context context;

    public FetchMovieDataFromDataBase(Context context, MainActivity.AsyncTaskCompleteListener<Movie[]> listener)
    {
        this.listener = listener;
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        listener.onStarting();
        super.onPreExecute();
    }

    @Override
    protected Movie[] doInBackground(Void... voids) {
        try {
            String sortBy = MovieContract.MovieEntry._ID;
            Cursor cursor = context.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    sortBy + " ASC"
            );
            return extractMovies(cursor);

        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        listener.onTaskCompleted(movies);
    }

    private Movie[] extractMovies(Cursor cursor) {
        Movie[] movies = new Movie[cursor.getCount()];
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        int i = 0;
        while(cursor.moveToNext()){
            int indexID = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            int indexTitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int indexTitleOriginal = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE_ORIGINAL);
            int indexPlot = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);
            int indexReleased = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASED);
            int indexRating = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
            int indexPosterPath = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

            int movieID = cursor.getInt(indexID);
            String title = cursor.getString(indexTitle);
            String titleOriginal = cursor.getString(indexTitleOriginal);
            String plot = cursor.getString(indexPlot);
            String released = cursor.getString(indexReleased);
            float rating = cursor.getInt(indexRating);
            String posterPath = cursor.getString(indexPosterPath);
            movies[i] = new Movie(plot,title,titleOriginal,rating,released,posterPath,movieID);
            i++;
        }
        cursor.close();
        return movies;
    }
}
