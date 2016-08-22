package org.buildmlearn.toolkit.matchtemplate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.matchtemplate.data.MatchContract.Matches;
import org.buildmlearn.toolkit.matchtemplate.data.MatchContract.MetaDetails;

/**
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief DatabaseHelper for match template's simulator.
 */
class MatchDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "matches.db";
    private static final int DATABASE_VERSION = 1;

    public MatchDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE_A = "CREATE TABLE " + Matches.TABLE_NAME + " (" +
                Matches._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Matches.MATCH_A + "  TEXT," +
                Matches.MATCH_B + "  TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE_A);

        final String SQL_CREATE__TABLE_B = "CREATE TABLE " + MatchContract.MetaDetails.TABLE_NAME + " (" +
                MatchContract.MetaDetails._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MetaDetails.TITLE + "  TEXT," +
                MetaDetails.FIRST_TITLE_TAG + "  TEXT," +
                MetaDetails.SECOND_TITLE_TAG + "  TEXT )";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE_B);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Matches.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MetaDetails.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}