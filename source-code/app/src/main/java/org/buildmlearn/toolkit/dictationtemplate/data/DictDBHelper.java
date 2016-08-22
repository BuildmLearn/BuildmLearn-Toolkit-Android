package org.buildmlearn.toolkit.dictationtemplate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.dictationtemplate.data.DictContract.Dict;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief DatabaseHelper for dictation template's simulator.
 */

class DictDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dictation.db";
    private static final int DATABASE_VERSION = 1;

    public DictDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE = "CREATE TABLE " + Dict.TABLE_NAME + " (" +
                Dict._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Dict.TITLE + "  TEXT," +
                Dict.PASSAGE + "  TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Dict.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}