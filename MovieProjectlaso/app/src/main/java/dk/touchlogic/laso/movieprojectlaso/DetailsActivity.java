package dk.touchlogic.laso.movieprojectlaso;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.net.URL;
import java.util.List;
import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.Movie.MovieInformation;
import dk.touchlogic.laso.movieprojectlaso.Movie.Review;
import dk.touchlogic.laso.movieprojectlaso.Movie.Trailer;
import dk.touchlogic.laso.movieprojectlaso.data.MovieContract;
import dk.touchlogic.laso.movieprojectlaso.utilities.NetworkUtilities;
import dk.touchlogic.laso.movieprojectlaso.utilities.OpenMovieJsonUtils;

public class DetailsActivity extends AppCompatActivity
        implements TrailerReviewsAdapter.TrailerReviewsAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{
    private TextView title, plot, titleOriginal;
    private ImageView imageView;
    private Movie movie;
    private TrailerReviewsAdapter trailerReviewsAdapter;
    private Button makeFavorite;
    private Cursor cursor = null;
    private static final int MOVIE_LOADER_ID = 125;
    private final static String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initializeViews();

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }
    private void initializeViews(){
        title = (TextView) findViewById(R.id.tv_movie_title);
        plot = (TextView) findViewById(R.id.tv_movie_plot);
        imageView = (ImageView) findViewById(R.id.iv_image_poster_details);
        titleOriginal = (TextView) findViewById(R.id.tv_title_original_and_date);
        makeFavorite = (Button) findViewById(R.id.action_make_favorite);
        makeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavorite();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        trailerReviewsAdapter = new TrailerReviewsAdapter(this);
        recyclerView.setAdapter(trailerReviewsAdapter);

        GridLayoutManager manager = new GridLayoutManager(DetailsActivity.this, 1);
        recyclerView.setLayoutManager(manager);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MainActivity.TAG)){
            movie = intent.getParcelableExtra(MainActivity.TAG);
            setupView(movie);
            loadTrailersAndReviews();
        }
        else
            setupErrorView();

    }
    private void loadTrailersAndReviews(){
        new FetchTrailersAndReviews().execute();
    }
    private void setupView(Movie movie){
        plot.setText(movie.getPlot());
        title.setText(movie.getTitle());
        titleOriginal.setText("original title: "+movie.getTitleOriginal()+
                "\nScoring: "+String.valueOf(movie.getRating()) +
                "\nreleased: "+movie.getReleaseDate());


        Picasso.with(this)
                .load(movie.getPosterPathLarge())
                .into(imageView);
    }
    private void setupErrorView(){
        title.setText(R.string.error_text);
    }

    @Override
    public void onClick(MovieInformation movieInformation) {
        Intent browserIntent;
        if(movieInformation.isTrailer()) {
            Trailer trailer = (Trailer) movieInformation;

            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getUrl()));
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getUrl()));
            try {
                startActivity(youtubeIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(browserIntent);
            }
        }
        else{
            Review review = (Review) movieInformation;
            browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(review.getUrl()));
            startActivity(browserIntent);
        }
    }


    private class FetchTrailersAndReviews extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            //fetch data from data source
            List<URL> movieRequestUrl = NetworkUtilities.buildReviewAndVideosURL(movie.getMovieID());
            String jsonStringReview;
            String jsonStringTrailers;
            try {
                jsonStringReview = NetworkUtilities.getResponseFromHttpUrl(movieRequestUrl.get(0));
                jsonStringTrailers = NetworkUtilities.getResponseFromHttpUrl(movieRequestUrl.get(1));
                OpenMovieJsonUtils.updateTrailerAndReviewList(movie,jsonStringReview,jsonStringTrailers);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            trailerReviewsAdapter.setUrlList(movie);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor movieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(movieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {
                String selection = MovieContract.MovieEntry._ID + " = ?";
                String[] selectionArgs = {String.valueOf(movie.getMovieID())};
                try {
                    return getContentResolver().query(
                            MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            selection,
                            selectionArgs,
                            null
                    );

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(Cursor data) {
                movieData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        if(checkMovieInDataBase())
            showFavoriteChosen();
        else
            showFavoriteUnchosen();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }
    private void updateFavorite() {

        if(checkMovieInDataBase()) {

            Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getMovieID())).build();
            getContentResolver().delete(uri,null,null);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, DetailsActivity.this);


        }
        else{
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getPlot());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE_ORIGINAL, movie.getTitleOriginal());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASED, movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPathID());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
            movieValues.put(MovieContract.MovieEntry._ID,movie.getMovieID());

            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, DetailsActivity.this);
        }
        cursor.close();
    }

    private boolean checkMovieInDataBase(){
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            int idOfMovie = cursor.getInt(idIndex);

            return idOfMovie == movie.getMovieID();
        }
        return false;
    }
    private void showFavoriteChosen(){
        makeFavorite.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow));
        makeFavorite.setText(R.string.btn_favorism_activated);
    }
    private void showFavoriteUnchosen(){
        makeFavorite.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
        makeFavorite.setText(R.string.btn_favorism_deactivated);
    }
}
