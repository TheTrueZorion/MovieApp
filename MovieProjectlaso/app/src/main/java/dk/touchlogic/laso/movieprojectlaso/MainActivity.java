package dk.touchlogic.laso.movieprojectlaso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.net.URL;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.utilities.NetworkUtilities;
import dk.touchlogic.laso.movieprojectlaso.utilities.OpenMovieJsonUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PREFS_NAME = "MyPrefsFile";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;
    private NetworkUtilities.MovieSearch sortBy = NetworkUtilities.MovieSearch.TOP;
    private GridLayoutManager manager;
    private String sorted = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        sorted = settings.getString("sorted", "null");
        switch (sorted){
            case "null": if (savedInstanceState != null) {
                sortBy = (NetworkUtilities.MovieSearch) savedInstanceState.getSerializable("sortBy");
            }break;
            case "top": sortBy = NetworkUtilities.MovieSearch.TOP; break;
            case "popular": sortBy = NetworkUtilities.MovieSearch.POPULAR;break;
            default:
        }
        initializeViews();
        loadMovieData();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if(sortBy == NetworkUtilities.MovieSearch.TOP)
            editor.putString("sorted","top");
        else
            editor.putString("sorted","popular");

        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("sortBy", sortBy);
    }

    private void initializeViews(){
        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_msg);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_movie_loader);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_show);
        movieAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(movieAdapter);
        int numColumns;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            numColumns = 3;
        }
        else{
            numColumns = 2;
        }
        manager = new GridLayoutManager(MainActivity.this,numColumns);
        recyclerView.setLayoutManager(manager);

    }

    private void loadMovieData(){
        new FetchMovieData().execute(sortBy);

    }
    private void showMovieView() {
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = MainActivity.this;
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(TAG, movie);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idChosenMenu = item.getItemId();

        switch (idChosenMenu){
            case R.id.action_sort_top_rated: {
                sortBy = NetworkUtilities.MovieSearch.TOP;
                recyclerView.setVisibility(View.INVISIBLE);
                loadMovieData();
                return true;
            }
            case R.id.action_sort_popular: {
                sortBy = NetworkUtilities.MovieSearch.POPULAR;
                recyclerView.setVisibility(View.INVISIBLE);
                loadMovieData();
                return true;
            }
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieData extends AsyncTask<NetworkUtilities.MovieSearch, Void, Movie[]>{

        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(View.VISIBLE);
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
            String jsonString = null;
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
            loadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null) {
                showMovieView();
                movieAdapter.setMovieList(movies);
            }
            else{
                showErrorMessage();
            }

        }
    }
}
