package org.buildmlearn.toolkit.learnspelling.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.learnspelling.data.SpellContract.Spellings;

/**
 * Created by Anupam (opticod) on 1/6/16.
 */

/**
 * @brief DatabaseHelper for spell template's simulator.
 */
class SpellDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "learn_spell.db";
    private static final int DATABASE_VERSION = 1;

    public SpellDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE_A = "CREATE TABLE " + Spellings.TABLE_NAME + " (" +
                Spellings._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Spellings.WORD + "  TEXT," +
                Spellings.MEANING + "  TEXT," +
                Spellings.ANSWERED + "  INTEGER," +
                Spellings.ATTEMPTED + "  INTEGER )";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE_A);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Spellings.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}