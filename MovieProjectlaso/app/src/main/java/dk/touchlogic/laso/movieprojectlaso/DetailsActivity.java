package dk.touchlogic.laso.movieprojectlaso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;

public class DetailsActivity extends AppCompatActivity {
    private TextView title, plot, titleOriginal;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title = (TextView) findViewById(R.id.tv_movie_title);
        plot = (TextView) findViewById(R.id.tv_movie_plot);
        imageView = (ImageView) findViewById(R.id.iv_image_poster_details);
        titleOriginal = (TextView) findViewById(R.id.tv_title_original_and_date);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MainActivity.TAG)){
            Movie movie = intent.getParcelableExtra(MainActivity.TAG);
            setupView(movie);
        }
        else
        {
            setupErrorView();
        }


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
}
