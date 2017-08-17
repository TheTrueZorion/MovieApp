package dk.touchlogic.laso.movieprojectlaso.Movie;

/**
 * Created by Lasse on 16-08-2017.
 */

public class Review extends MovieInformation{
    private final String author;
    private final String url;

    public Review(String author, String url) {
        this.author = author;
        this.url = url;
    }

    public String getAuthor() {
        if(author.length()<30)
            return author;
        else
            return author.substring(0,30)+"...";
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean isTrailer() {
        return false;
    }
}
