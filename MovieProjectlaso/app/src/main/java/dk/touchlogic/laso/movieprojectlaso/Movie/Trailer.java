package dk.touchlogic.laso.movieprojectlaso.Movie;

/**
 * Created by Lasse on 16-08-2017.
 */

public class Trailer extends MovieInformation{
    private final String name;
    private final String url;

    public Trailer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        if(name.length()<30)
            return name;
        else
            return name.substring(0,30)+"...";
    }

    @Override
    public boolean isTrailer() {
        return true;
    }
}
