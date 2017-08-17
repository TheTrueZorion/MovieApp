package dk.touchlogic.laso.movieprojectlaso.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import dk.touchlogic.laso.movieprojectlaso.data.MovieContract.MovieEntry;

/**
 * Created by Lasse on 15-08-2017.
 */

class MovieDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "moviesDb.db";
    private static final int VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                  + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_PLOT          + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE         + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE_ORIGINAL+ " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH   + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASED      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING        + " FLOAT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
