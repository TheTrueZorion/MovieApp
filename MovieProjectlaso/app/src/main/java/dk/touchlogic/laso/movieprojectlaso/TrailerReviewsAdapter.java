package dk.touchlogic.laso.movieprojectlaso;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dk.touchlogic.laso.movieprojectlaso.Movie.Movie;
import dk.touchlogic.laso.movieprojectlaso.Movie.MovieInformation;


/**
 * Created by Lasse on 15-08-2017.
 */

public class TrailerReviewsAdapter extends RecyclerView.Adapter<TrailerReviewsAdapter.TrailerReviewViewHolder> {

    private ArrayList<MovieInformation> movieInformations;
    private final TrailerReviewsAdapterOnClickHandler clickHandler;
    private Movie movie;

    public interface TrailerReviewsAdapterOnClickHandler{
        void onClick(MovieInformation movieInformation);
    }
    public TrailerReviewsAdapter(TrailerReviewsAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setUrlList(Movie movie) {
        this.movie = movie;
        movieInformations = new ArrayList<>();
        movieInformations.addAll(movie.getReviews());
        movieInformations.addAll(movie.getTrailers());
        notifyDataSetChanged();
    }
    @Override
    public TrailerReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem,viewGroup,false);

        return new TrailerReviewsAdapter.TrailerReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerReviewViewHolder holder, int position) {

        if(position < movie.getReviews().size()) {
            String author = movie.getReviews().get(position).getAuthor();
            String reviewText = "Review " + String.valueOf(position + 1) + " - " + author;
            SpannableStringBuilder str = new SpannableStringBuilder(reviewText);
            str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD_ITALIC), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.urlView.setText(str);
        }
        else{
            String name = movie.getTrailers().get(position-movie.getReviews().size()).getName();
            String trailerText = "Trailer " + String.valueOf(position+1-movie.getReviews().size()) + " - " + name;
            SpannableStringBuilder str = new SpannableStringBuilder(trailerText);
            str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD_ITALIC), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.urlView.setText(str);
        }
    }

    @Override
    public int getItemCount() {
        if(movieInformations == null) return 0;
        return movieInformations.size();
    }

    public class TrailerReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView urlView;
        public TrailerReviewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            urlView = itemView.findViewById(R.id.tv_trailer_review);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieInformation info = movieInformations.get(adapterPosition);
            clickHandler.onClick(info);
        }
    }
}
