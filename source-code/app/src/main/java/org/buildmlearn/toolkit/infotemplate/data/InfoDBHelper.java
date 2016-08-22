package org.buildmlearn.toolkit.infotemplate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.infotemplate.data.InfoContract.Info;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */

/**
 * @brief DatabaseHelper for info template's simulator.
 */
class InfoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mLearning.db";
    private static final int DATABASE_VERSION = 1;

    public InfoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE = "CREATE TABLE " + Info.TABLE_NAME + " (" +
                Info._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Info.TITLE + "  TEXT," +
                Info.DESCRIPTION + "  TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Info.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}