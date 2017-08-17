package dk.touchlogic.laso.movieprojectlaso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.utilities.FetchMovieDataFromDataBase;
import dk.touchlogic.laso.movieprojectlaso.utilities.NetworkUtilities;
import dk.touchlogic.laso.movieprojectlaso.utilities.FetchMovieData;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREFS_NAME = "MyPrefsFile";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;
    private NetworkUtilities.MovieSearch sortBy = NetworkUtilities.MovieSearch.TOP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String sorted = settings.getString("sorted", "null");
        switch (sorted){
            case "null": if (savedInstanceState != null) {
                sortBy = (NetworkUtilities.MovieSearch) savedInstanceState.getSerializable("sortBy");
            }break;
            case "top":
                sortBy = NetworkUtilities.MovieSearch.TOP; break;
            case "popular":
                sortBy = NetworkUtilities.MovieSearch.POPULAR;break;
            case "favorite":
                sortBy = NetworkUtilities.MovieSearch.FAVORITES;break;
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
        switch (sortBy){
            case TOP: editor.putString("sorted","top");break;
            case POPULAR:editor.putString("sorted","popular");break;
            case FAVORITES:editor.putString("sorted","favorite");break;
            default:
        }
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
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, numColumns);
        recyclerView.setLayoutManager(manager);

    }

    private void loadMovieData(){
        if(sortBy == NetworkUtilities.MovieSearch.FAVORITES)
            new FetchMovieDataFromDataBase(this,new FetchMovieDataListener()).execute();
        else
            new FetchMovieData(new FetchMovieDataListener()).execute(sortBy);

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
            case R.id.action_sort_favorite:
                sortBy = NetworkUtilities.MovieSearch.FAVORITES;
                recyclerView.setVisibility(View.INVISIBLE);
                loadMovieData();
                return true;


            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public interface AsyncTaskCompleteListener<T>{
        void onStarting();
        void onTaskCompleted(T results);
    }

    public class FetchMovieDataListener implements AsyncTaskCompleteListener<Movie[]>{
        @Override
        public void onStarting() {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTaskCompleted(Movie[] results) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if(results != null) {
                showMovieView();
                movieAdapter.setMovieList(results);
            }
            else{
                showErrorMessage();
            }
        }
    }
}
