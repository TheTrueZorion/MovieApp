package dk.touchlogic.laso.movieprojectlaso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;

/**
 * Created by lasse_sorensen on 06/07/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private Movie[] movieList;
    private final MovieAdapterOnClickHandler clickHandler;
    private final Context context;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, Context context) {
        this.clickHandler = clickHandler;
        this.context = context;
    }

    public void setMovieList(Movie[] movies) {
        movieList = movies;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(Movie movie);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_view;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem,viewGroup,false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList[position];
        fillMovieData(movie, holder);
    }

    @Override
    public int getItemCount() {
        if(movieList == null) return 0;
        return movieList.length;
    }

    private void fillMovieData(Movie movie, MovieViewHolder holder){
        Picasso.with(context)
               .load(movie.getPosterPath())
                .into(holder.theMovieImage);

    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView theMovieImage;

        public MovieViewHolder(View view){
            super(view);
            theMovieImage = view.findViewById(R.id.iv_image_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movieList[adapterPosition];
            clickHandler.onClick(movie);
        }
    }
}
